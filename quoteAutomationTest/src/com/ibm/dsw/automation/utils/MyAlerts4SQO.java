package com.ibm.dsw.automation.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class MyAlerts4SQO{
	public static final String MYALERTS_SERVICE_ENDPOINT = "http://myalerts.innovate.ibm.com";
	public static final String MYALERTS_NOTIFY_URI = "/api/message/notify";
	public static final String MYALERTS_API_STATE_JSON = "success";
	public static final String MYALERTS_API_ERROR_JSON = "error";
	public static final String APP_ID = "com.ibm.dsw.sqo.automationTest";
//	public static final String APP_ID = "com.ibm.dsw.quote.mobile";
	
	private Logger logger = Logger.getLogger(this.getClass());

	public void sendMessage(String message, String emailAdd) {

		if ((message == null) || (emailAdd == null)) {
			logger.info("--Invalid input parameter, message: "	+ message + ",emailAdd: " + emailAdd);
			logger.error(String.format("---Invalid input parameter, message: %s, emailAdd: %s", message, emailAdd));
			return;
		}

		SendThread thread = new SendThread(message, emailAdd);
		thread.start();
		
		return;
	}


	private class SendThread extends Thread{
		
		private List<BasicNameValuePair> paramList = new ArrayList<BasicNameValuePair>(); 
		private String message = null;
        private String userList = null;
        
		public SendThread(String message, String emailAdd){
			this.message = message;
			this.userList = emailAdd;
		}
		
		@Override
		public void run() {
			
			String[] notifiedUsers = StringUtils.split(userList, ",");

			if (notifiedUsers != null) {

				for (int i = 0; i < notifiedUsers.length; i++) {

					String user = notifiedUsers[i];
					
					paramList.clear();

					paramList.add(new BasicNameValuePair("target", user));

					paramList.add(new BasicNameValuePair("text", message));

					paramList.add(new BasicNameValuePair("app_id", APP_ID));
					
					callMyAlertsAPI(MYALERTS_NOTIFY_URI);

				}
			}
			
		}
		
		/**
		 * parse MyAlerts API call result
		 * @param is
		 * @return
		 */
		private String convertStreamToString(InputStream is) {

			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					logger.error("Failed to convert Stream to String when send MyAlerts.",e);
				}
			}
			return sb.toString();
		}
		
		/**
		 * Call MyAlerts API
		 * @param APIName
		 */
		private void callMyAlertsAPI(String APIName){
			
			HttpClient client = new DefaultHttpClient();
			HttpEntity entity = null;
			HttpResponse response = null;
			HttpPost httpPost = new HttpPost(MYALERTS_SERVICE_ENDPOINT + APIName);

			Map<String, String> result = new HashMap<String, String>();

			try {
				entity = new UrlEncodedFormEntity(paramList, "UTF-8");
				httpPost.setEntity(entity);
				response = client.execute(httpPost);
				entity = response.getEntity();

				if (entity != null) {
					InputStream instream = entity.getContent();
					String JSONresult = convertStreamToString(instream);
					instream.close();
					JSONObject json = new JSONObject(JSONresult);
					JSONArray nameArray = json.names();
					JSONArray valArray = json.toJSONArray(nameArray);

					if (nameArray != null && valArray != null) {
						for (int i = 0; i < nameArray.length(); i++) {
							String name = nameArray.getString(i);
							String value = valArray.getString(i);
							result.put(name, value);
						}
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
				result.put("success", "false");
				result.put("error", "error happens on MyAlerts REST API call.");
			}

			try {
				if (result.containsKey(MYALERTS_API_STATE_JSON)) {
					boolean callFlag = Boolean.parseBoolean(result.get(MYALERTS_API_STATE_JSON));
					logger.info("MyAlerts REST API calling result: "	+ callFlag);
				}

				if (result.containsKey(MYALERTS_API_ERROR_JSON)) {
					logger.info("MyAlerts REST API calling message: " + result.get(MYALERTS_API_ERROR_JSON));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}
	
	
	
	public static void main(String[] args) {
		MyAlerts4SQO impl = new MyAlerts4SQO();
		impl.sendMessage(
				"Sorry to disturb, this is a MyAlerts testing message. Please kindly ignore this message.",
				"xiaogy@cn.ibm.com");
	}
}
