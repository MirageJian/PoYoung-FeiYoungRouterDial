package com.feiyoung;

import android.content.Context;
import android.os.Build;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

class AuthAtrrTool {
    private static String getAttr8(HashMap<String, String> map) {
        String[] attr2;
        try {
            String t = EncryptionTool.decrypt2(map.get("AidcAuthAttr2"));
            System.out.println(t);
            if (t != null) attr2 = t.split(";");
            else return null;
            if (attr2.length != 3) return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        // 拼接ping命令
        long l1 = System.currentTimeMillis();
        StringBuilder pingCmd = new StringBuilder();
        pingCmd.append("ping -c ");
        pingCmd.append("1");
        pingCmd.append(" -w ");
        pingCmd.append(attr2[2]);
        pingCmd.append(" -t ");
        pingCmd.append(attr2[1]);
        pingCmd.append(" ");
        pingCmd.append(attr2[0]);
        try {
            Process process = Runtime.getRuntime().exec(pingCmd.toString());
            process.waitFor();
            // 处理返回数据
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder result = new StringBuilder("content=");
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 连接结果
        StringBuilder result = new StringBuilder();
        long l2 = System.currentTimeMillis() - l1;
        result.append(",");
        result.append(l2);
        result.append(",-1,not matcher content");
        l2 = System.currentTimeMillis() - l1;
        result.append(";,");
        result.append(l2);
        result.append(",-1,not matcher content");
        l2 = System.currentTimeMillis() - l1;
        result.append(";,");
        result.append(l2);
        result.append(",-1,not matcher content");
        return EncryptionTool.encrypt2(result.toString());
    }

    private static String getAttr1() {
        return new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
    }

    private static String getAttr3() {
        // paramContext.getApplicationContext().getPackageManager().getPackageInfo(paramContext.getPackageName(), 0).versionName;paramContext.append("localVersion=")
        return EncryptionTool.encrypt2(FeiyoungServer.APK_BUILD_VERSION);
    }

    private static String getAttr4() {
        return EncryptionTool.encrypt2(Build.BRAND + ";" + Build.MODEL + ";" + Build.VERSION.RELEASE);
    }

    private static String getAttr5(HashMap<String, String> loginParamsMap) {
        return EncryptionTool.encrypt2("10.0.8.1;127.0.0.1;" + loginParamsMap.get("wlanuserip"));
    }

    private static String getAttr6(HashMap<String, String> loginParamsMap) {
        return EncryptionTool.encrypt2(loginParamsMap.get("usermac").replaceAll("-", ":").toUpperCase());
    }

    private static String getAttr7() {
        StringBuilder str = new StringBuilder();
        // 获取arp信息
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String t = line.trim();
                if (t.length() < 63) continue;
                line = line.replaceAll("\\d+.\\d+.\\d+.\\d+", "100.64.0.1");
//                line = line.replaceAll("[a-f\\d]{2}:[a-f\\d]{2}:[a-f\\d]{2}:[a-f\\d]{2}:[a-f\\d]{2}:[a-f\\d]{2}", "38:4c:4f:38:73:86");
                str.append(line);
                str.append(";");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(br != null)br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return EncryptionTool.encrypt2(str.toString());
    }

    private static String getAttr15(HashMap<String, String> attrMap) {
        return attrMap.get("AidcAuthAttr15");
    }

    private static String getAttr22Or24() {
        return EncryptionTool.encrypt2(getCreateAuthorFlag());
    }

    private static String getAttr23() {
        return EncryptionTool.encrypt2("success");
    }

    private static String getCreateAuthorFlag() {
        return "0";
    }

    static HashMap<String, String> returnLoginParams(String username, String password, HashMap<String, String> loginParamsMap, HashMap<String, String> attrMap) {
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("UserName", FeiyoungServer.getAppStart() + username);
        paramsMap.put("Password", password);
        if (FeiyoungServer.sUseMobile) {
            paramsMap.put("AidcAuthAttr1", getAttr1());
            paramsMap.put("AidcAuthAttr3", getAttr3());
            paramsMap.put("AidcAuthAttr4", getAttr4());
            paramsMap.put("AidcAuthAttr5", getAttr5(loginParamsMap));
            paramsMap.put("AidcAuthAttr6", getAttr6(loginParamsMap));
            paramsMap.put("AidcAuthAttr7", getAttr7());
            paramsMap.put("AidcAuthAttr8", getAttr8(attrMap));
            paramsMap.put("AidcAuthAttr15", getAttr15(attrMap));
            paramsMap.put("AidcAuthAttr22", getAttr22Or24());
            paramsMap.put("AidcAuthAttr23", getAttr23());
            paramsMap.put("createAuthorFlag", getCreateAuthorFlag());
        } else {
            paramsMap.put("button", "Login");
            paramsMap.put("FNAME", "0");
            paramsMap.put("OriginatingServer", "http://www.baidu.com/");
        }
        return paramsMap;
    }
}
