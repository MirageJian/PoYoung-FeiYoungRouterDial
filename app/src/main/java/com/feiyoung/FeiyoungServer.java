package com.feiyoung;

public class FeiyoungServer {
    public static final String HOST_URL = "http://58.53.199.144:8001";
    public static final String REDIRECT_URL = "http://100.64.0.1";
    public static final String TEST_CONNECT_URL = "http://baidu.com";

    public static boolean sUseMobile = true;
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
