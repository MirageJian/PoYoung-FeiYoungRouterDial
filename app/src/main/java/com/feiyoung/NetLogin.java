package com.feiyoung;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetLogin {
    private static String sCookies = "";
    public static String sErrMsg = "";

    public String doLogin(Context context, String... info) {
        // get login url
        HashMap<String, String> authAttrs = new HashMap<>();
        String loginUrl = getLoginUrl(authAttrs);
        if (loginUrl == null) {
            if (testConnection()) // 测试连通性
                return FeiyoungServer.TEST_CONNECT_URL;
            return null;
        }
        // 加密信息
        String username = info[0];
        String key = DateEnum.getKeyByIndex(Calendar.getInstance().get(Calendar.DATE));
        if (key == null) return null;
        String password = EncryptionTool.encryptPassword(key, info[1]);
        // 处理AuthAttr
        HashMap<String, String> paramsMap = AuthAtrrTool.returnLoginParams(username, password,unLinkParams(loginUrl), authAttrs, context);
        System.out.println(paramsMap);
        System.out.println(authAttrs);
        // 登陆并取得登出信息
        return getLogoutUrl(loginUrl, paramsMap);
    }

    private String getLoginUrl(HashMap<String, String> authAttrs) {
        // 设置url
        String result = "";
        try {
            // 设置url
            URL url = new URL(FeiyoungServer.REDIRECT_URL);
            // 打开链接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", FeiyoungServer.getAppUa());
            connection.setRequestProperty("ClientVersion", FeiyoungServer.getAppVersion());
            connection.setConnectTimeout(2_000);
            connection.connect();
            // 取得响应头
            // 写入cookies
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
                sErrMsg = matcher1.group(1);
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

    private String getLogoutUrl(String loginUrl,HashMap<String, String> paramsMap) {
        String params = this.linkParams(paramsMap);
        String result = "";
        try {
            URL url = new URL(loginUrl);//设置url
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestProperty("Cookie", DeviceParams.sCookies);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Cookie", sCookies);
            connection.setRequestProperty("User-Agent", FeiyoungServer.getAppUa());
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("ClientVersion", FeiyoungServer.getAppVersion());
            connection.setConnectTimeout(10_000);
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
