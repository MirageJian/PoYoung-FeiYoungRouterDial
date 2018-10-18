package com.poyoung.logout;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class LogoutUrlRecoder {
    private static String sLogoutUrlFilename = "/LogoutUrl.json";

    private static ArrayList<HashMap<String, String>> readFile(Context context) {
        String filename = context.getFilesDir().getAbsolutePath().concat(sLogoutUrlFilename);
        StringBuilder sBuf = new StringBuilder();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(filename));
            System.out.println(fis);
            int len;
            byte[] buf = new byte[1024];
            while ((len = fis.read(buf)) != -1) {
                sBuf.append(new String(buf, 0, len));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        ArrayList<HashMap<String, String>> listLogoutUrl = new Gson().fromJson(sBuf.toString(), new TypeToken<ArrayList<HashMap<String, String>>>(){}.getType());
        if (listLogoutUrl == null)
            return new ArrayList<>();
        else
            return listLogoutUrl;
    }

    private static void writeFile(Context context, ArrayList<HashMap<String, String>> listLogoutUrl) {
        String filename = context.getFilesDir().getAbsolutePath().concat(sLogoutUrlFilename);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(filename));
            fos.write(new Gson().toJson(listLogoutUrl).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void addOne(Context context, String logoutUrl) {
        HashMap<String, String> newLogoutUrl = new HashMap<>();
        newLogoutUrl.put("logoutUrl", logoutUrl);
        newLogoutUrl.put("date", DateFormat.getDateInstance().format(new Date()));
        ArrayList<HashMap<String, String>> listLogoutUrl = readFile(context);
        if (listLogoutUrl == null || listLogoutUrl.isEmpty())
            listLogoutUrl = new ArrayList<>();
        listLogoutUrl.add(newLogoutUrl);
        writeFile(context, listLogoutUrl);
    }

    public static void removeOne(Context context, String url) {
        ArrayList<HashMap<String, String>> listLogoutUrl = readFile(context);
        if (listLogoutUrl.remove(getOne(context, url))) {
            writeFile(context, listLogoutUrl);
        }
    }

    public static HashMap<String, String> getOne(Context context, String url) {
        ArrayList<HashMap<String, String>> listLogoutUrl = readFile(context);
        for (HashMap<String, String> one : listLogoutUrl) {
            if (one.get("logoutUrl").equals(url)) {
                return one;
            }
        }
        return null;
    }

    public static ArrayList<HashMap<String, String>> getAll(Context context) {
        return readFile(context);
    }
}
