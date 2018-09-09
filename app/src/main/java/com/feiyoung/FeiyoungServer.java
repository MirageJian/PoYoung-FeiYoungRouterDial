package com.feiyoung;

public class FeiyoungServer {
    public static final String HostUrl = "http://58.53.199.144:8001";
    public static final String RedirectUrl = "http://100.64.0.1";
    public static final String TestConnetUrl = "http://baidu.com";

    public static boolean useMobile = true;
    private static final String MobileVersion = "Maod";
    private static final String ComputerVersion = "B6EA";
    public static String getAppVersion(){
        return useMobile ? MobileVersion : ComputerVersion;
    }
    public static String getAppUa(){
        return "CDMA+WLAN("+ (useMobile ? MobileVersion : "win64") +")";
    }
    public static String getAppStart(){
        return "!^" + (useMobile ? MobileVersion : ComputerVersion) + "0";
    }
}
