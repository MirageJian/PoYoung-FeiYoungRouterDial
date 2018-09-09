package com.feiyoung;

import java.io.PrintStream;

public class k {
    private final byte[] a = new byte['Ā'];
    private final byte[] b = new byte['Ā'];
    private final int c;

    public k(byte[] paramArrayOfByte)
    {
        if ((paramArrayOfByte.length >= 1) && (paramArrayOfByte.length <= 256))
        {
            this.c = paramArrayOfByte.length;
            int i = 0;
            for (int j = 0; j < 256; j++)
            {
                this.a[j] = ((byte)(byte)j);
                this.b[j] = ((byte)paramArrayOfByte[(j % this.c)]);
            }
            int k = 0;
            for (int j = i; j < 256; j++)
            {
                k = k + this.a[j] + this.b[j] & 0xFF;
                i = this.a[k];
                this.a[k] = ((byte)this.a[j]);
                this.a[j] = ((byte)i);
            }
            return;
        }
        throw new IllegalArgumentException("key must be between 1 and 256 bytes");
    }

    private static byte a(char paramChar) {
        switch (paramChar) {
            default:
                return 0;
            case 'F':
                return 15;
            case 'E':
                return 14;
            case 'D':
                return 13;
            case 'C':
                return 12;
            case 'B':
                return 11;
            case 'A':
                return 10;
            case '9':
                return 9;
            case '8':
                return 8;
            case '7':
                return 7;
            case '6':
                return 6;
            case '5':
                return 5;
            case '4':
                return 4;
            case '3':
                return 3;
            case '2':
                return 2;
            case '1':
                return 1;
        }
    }
    public static String a(String paramString1, String paramString2)
    {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("k=");
        localStringBuilder.append(paramString1);
        localStringBuilder.append(" ,s=");
        localStringBuilder.append(paramString2);
        return i.a(new k(a(paramString1)).a(paramString2.getBytes()));
    }

    private static byte[] a(String paramString)
    {
        byte[] arrayOfByte = new byte[paramString.length()];
        for (int i = 0; i < paramString.length(); i++) {
            arrayOfByte[i] = a(paramString.charAt(i));
        }
        return arrayOfByte;
    }

    public byte[] a(byte[] paramArrayOfByte)
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
            this.a[k] = ((byte)this.a[j]);
            this.a[j] = ((byte)m);
            int n = this.a[j];
            m = this.a[k];
            arrayOfByte[i] = ((byte)(byte)(this.a[(n + m & 0xFF)] ^ paramArrayOfByte[i]));
            i++;
        }
        return arrayOfByte;
    }
}