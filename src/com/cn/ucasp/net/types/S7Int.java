package com.cn.ucasp.net.types;

public class S7Int {

    public static short FromByteArray(byte[] bytes)
    {
        return FromBytes((short)bytes[1]&0xff, (short)bytes[0]&0xff);
    }

    public static short FromBytes(int LoVal, int HiVal)
    {
        return (short)(HiVal * 256 + LoVal);
    }

    public static byte[] ToByteArray(int value)
    {
    	byte[] bytes = new byte[]{(byte)(value>>8&0xff),(byte)(value&0xff)};        
        return bytes;
    }

    public static byte[] ToByteArray(int[] value)
    {
        ByteArray arr = new ByteArray();
        for(int val:value)
            arr.Add(ToByteArray(val));
        return arr.getArray();
    }

    public static short[] ToArray(byte[] bytes)
    {
    	short[] values = new short[bytes.length / 2];

        int counter = 0;
        for (int cnt = 0; cnt < bytes.length / 2; cnt++)
            values[cnt] = FromByteArray(new byte[] { bytes[counter++], bytes[counter++] });

        return values;
    }

    public static short CWord(int value)
    {
        if (value > 32767)
        {
            value -= 32768;
            value = 32768 - value;
            value *= -1;
        }
        return (short)value;
    }
}
