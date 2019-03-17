package com.feiyoung;

import android.util.Base64;

import java.util.Calendar;

class EncryptionTool {
    private final byte[] a = new byte['Ā'];
    // 预处理key
    private EncryptionTool(byte[] paramArrayOfByte)
    {
        final byte[] b = new byte['Ā'];
        if ((paramArrayOfByte.length >= 1) && (paramArrayOfByte.length <= 256))
        {
            int i = 0;
            for (int j = 0; j < 256; j++)
            {
                this.a[j] = (byte)j;
                b[j] = paramArrayOfByte[(j % paramArrayOfByte.length)];
            }
            int k = 0;
            for (int j = i; j < 256; j++)
            {
                k = k + this.a[j] + b[j] & 0xFF;
                i = this.a[k];
                this.a[k] = this.a[j];
                this.a[j] = (byte)i;
            }
            return;
        }
        throw new IllegalArgumentException("key must be between 1 and 256 bytes");
    }

    static String encryptPassword(String key, String password)
    {
//        StringBuilder localStringBuilder = new StringBuilder();
//        localStringBuilder.append("k=");
//        localStringBuilder.append(paramString1);
//        localStringBuilder.append(" ,s=");
//        localStringBuilder.append(paramString2);
        return CustomMd5.getHexMd5(new EncryptionTool(toBytes(key)).obfuscate(password.getBytes()));
    }

    static String decrypt2(String encryption) {
        byte[] base64Bytes =Base64.decode(encryption, Base64.DEFAULT);
        String key = DateEnum2.getKeyByIndex(Calendar.getInstance().get(Calendar.DATE));
        if (key != null)
            return new String(Decryption2(Encryption1(toBytes(key)), base64Bytes));
        else
            return null;
    }
    static String encrypt2(String str) {
        String key = DateEnum2.getKeyByIndex(Calendar.getInstance().get(Calendar.DATE));
        if (key != null)
            return Base64.encodeToString(new EncryptionTool(toBytes(key)).obfuscate(str.getBytes()), Base64.NO_WRAP);
        else
            return null;
    }
    // 通过key加密
    private byte[] obfuscate(byte[] paramArrayOfByte)
    {
        byte[] arrayOfByte = new byte[paramArrayOfByte.length];
        int i = 0;
        int j = 0;
        int k = 0;
        while (i < paramArrayOfByte.length)
        {
            j = j + 1 & 0xFF;
            k = k + this.a[j] & 0xFF;
            int m = this.a[k];
            this.a[k] = this.a[j];
            this.a[j] = ((byte)m);
            int n = this.a[j];
            m = this.a[k];
            arrayOfByte[i] = ((byte)(this.a[(n + m & 0xFF)] ^ paramArrayOfByte[i]));
            i++;
        }
        return arrayOfByte;
    }
    // 通过key解密
    public static byte[] Encryption1(byte[] originalBytes)
    {
        byte[] a = new byte['Ā'];
        byte[] b = new byte['Ā'];
        int c = originalBytes.length;
        if ((originalBytes.length >= 1) && (originalBytes.length <= 256))
        {
            int i = 0;
            for (int j = 0; j < 256; j++)
            {
                a[j] = ((byte)(byte)j);
                b[j] = (originalBytes[(j % c)]);
            }
            int k = 0;
            for (int j = i; j < 256; j++)
            {
                k = k + a[j] + b[j] & 0xFF;
                i = a[k];
                a[k] = (a[j]);
                a[j] = ((byte)i);
            }
        }
        return a;
    }
    public static byte[] Decryption2(byte[] param1, byte[] param2)
    {
        byte[] arrayOfByte = new byte[param2.length];
        int i = 0;
        int j = 0;
        int k = 0;
        while (i < param2.length)
        {
            j = j + 1 & 0xFF;
            k = k + param1[j] & 0xFF;
            int n = param1[j];
            int m = param1[k];
            param1[k] = param1[j];
            param1[j] = ((byte)m);
            arrayOfByte[i] = (byte)(param1[(n + m & 0xFF)] ^ param2[i]);
            i++;
        }
        return arrayOfByte;
    }

    private byte[] doDecrypt(byte[] param2)
    {
        byte[] arrayOfByte = new byte[param2.length];
        int i = 0;
        int j = 0;
        int k = 0;
        while (i < param2.length)
        {
            j = j + 1 & 0xFF;
            k = k + this.a[j] & 0xFF;
            int n = this.a[j];
            int m = this.a[k];
            this.a[k] = this.a[j];
            this.a[j] = ((byte)m);
            arrayOfByte[i] = (byte)(this.a[(n + m & 0xFF)] ^ param2[i]);
            i++;
        }
        return arrayOfByte;
    }

    private static byte[] toBytes(String paramString)
    {
        byte[] arrayOfByte = new byte[paramString.length()];
        for (int i = 0; i < paramString.length(); i++) {
            arrayOfByte[i] = toIntFromChar(paramString.charAt(i));
        }
        return arrayOfByte;
    }
    private static byte toIntFromChar(char paramChar) {
        switch (paramChar) {
            default: return 0;
            case 'F': return 15;
            case 'E': return 14;
            case 'D': return 13;
            case 'C': return 12;
            case 'B': return 11;
            case 'A': return 10;
            case '9': return 9;
            case '8': return 8;
            case '7': return 7;
            case '6': return 6;
            case '5': return 5;
            case '4': return 4;
            case '3': return 3;
            case '2': return 2;
            case '1': return 1;
        }
    }
}