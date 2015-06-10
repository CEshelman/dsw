package com.ibm.dsw.quote.common.util;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

public class OpptNumServiceLock {
	protected static final LogContext logger = LogContextFactory.singleton()
			.getLogContext();

	private static final OpptNumServiceLock instance = new OpptNumServiceLock();

	// record lock information about current thread lock: nest number, locking
	// spending time
	private static ThreadLocal<LockCounter> currentThreadNestCount = new ThreadLocal<LockCounter>();

	// lightweight synchronized, to replace java synchronized
	private static Lock locksync = new ReentrantLock();

	// when quietMode = ture, any locking action will return exception directly,
	// means current SCODS is unavaliable.
	private static volatile boolean quietMode = false;

	// when quietMode = true, retry once in every quietModeRetryInterval time
	// to detect if SCODS is working
	private static long quietModeRetryInterval = 900;//seconds

	// in quiet mode, update quietModeRefreshTimestamp to current time stamp
	// before retry
	private static volatile long quietModeRefreshTimestamp = 0;// seconds

	// the last time when a thread get the lock successfully (means lockCount++)
	private static volatile long lastLockTime = System.currentTimeMillis();

	// the lock number which has already been used in lock pool,
	// when a thread get a lock successfully, the lockCount will increase 1
	// until reach the upper limit: maxLockCount
	public volatile static int lockCount = 0;

	// waitingCount indicate how many threads are waiting for opportunity to get
	// lock from lock pool
	// waitingCount also has a upper limit: maxWaitingCount
	public volatile static int waitingCount = 0;

	// max number of lock could be provided by lock pool
	public static int maxLockCount = 6;

	// max number of threads could be waiting lock to be released into lock pool
	public static int maxWaitingCount = 15;

	// waiting timeout seconds
	public static int waitingTimeout = 30;// seconds

	// if lock failed to be released in <quietModeDetectTime> seconds, that
	// means SCODS is having issue now
	public static int quietModeDetectTime = 300;//seconds

	// refresh configuration interval
	public volatile static int configReloadInterval = 1800; //seconds

	// the last time the configuration being refreshed
	public volatile static long configReloadTimestamp = 0;

	public static OpptNumServiceLock getSingle() {
		return OpptNumServiceLock.instance;
	}

	/**
	 * acquire a lock, if failed, throws
	 * OpptNumServiceLockAcquireFailedException this method have to be try catch
	 * 
	 * @throws OpptNumServiceLockAcquireFailedException
	 */
	public static void lock() throws OpptNumServiceLockAcquireFailedException {
		OpptNumServiceLock.getSingle().locking();
	}

	/**
	 * release a lock after using, this method has to be in finally sectionF
	 */
	public static void unLock() {
		OpptNumServiceLock.getSingle().unLocking();
	}

	public boolean getQuietMode() {
		return quietMode;
	}

	private void reloadConfig() {

		boolean needRefreshConfig = false;
		try {
			locksync.lock();
			if ((System.currentTimeMillis() - configReloadTimestamp) > configReloadInterval * 1000) {
				needRefreshConfig = true;
				configReloadTimestamp = System.currentTimeMillis();
			}
		} catch (Throwable t) {
			// do nothing
		} finally {
			locksync.unlock();
		}
		if (needRefreshConfig) {
			info("OpptNumServiceLock reloading config");
			try {
				Map<String, String> config = QuoteProcessFactory.singleton()
						.create().refreshSCODSConnPoolConfig();
				String value = config.get("PORETRYINTERVAL");
				if (StringUtils.isNotBlank(value)) {
					quietModeRetryInterval = Integer.valueOf(value.trim());
					info("OpptNumServiceLock config reload quietModeRetryInterval -> "
							+ quietModeRetryInterval);
				}
				value = config.get("POLOCKPOOLMAX");
				if (StringUtils.isNotBlank(value)) {
					maxLockCount = Integer.valueOf(value.trim());
					info("OpptNumServiceLock config reload maxLockCount -> "
							+ maxLockCount);
				}
				value = config.get("POWAITINGMAX");
				if (StringUtils.isNotBlank(value)) {
					maxWaitingCount = Integer.valueOf(value.trim());
					info("OpptNumServiceLock config reload maxWaitingCount -> "
							+ maxWaitingCount);
				}
				value = config.get("POWAITTIMEOUT");
				if (StringUtils.isNotBlank(value)) {
					waitingTimeout = Integer.valueOf(value.trim());
					info("OpptNumServiceLock config reload waitingTimeout -> "
							+ waitingTimeout);
				}
				value = config.get("POQUIETDECTTIME");
				if (StringUtils.isNotBlank(value)) {
					quietModeDetectTime = Integer.valueOf(value.trim());
					info("OpptNumServiceLock config reload quietModeDetectTime -> "
							+ quietModeDetectTime);
				}
				value = config.get("POCONFIGRELOADINTER");
				if (StringUtils.isNotBlank(value)) {
					configReloadInterval = Integer.valueOf(value.trim());
					info("OpptNumServiceLock config reload configReloadInterval -> "
							+ configReloadInterval);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public void locking() throws OpptNumServiceLockAcquireFailedException {
		debug("OpptNumServiceLock Thread[" + Thread.currentThread() + "("
				+ Thread.currentThread().getId() + ")] is acquiring lock...");

		reloadConfig();

		LockCounter count = currentThreadNestCount.get();
		if (count == null) {// the first time current thread is acquiring lock,
							// not nest invoking
			count = new LockCounter();
			currentThreadNestCount.set(count);
		} else if (count.getCount() > 0) {// if count>0, means nest invoking,
											// don't need to acquire new lock
			count.inc();
			return;
		}
		boolean waiting = false;// this is a flag to indicate if this locking
								// request has to wait when lock pool is empty
		try {
			locksync.lock();

			if (!quietMode
					&& lockCount > 0
					&& ((System.currentTimeMillis() - lastLockTime) > quietModeDetectTime * 1000)) {
				// if lock failed to be released to lock pool
				// timely(>quietModeDetectTime), SCODS maybe experiencing issue,
				// our lock pool needs to become quiet mode, which means any
				// locking request will get failed to avoid high DB connection
				// disaster.
				quietMode = true;
				quietModeRefreshTimestamp = System.currentTimeMillis();
				info("OpptNumServiceLock Thread[" + Thread.currentThread()
						+ "(" + Thread.currentThread().getId()
						+ ")] is going into quiet mode. lastLockTime -> "
						+ lastLockTime + " currentTimeMillis -> "
						+ System.currentTimeMillis());
			}

			if (quietMode
					&& ((System.currentTimeMillis() - quietModeRefreshTimestamp) > quietModeRetryInterval * 1000)) {
				// in quiet mode, have to detect if SCODS is working again in
				// each quietModeRetryInterval seconds
				lockCount++;
				count.inc();
				lastLockTime = System.currentTimeMillis();
				quietModeRefreshTimestamp = System.currentTimeMillis();
				info("OpptNumServiceLock Thread[" + Thread.currentThread()
						+ "(" + Thread.currentThread().getId()
						+ ")] is retrying once in quiet mode");
				return;
			} else if (quietMode) {
				throw new OpptNumServiceLockAcquireFailedException(
						"OpptNumServiceLock is in quiet mode");
			}

			if (waitingCount >= maxWaitingCount) {
				// waiting pool is full, return exception directly
				throw new OpptNumServiceLockAcquireFailedException(
						"OpptNumServiceLock over max waiting count");
			}

			if (waitingCount > 0 || lockCount >= maxLockCount) {
				// lock pool is empty, thread has to wait until lock is released
				waitingCount++;
				waiting = true;
			} else {
				// thread is getting lock successfully
				lockCount++;
				count.inc();
				lastLockTime = System.currentTimeMillis();
				debug("OpptNumServiceLock Thread[" + Thread.currentThread()
						+ "(" + Thread.currentThread().getId()
						+ ")] has got lock successfully");
				return;
			}

		} catch (OpptNumServiceLockAcquireFailedException e) {
			throw e;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		} finally {
			locksync.unlock();
		}

		if (waiting) {
			info("OpptNumServiceLock Thread[" + Thread.currentThread() + "("
					+ Thread.currentThread().getId() + ")] is waiting lock...");
			long waitingStartTime = System.currentTimeMillis();
			for (; !quietMode;) {// loop to get available lock, unless getting
									// into quiet mode
				try {
					locksync.lock();
					if (lockCount < maxLockCount) {
						lockCount++;
						count.inc();
						lastLockTime = System.currentTimeMillis();
						waitingCount--;
						if (waitingCount < 0)
							waitingCount = 0;
						info("OpptNumServiceLock Thread["
								+ Thread.currentThread() + "("
								+ Thread.currentThread().getId()
								+ ")] fianlly got lock after waiting");
						return;
					}
				} catch (Throwable t) {
					// do nothing
				} finally {
					locksync.unlock();
				}

				if ((System.currentTimeMillis() - waitingStartTime) % 10000 > 2000) {
					// to avoid high CPU consuming, the loop sequence will keep
					// high in 2 seconds and lower in next 8 seconds
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// do nothing
					}
				} else {
					// don't sleep, full speed loop;
				}

				if ((System.currentTimeMillis() - waitingStartTime) > waitingTimeout * 1000) {
					// waiting timeout, throw exception in the end
					break;
				}
			}

			waitingCount--;
			if (waitingCount < 0)
				waitingCount = 0;
			throw new OpptNumServiceLockAcquireFailedException(
					"OpptNumServiceLock wait timeout to aquire lock");

		}

	}

	public void unLocking() {
		LockCounter count = currentThreadNestCount.get();
		if (count != null && count.getCount() > 0) {
			// current thread actually got lock successfully
			long spendTime = count.dec();
			if (count.getCount() == 0) {
				try {
					locksync.lock();
					lockCount--;
					if (quietMode && spendTime < quietModeDetectTime * 1000) {
						// in quiet mode, if lock is released in time (within
						// quietModeDetectTime seconds), means SCODS is working
						// fine now. So cancel quiet mode, clear lock pool
						quietMode = false;
						quietModeRefreshTimestamp = System.currentTimeMillis();
						lockCount = 0;
						info("OpptNumServiceLock Thread["
								+ Thread.currentThread()
								+ "("
								+ Thread.currentThread().getId()
								+ ")] released lock and set quietMode false successfuly");
					} else if (quietMode) {
						info("OpptNumServiceLock Thread["
								+ Thread.currentThread()
								+ "("
								+ Thread.currentThread().getId()
								+ ")] released lock but can't set quietMode false");
					}
					debug("OpptNumServiceLock Thread["
							+ Thread.currentThread() + "("
							+ Thread.currentThread().getId()
							+ ")] released lock");
				} catch (Throwable t) {
					// do nothing
				} finally {
					locksync.unlock();
				}
			}
		}
	}

	class LockCounter {
		// the number re-acquire lock in one thread
		private int count = 0;
		// the time stamp when lock is acquired
		private long startTime = System.currentTimeMillis();

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		// increasing count
		public void inc() {
			if (count == 0) {
				startTime = System.currentTimeMillis();
			}
			this.count++;
		}

		// decreasing count, return spending time after start locking time
		public long dec() {
			this.count--;
			if (this.count < 0) {
				this.count = 0;
			}
			return System.currentTimeMillis() - startTime;
		}
	}

	// private void log() {
	// System.out.println("lockCount: " + OpptNumServiceLock.lockCount
	// + " waitingCount: " + OpptNumServiceLock.waitingCount + " ");
	// }

	private void debug(String msg) {
		logger.debug(this, msg);
	}

	private void info(String msg) {
		logger.info(this, msg);
	}

	public static void main(String args[]) throws Exception {
		for (int i = 0; i < 10000000; i++) {
			UThread u = new UThread();
			u.start();
			Thread.sleep(1000);
		}
	}
}

class UThread extends Thread {
	private static int i = 0;

	public void run() {
		try {
			OpptNumServiceLock.lock();
			if (OpptNumServiceLock.getSingle().getQuietMode()) {
				if (i > 1) {
					Thread.sleep(100);
					i = 0;
					return;
				} else {
					i++;
					Thread.sleep(1000000);
				}
			} else {
				i = 0;
				Thread.sleep(1000000);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				OpptNumServiceLock.unLock();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
