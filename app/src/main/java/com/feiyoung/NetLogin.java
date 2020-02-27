package com.feiyoung;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetLogin {
    private static String sCookies = "";
    public static String sErrMsg = "";

    public String doLogin(String... info) {
        // get login url
        if (FeiyoungServer.sUseMobile)
            checkVersion();
        String attrUrl = getRedirectUrl();
        if (FeiyoungServer.sUseMobile) {
            testConnection(attrUrl);
        }
        HashMap<String, String> authAttrs = new HashMap<>();
        String[] urls = getLoginUrl(authAttrs, attrUrl);
        if (urls == null) {
            if (testConnection(FeiyoungServer.TEST_CONNECT_URL)) // 测试连通性
                return FeiyoungServer.TEST_CONNECT_URL;
            return null;
        }
        String loginUrl = urls[0];
        // 加密信息，用户名加上前缀
        String username = info[0];
        String key = DateEnum.getKeyByIndex(Calendar.getInstance().get(Calendar.DATE));
        if (key == null) return null;
        String password = EncryptionTool.encryptPassword(key, info[1]);
        // 处理AuthAttr
        HashMap<String, String> paramsMap = AuthAtrrTool.returnLoginParams(username, password, info[2],unLinkParams(loginUrl), authAttrs);
        System.out.println(paramsMap);
        System.out.println(authAttrs);
        // 登陆并取得登出信息
        return getLogoutUrl(loginUrl, paramsMap, AuthAtrrTool.returnSortParams(username, password, info[2],unLinkParams(loginUrl), authAttrs));
    }
    private void checkVersion() {
        String result = "";
        try {
            URL url = new URL(FeiyoungServer.CHECK_VERSION_URL);//设置url
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("User-Agent", FeiyoungServer.OK_HTTP_UA);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setConnectTimeout(2_000);
            connection.connect();
            //字符流写入数据 //输出流，用来发送请求，http请求实际上直到这个函数里面才正式发送出去
            OutputStream out = connection.getOutputStream();
            //创建字符流对象并用高效缓冲流包装它，便获得最高的效率,发送的是字符串推荐用字符流，其它数据就用字节流
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
            StringBuilder param = new StringBuilder();
            param.append("CurrentVersion=");
            param.append(FeiyoungServer.APK_BUILD_VERSION);
            param.append("&LocalTime=");
            param.append(URLEncoder.encode(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()), "UTF-8"));
            param.append("&ClientType=1");
            bw.write(param.toString());//把json字符串写入缓冲区中
            bw.flush();//刷新缓冲区，把数据发送出去，这步很重要
            bw.close();//使用完关闭
            out.close();
            BufferedReader inData = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "UTF-8"
            ));
            String line = inData.readLine();//读取内容
            while (line != null) {
                result = result.concat(line);
                line = inData.readLine();
            }
            inData.close();
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getRedirectUrl() {
        try {
            // 设置url
            URL url = new URL(FeiyoungServer.TEST_CONNECT_URL);
            // 打开链接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", FeiyoungServer.getAppUa());
            if (!FeiyoungServer.sUseMobile)
                connection.setRequestProperty("ClientVersion", FeiyoungServer.getAppVersion());
            connection.setConnectTimeout(2_000);
            connection.setInstanceFollowRedirects(false);
            connection.connect();
            // 获取重定向地址
            return connection.getHeaderField("Location");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private String[] getLoginUrl(HashMap<String, String> authAttrs, String attrUrl) {
        // 设置url
        String result = "";
        String redirectUrl;
        try {
            // 设置url
            URL url = new URL(attrUrl);
            // 打开链接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", FeiyoungServer.getAppUa());
            if (!FeiyoungServer.sUseMobile)
                connection.setRequestProperty("ClientVersion", FeiyoungServer.getAppVersion());
            connection.setConnectTimeout(2_000);
            connection.setInstanceFollowRedirects(false);
            connection.connect();
            // 取得响应头 写入cookies
            sCookies = connection.getHeaderField("Set-Cookie");
            // 获取数据的变量 //设置读取文件的编码格式和读取文件
            BufferedReader inData = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "UTF-8"
            ));
            // 开始获取attr
            Pattern r = Pattern.compile("<(AidcAuthAttr\\d+)>([^<]+)</");
            // 读取内容
            String line = inData.readLine();
            while (line != null) {
                // 连接结果
                result = result.concat(line);
                // 匹配xml的authAttr
                Matcher m = r.matcher(line);
                if (m.find()) {
                    authAttrs.put(m.group(1), m.group(2));
                }
                line = inData.readLine();
                System.out.println(line);
            }
            inData.close();
            // 获取重定向地址
            redirectUrl = connection.getURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Matcher matcher = Pattern.compile("<LoginURL><!\\[CDATA\\[([^]]+)]]").matcher(result);
        if (matcher.find()) {
            return new String[]{matcher.group(1), redirectUrl};
        } else {
            Matcher matcher1 = Pattern.compile("<ReplyMessage>([^<]+)</ReplyMessage>").matcher(result);
            if (matcher1.find()) {
                sErrMsg = matcher1.group(1);
            }
            return null;
        }
    }

    private boolean testConnection(String testUrl) {
        try {
            URL url = new URL(testUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", FeiyoungServer.OK_HTTP_UA);
            connection.connect();
            //获取数据的变量 //设置读取文件的编码格式和读取文件
            BufferedReader inData = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "UTF-8"
            ));
            //读取内容
            String line = inData.readLine();
            String result = "";
            while (line != null) {
                result = result.concat(line);
                line = inData.readLine();
            }
            inData.close();
            return !result.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getLogoutUrl(String loginUrl,HashMap<String, String> paramsMap, String paramsString) {
//        String params = this.linkParams(paramsMap);
        String result = "";
        try {
            URL url = new URL(loginUrl);//设置url
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestProperty("Cookie", DeviceParams.sCookies);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            if (!FeiyoungServer.sUseMobile)
                connection.setRequestProperty("Cookie", sCookies);
            connection.setRequestProperty("User-Agent", FeiyoungServer.getAppUa());
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if (!FeiyoungServer.sUseMobile)
                connection.setRequestProperty("ClientVersion", FeiyoungServer.getAppVersion());
            connection.setConnectTimeout(10_000);
            connection.connect();
            //字符流写入数据 //输出流，用来发送请求，http请求实际上直到这个函数里面才正式发送出去
            OutputStream out = connection.getOutputStream();
            //创建字符流对象并用高效缓冲流包装它，便获得最高的效率,发送的是字符串推荐用字符流，其它数据就用字节流
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
            bw.write(paramsString);//把json字符串写入缓冲区中
            bw.flush();//刷新缓冲区，把数据发送出去，这步很重要
            bw.close();//使用完关闭
            out.close();
            BufferedReader inData = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "UTF-8"
            ));
            String line = inData.readLine();//读取内容
            while (line != null) {
                result = result.concat(line);
                line = inData.readLine();
            }
            inData.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Matcher matcher = Pattern.compile("<LogoffURL><!\\[CDATA\\[([^]]+)]]").matcher(result);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            Matcher matcher1 = Pattern.compile("<ReplyMessage>([^<]+)</ReplyMessage>").matcher(result);
            if (matcher1.find()) {
                sErrMsg = matcher1.group(1);
            }
            return null;
        }
    }

    public static boolean doLogout(String logoutUrl) {
        String result = "";
        try {
            URL url = new URL(logoutUrl);//设置url
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", sCookies);
            connection.setRequestProperty("User-Agent", FeiyoungServer.getAppUa());
            connection.setRequestProperty("ClientVersion", FeiyoungServer.getAppVersion());
            connection.setConnectTimeout(2_000);
            connection.connect();
            //获取数据的变量 //设置读取文件的编码格式和读取文件
            BufferedReader inData = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "UTF-8"
            ));
            //读取内容
            String line = inData.readLine();
            while (line != null) {
                result = result.concat(line);
                line = inData.readLine();
            }
            inData.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return result.contains("<ResponseCode>150</ResponseCode>");
    }

    private String linkParams(HashMap<String, String> getParams) {
        StringBuilder sendParams = new StringBuilder();
        for (String key : getParams.keySet()) {
            String value = getParams.get(key);
            try {
                value = URLEncoder.encode(value, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!sendParams.toString().equals("")) sendParams = sendParams.append("&");
            sendParams = sendParams.append(key).append("=").append(value);
        }
        return sendParams.toString();
    }

    private HashMap<String, String> unLinkParams(String paramsStr) {
        try {
            HashMap<String, String> localHashMap = new HashMap<>();
            String[] params = paramsStr.substring(paramsStr.indexOf("?") + 1).split("&");
            int i = 0;
            while (i < params.length) {
                String[] arrayOfString = params[i].split("=");
                if (arrayOfString.length == 1) {
                    localHashMap.put(arrayOfString[0], "");
                } else if (arrayOfString.length >= 2) {
                    localHashMap.put(arrayOfString[0], URLDecoder.decode(arrayOfString[1], "UTF-8"));
                }
                i += 1;
            }
            return localHashMap;
        } catch (Exception paramString) {
            paramString.printStackTrace();
            return null;
        }
    }
}
