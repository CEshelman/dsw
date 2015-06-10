cd /home/wesley/webdriver/www
python -m SimpleHTTPServer &
#could set this as userdir 
#cd ${base.dir}/webdriver_${env}/source/quoteAutomationTest/build
cd /home/wesley/webdriver/build/fvt
/home/will/willATibm/IBM/jazz/buildsystem/buildengine/eclipse/jbe -repository https://igartc03.swg.usma.ibm.com/jazz/ -userid zhoujunz@cn.ibm.com -passwordFile /home/will/willATibm/wesleypass.txt -engineId CVT-DSW-WebDriver-ead
