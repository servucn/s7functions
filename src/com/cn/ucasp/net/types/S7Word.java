package com.cn.ucasp.net.types;

public  class S7Word {
    public static int FromByteArray(byte[] bytes)
    {
    	
        return FromBytes(bytes[1]& 0xff, bytes[0]& 0xff);
    }

    public static int FromBytes(int LoVal, int HiVal)
    {
    	return (int)((HiVal)<<8 )+(int)(LoVal);
    }


    public static byte[] ToByteArray(int value)
    {
        byte[] bytes = new byte[]{(byte)(value>>8&0xff),(byte)(value&0xff)};        
        return bytes;
    }

    public static byte[] ToByteArray(int[] value)
    {
        ByteArray arr = new ByteArray();
        for(int x:value)
        {
        	arr.Add(ToByteArray(x));
        }
        return arr.getArray();
    }
    public static int[] ToArray(byte[] bytes)
    {
        int[] values = new int[bytes.length / 2];

        int counter = 0;
        for (int cnt = 0; cnt < bytes.length / 2; cnt++)
            values[cnt] = FromByteArray(new byte[] { bytes[counter++], bytes[counter++] });

        return values;
    }
}
