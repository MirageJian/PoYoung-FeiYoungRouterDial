package com.feiyoung;

import java.security.MessageDigest;

public class CustomMd5
{
    public static String getHexMd5(byte[] paramArrayOfByte)
    {
        try
        {
            return toHex(MessageDigest.getInstance("MD5").digest(paramArrayOfByte));
        }
        catch (Exception e)
        {
            System.out.println("MD5 failure!!!");
        }
        return "";
    }

    private static String toHex(byte[] paramArrayOfByte)
    {
        StringBuilder localStringBuilder = new StringBuilder();
        for (int i = 0; i < paramArrayOfByte.length; i++)
        {
            String str = Integer.toHexString(paramArrayOfByte[i] & 0xFF);
            Object localObject = str;
            if (str.length() == 1)
            {
                localObject = new StringBuilder();
                ((StringBuilder)localObject).append('0');
                ((StringBuilder)localObject).append(str);
                localObject = ((StringBuilder)localObject).toString();
            }
            localStringBuilder.append(((String)localObject).toLowerCase());
        }
        return localStringBuilder.toString();
    }
}
