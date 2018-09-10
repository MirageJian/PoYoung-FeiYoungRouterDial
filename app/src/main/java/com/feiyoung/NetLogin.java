package com.feiyoung;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetLogin {
    private static String sCookies = "";
    public static String sErrMsg = "";

    public String doLogin(String... info) {
//        get login url
        String loginUrl = getLoginUrl();
        if (loginUrl == null) {
            if (testConnection())
                return FeiyoungServer.TEST_CONNECT_URL;
            return null;
        }
//        encrypt info
        String username = info[0];
        String key = DateEnum.getKeyByIndex(Calendar.getInstance().get(Calendar.DATE));
        String password = Encryption.encrypt(key, info[1]);
//        login and get logout url
        return getLogoutUrl(loginUrl, username, password);
    }

    private String getLoginUrl() {
        //设置url
        String result = "";
        int timeoutTime = 2000;
        try {
            //设置url
            URL url = new URL(FeiyoungServer.REDIRECT_URL);
            //打开链接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", FeiyoungServer.getAppUa());
            connection.setRequestProperty("ClientVersion", FeiyoungServer.getAppVersion());
            connection.setConnectTimeout(timeoutTime);
            connection.connect();
            //取得响应头
            //写入cookies
            sCookies = connection.getHeaderField("Set-Cookie");
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
            return null;
        }
        Matcher matcher = Pattern.compile("<LoginURL><!\\[CDATA\\[([^]]+)]]").matcher(result);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            Matcher matcher1 = Pattern.compile("<ReplyMessage>([^<]+)</ReplyMessage>").matcher(result);
            if (matcher1.find()) {
                sErrMsg =  matcher1.group(1);
            }
            return null;
        }
    }

    private boolean testConnection() {
        try {
            URL url = new URL(FeiyoungServer.TEST_CONNECT_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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

    private String getLogoutUrl(String loginUrl, String username, String password) {
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("button", "Login");
        paramsMap.put("UserName", FeiyoungServer.getAppStart() + username);
        paramsMap.put("Password", password);
        paramsMap.put("FNAME", "0");
        paramsMap.put("OriginatingServer", "http://www.baidu.com/");
        String params = this.linkParams(paramsMap);
        String result = "";
        try {
            URL url = new URL(loginUrl);//设置url
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
//            connection.setRequestProperty("Cookie", DeviceParams.sCookies);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Cookie", sCookies);
            connection.setRequestProperty("User-Agent", FeiyoungServer.getAppUa());
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("ClientVersion", FeiyoungServer.getAppVersion());
            connection.connect();
            //字符流写入数据 //输出流，用来发送请求，http请求实际上直到这个函数里面才正式发送出去
            OutputStream out = connection.getOutputStream();
            //创建字符流对象并用高效缓冲流包装它，便获得最高的效率,发送的是字符串推荐用字符流，其它数据就用字节流
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
            bw.write(params);//把json字符串写入缓冲区中
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
                sErrMsg =  matcher1.group(1);
            }
            return null;
        }
    }

    public static boolean doLogout(String logoutUrl){
        String result = "";
        try {
            URL url = new URL(logoutUrl);//设置url
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Cookie", sCookies);
            connection.setRequestProperty("User-Agent", FeiyoungServer.getAppUa());
            connection.setRequestProperty("ClientVersion", FeiyoungServer.getAppVersion());
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

    private String linkParams(HashMap<String, String> getParams){
        StringBuilder sendParams = new StringBuilder("");
        for (String key: getParams.keySet()) {
            String value = getParams.get(key);
            if (!sendParams.toString().equals("")) sendParams = sendParams.append("&");
            sendParams = sendParams.append(key).append("=").append(value);
        }
        return sendParams.toString();
    }
}
