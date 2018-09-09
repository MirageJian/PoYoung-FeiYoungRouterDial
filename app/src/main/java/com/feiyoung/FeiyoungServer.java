package com.feiyoung;

public class FeiyoungServer {
    public static final String HOST_URL = "http://58.53.199.144:8001";
    public static final String REDIRECT_URL = "http://100.64.0.1";
    public static final String TEST_CONNECT_URL = "http://baidu.com";

    public static boolean useMobile = true;
    private static final String MOBILE_VERSION = "Maod";
    private static final String COMPUTER_VERSION = "B6EA";
    public static String getAppVersion(){
        return useMobile ? MOBILE_VERSION : COMPUTER_VERSION;
    }
    public static String getAppUa(){
        return "CDMA+WLAN("+ (useMobile ? MOBILE_VERSION : "win64") +")";
    }
    public static String getAppStart(){
        return "!^" + (useMobile ? MOBILE_VERSION : COMPUTER_VERSION) + "0";
    }
}
