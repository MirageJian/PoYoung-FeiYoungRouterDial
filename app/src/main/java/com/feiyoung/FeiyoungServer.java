package com.feiyoung;

public class FeiyoungServer {
    public static final String HOST_URL = "http://58.53.199.144:8001";
    public static final String REDIRECT_URL = "http://100.64.0.1";
    public static final String CHECK_VERSION_URL = "http://58.53.199.149:8005/CheckVersion";
    public static final String TEST_CONNECT_URL = "http://www.baidu.com";
    public static final String APK_BUILD_VERSION = "1.0.13";
    public static final String OK_HTTP_UA = "okhttp/3.10.0";

    public static boolean sUseMobile = true;
    static final String USERNAME_PREFIX = "!^Adcm0";
    private static final String MOBILE_VERSION = "Maod";
    private static final String COMPUTER_VERSION = "B6EA";
    public static String getAppVersion(){
        return sUseMobile ? MOBILE_VERSION : COMPUTER_VERSION;
    }
    public static String getAppUa(){
        return "CDMA+WLAN("+ (sUseMobile ? MOBILE_VERSION : "win64") +")";
    }
    public static String getAppStart(){
        return "!^" + (sUseMobile ? MOBILE_VERSION : COMPUTER_VERSION) + "0";
    }
}
