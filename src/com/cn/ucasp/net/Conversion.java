package com.cn.ucasp.net;

public class Conversion {

    public static int BinStringToInt32(String txt)
    {
        int cnt = 0;
        int ret = 0;

        for (cnt = txt.length() - 1; cnt >= 0; cnt += -1)
        {
            if (Integer.parseInt(txt.substring(cnt, 1)) == 1)
            {
                ret += (int)(Math.pow(2, (txt.length() - 1 - cnt)));
            }
        }
        return ret;
    }

    public static byte BinStringToByte(String txt)
    {
        int cnt = 0;
        int ret = 0;

        if (txt.length() == 8)
        {
            for (cnt = 7; cnt >= 0; cnt += -1)
            {
                if (Integer.parseInt(txt.substring(cnt, 1)) == 1)
                {
                    ret += (int)(Math.pow(2, (txt.length() - 1 - cnt)));
                }
            }
            return (byte)ret;
        }
        return (Byte) null;
    }

    public static String ValToBinString(Object value)
    {
        int cnt = 0;
        int cnt2 = 0;
        int x = 0;
        String txt = "";
        long longValue = 0;

        try
        {
            if (value.getClass().getName().indexOf("[]") < 0)
            {
                // ist nur ein Wert
                switch (value.getClass().getName())
                {
                    case "Byte":
                        x = 7;
                        longValue = (long)((byte)value);
                        break;
                    case "Int16":
                        x = 15;
                        longValue = (long)((int)value);
                        break;
                    case "Int32":
                        x = 31;
                        longValue = (long)((int)value);
                        break;
                    case "Int64":
                        x = 63;
                        longValue = (long)(value);
                        break;
                    default:
                        throw new Exception();
                }

                for (cnt = x; cnt >= 0; cnt += -1)
                {
                    if (((long)longValue & (long)Math.pow(2, cnt)) > 0)
                        txt += "1";
                    else
                        txt += "0";
                }
            }
            else
            {
                // ist ein Array
                switch (value.getClass().getName())
                {
                    case "Byte[]":
                        x = 7;
                        byte[] ByteArr = (byte[])value;
                        for (cnt2 = 0; cnt2 <= ByteArr.length - 1; cnt2++)
                        {
                            for (cnt = x; cnt >= 0; cnt += -1)
                                if ((ByteArr[cnt2] & (byte)Math.pow(2, cnt)) > 0) txt += "1"; else txt += "0";
                        }
                        break;
                    case "Int16[]":
                        x = 15;
                        int[] Int16Arr = (int[])value;
                        for (cnt2 = 0; cnt2 <= Int16Arr.length - 1; cnt2++)
                        {
                            for (cnt = x; cnt >= 0; cnt += -1)
                                if ((Int16Arr[cnt2] & (byte)Math.pow(2, cnt)) > 0) txt += "1"; else txt += "0";
                        }
                        break;
                    case "Int32[]":
                        x = 31;
                        int[] Int32Arr = (int[])value;
                        for (cnt2 = 0; cnt2 <= Int32Arr.length - 1; cnt2++)
                        {
                            for (cnt = x; cnt >= 0; cnt += -1)
                                if ((Int32Arr[cnt2] & (byte)Math.pow(2, cnt)) > 0) txt += "1"; else txt += "0";
                        }
                        break;
                    case "Int64[]":
                        x = 63;
                        byte[] Int64Arr = (byte[])value;
                        for (cnt2 = 0; cnt2 <= Int64Arr.length - 1; cnt2++)
                        {
                            for (cnt = x; cnt >= 0; cnt += -1)
                                if ((Int64Arr[cnt2] & (byte)Math.pow(2, cnt)) > 0) txt += "1"; else txt += "0";
                        }
                        break;
                    default:
                        throw new Exception();
                }
            }
            return txt;
        }catch(Exception ex)
        {
        	
        	return "";
        }
    }

    public static  boolean SelectBit(byte data, int bitPosition)
    {
        int mask = 1 << bitPosition;
        int result = data & mask;

        return (result != 0);
    }

 
}
