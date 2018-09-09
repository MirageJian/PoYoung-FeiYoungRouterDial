package com.server_auth;

import android.os.AsyncTask;

import com.feiyoung.FeiyoungServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class HttpClientHelper extends AsyncTask<Void, Void, Boolean> {
    public interface CustomDoInBackground {
        String getResult();
    }

    public interface HttpCallback {
        void doCallback(String result);
    }

    public static final String POST = "POST";
    public static final String GET = "GET";
    private static String cookies = "";

    private final CustomDoInBackground customDoInBackground;

    private String httpUrl;
    private String httpMethod = HttpClientHelper.GET;
    private String httpParams;
    private HttpCallback httpSuccessCallback;
    private HttpCallback httpErrorCallback;
    private HttpCallback httpCancelCallback;
    private String httpResult = "";

    public HttpClientHelper(CustomDoInBackground customDoInBackground) {
        this.customDoInBackground = customDoInBackground;
    }

    public HttpClientHelper() {
        this.customDoInBackground = null;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.
        if (this.customDoInBackground == null) {
            try {
                if (this.httpMethod.equals(HttpClientHelper.GET)){
                    this.httpUrl = this.httpUrl.concat("?").concat(this.httpParams);
                }
                URL url = new URL(this.httpUrl);//设置url
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(this.httpMethod);
                if (this.httpMethod.equals(HttpClientHelper.POST)) {
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                }
                connection.setRequestProperty("Cookie", cookies);
                connection.setRequestProperty("User-Agent", FeiyoungServer.getAppUa());
                connection.setRequestProperty("ClientVersion", FeiyoungServer.getAppVersion());
                connection.connect();
                if (this.httpMethod.equals(HttpClientHelper.POST)) {
                    //字符流写入数据 //输出流，用来发送请求，http请求实际上直到这个函数里面才正式发送出去
                    OutputStream out = connection.getOutputStream();
                    //创建字符流对象并用高效缓冲流包装它，便获得最高的效率,发送的是字符串推荐用字符流，其它数据就用字节流
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
                    //把字符串写入缓冲区中
                    bw.write(this.httpParams);
                    //刷新缓冲区，把数据发送出去，这步很重要
                    bw.flush();
                    //使用完关闭
                    bw.close();
                    out.close();
                }
                // 设置cookie
                String cookie = connection.getHeaderField("Set-Cookie");
                if (cookie != null){
                    cookies = cookie;
                }
                //获取数据的变量 //设置读取文件的编码格式和读取文件
                BufferedReader inData = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(), "UTF-8"
                ));
                //读取内容
                String line = inData.readLine();
                while (line != null) {
                    this.httpResult = this.httpResult.concat(line);
                    line = inData.readLine();
                }
                inData.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        } else {
            this.httpResult = this.customDoInBackground.getResult();
            return this.httpResult != null;
        }
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            if (this.httpSuccessCallback != null)
                this.httpSuccessCallback.doCallback(this.httpResult);
        } else {
            if (this.httpErrorCallback != null)
                this.httpErrorCallback.doCallback(this.httpResult);
        }
    }

    @Override
    protected void onCancelled() {
        if (this.httpCancelCallback != null){
            this.httpCancelCallback.doCallback(null);
        }
    }

    public HttpClientHelper setUrl(String url) {
        this.httpUrl = url;
        return this;
    }

    public HttpClientHelper setMethod(String method) {
        this.httpMethod = method;
        return this;
    }

    public HttpClientHelper setParams(HashMap<String, String> hashMap) {
        StringBuilder sendParams = new StringBuilder("");
        for (String key : hashMap.keySet()) {
            String value = hashMap.get(key);
            if (!sendParams.toString().equals("")) sendParams = sendParams.append("&");
            sendParams = sendParams.append(key).append("=").append(value);
        }
        this.httpParams = sendParams.toString();
        return this;
    }

    public HttpClientHelper setSuccessCallback(HttpCallback callback) {
        this.httpSuccessCallback = callback;
        return this;
    }

    public HttpClientHelper setErrorCallback(HttpCallback callback) {
        this.httpErrorCallback = callback;
        return this;
    }

    public HttpClientHelper setCancelCallback(HttpCallback callback) {
        this.httpCancelCallback = callback;
        return this;
    }

    public void doTask() {
        this.execute();
    }
}
