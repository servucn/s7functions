package com.cn.ucasp.net.types;

import com.cn.ucasp.net.Conversion;

public class S7Timer {
     public static double FromByteArray(byte[] bytes)
     {
         double wert = 0;
         int value = (int)S7Word.FromBytes(bytes[1], bytes[0]);
         String txt = Conversion.ValToBinString(value);
         wert = Conversion.BinStringToInt32(txt.substring(4, 4)) * 100.0;
         wert += Conversion.BinStringToInt32(txt.substring(8, 4)) * 10.0;
         wert += Conversion.BinStringToInt32(txt.substring(12, 4));
         switch (txt.substring(2, 2))
         {
             case "00":
                 wert *= 0.01;
                 break;
             case "01":
                 wert *= 0.1;
                 break;
             case "10":
                 wert *= 1.0;
                 break;
             case "11":
                 wert *= 10.0;
                 break;
         }
         return wert;
     }

     public static byte[] ToByteArray(int value)
     {
         byte[] bytes = new byte[2];
         int x = 2;
         long valLong = (long)(value);
         for (int cnt = 0; cnt < x; cnt++)
         {
        	 long x1 = (long)Math.pow(256, (cnt));

        	 long x3 = (long)(valLong / x1);
             bytes[x - cnt - 1] = (byte)(x3 & 255);
             valLong -= bytes[x - cnt - 1] * x1;
         }
         return bytes;
     }

     public static byte[] ToByteArray(int[] value)
     {
         ByteArray arr = new ByteArray();
         for(int val:value)
             arr.Add(ToByteArray(val));
         return arr.getArray();
     }
     public static double[] ToArray(byte[] bytes)
     {
         double[] values = new double[bytes.length / 2];

         int counter = 0;
         for (int cnt = 0; cnt < bytes.length / 2; cnt++)
             values[cnt] = FromByteArray(new byte[] { bytes[counter++], bytes[counter++] });

         return values;
     }
}
