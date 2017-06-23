package com.cn.ucasp.net.types;

public class S7Boolean {

    public static  boolean GetValue(byte value, int bit)
    {
        if ((value & (int)Math.pow(2, bit)) != 0)
            return true;
        else
            return false;
    }

    public static byte SetBit(byte value, int bit)
    {
        return (byte)(value | (byte)Math.pow(2, bit));
    }

    public static byte ClearBit(byte value, int bit)
    {
        return (byte)(value & (byte)(~(byte)Math.pow(2, bit)));
    }

}
