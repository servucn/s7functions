package com.cn.ucasp.net.types;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.Buffer;

public class S7Struct {

    public static int GetStructSize(Class<?> structType)
    {
        double numBytes = 0.0;

        Field[] infos = structType.getFields();
        for (Field info:infos)
        {
            switch (info.getClass().getSimpleName())
            {
                case "Boolean":
                    numBytes += 0.125;
                    break;
                case "Byte":
                    numBytes = Math.ceil(numBytes);
                    numBytes++;
                    break;
                case "Int":
                    break;
                case "Long":
                    numBytes = Math.ceil(numBytes);
                    if ((numBytes / 2 - Math.floor(numBytes / 2.0)) > 0)
                        numBytes++;
                    numBytes += 4;
                    break;
                case "Float": 
                case "Double":
                    numBytes = Math.ceil(numBytes);
                    if ((numBytes / 2 - Math.floor(numBytes / 2.0)) > 0)
                        numBytes++;
                    numBytes += 4;
                    break;
                default:
                    numBytes += GetStructSize(info.getType());
                    break;
            }
        }
        return (int)numBytes;
    }

    /// <summary>
    /// Creates a struct of a specified type by an array of bytes.
    /// </summary>
    /// <param name="structType">The struct type</param>
    /// <param name="bytes">The array of bytes</param>
    /// <returns>The object depending on the struct type or null if fails(array-length != struct-length</returns>
    public static Object FromBytes(Class<?> structType, byte[] bytes) throws IllegalArgumentException, Exception
    {
        if (bytes == null)
            return null;

        if (bytes.length != GetStructSize(structType))
            return null;

        // and decode it
        int bytePos = 0;
        int bitPos = 0;
        double numBytes = 0.0;
        Object structValue = structType.newInstance();

       Field[] infos = structValue.getClass().getFields();
        for (Field info:infos)
        {
            switch (info.getClass().getSimpleName())
            {
                case "Boolean":
                    // get the value
                    bytePos = (int)Math.floor(numBytes);
                    bitPos = (int)((numBytes - (double)bytePos) / 0.125);
                    if ((bytes[bytePos] & (int)Math.pow(2, bitPos)) != 0)
                        info.set(structValue, true);
                    else
                        info.set(structValue, false);
                    numBytes += 0.125;
                    break;
                case "Byte":
                    numBytes = Math.ceil(numBytes);
                    info.set(structValue, (byte)(bytes[(int)numBytes]));
                    numBytes++;
                    break;
                case "Int":
                    numBytes = Math.ceil(numBytes);
                    if ((numBytes / 2 - Math.floor(numBytes / 2.0)) > 0)
                        numBytes++;
                    // hier auswerten
                    int source = S7Word.FromBytes(bytes[(int)numBytes + 1], bytes[(int)numBytes]);
                    info.set(structValue, source);
                    numBytes += 2;
                    break;
                case "DInt":
                    numBytes = Math.ceil(numBytes);
                    if ((numBytes / 2 - Math.floor(numBytes / 2.0)) > 0)
                        numBytes++;
                    // hier auswerten
                    int sourceUInt = S7DWord.FromBytes(bytes[(int)numBytes + 3],
                                                                       bytes[(int)numBytes + 2],
                                                                       bytes[(int)numBytes + 1],
                                                                       bytes[(int)numBytes + 0]);                        
                    info.set(structValue, sourceUInt);                       
                    numBytes += 4;
                    break;

                case "Double":
                    numBytes = Math.ceil(numBytes);
                    if ((numBytes / 2 - Math.floor(numBytes / 2.0)) > 0)
                        numBytes++;
                    // hier auswerten
                    info.set(structValue, S7Real.FromByteArray(new byte[] { bytes[(int)numBytes],
                                                                       bytes[(int)numBytes + 1],
                                                                       bytes[(int)numBytes + 2],
                                                                       bytes[(int)numBytes + 3] }));
                    numBytes += 4;
                    break;
                default:
                    byte[] buffer = new byte[GetStructSize(info.getClass())];
                    if (buffer.length == 0)
                        continue;
                   // Buffer.BlockCopy(bytes, (int)Math.ceil(numBytes), buffer, 0, buffer.length);
                    info.set(structValue, FromBytes(info.getClass(), buffer));
                    numBytes += buffer.length;
                    break;
            }
        }
        return structValue;
    }

    /// <summary>
    /// Creates a byte array depending on the struct type.
    /// </summary>
    /// <param name="structValue">The struct object</param>
    /// <returns>A byte array or null if fails.</returns>
    public static byte[] ToBytes(Object structValue) throws IllegalArgumentException, IllegalAccessException
    {
        Class<?> type = structValue.getClass();

        int size = S7Struct.GetStructSize(type);
        byte[] bytes = new byte[size];
        byte[] bytes2 = null;

        int bytePos = 0;
        int bitPos = 0;
        double numBytes = 0.0;

        Field[] infos = type.getFields();
        for (Field info :infos)
        {
            bytes2 = null;
            switch (info.getClass().getSimpleName())
            {
                case "Boolean":
                    bytePos = (int)Math.floor(numBytes);
                    bitPos = (int)((numBytes - (double)bytePos) / 0.125);
                    if ((boolean)info.get(structValue))
                        bytes[bytePos] |= (byte)Math.pow(2, bitPos);            // is true
                    else
                        bytes[bytePos] &= (byte)(~(byte)Math.pow(2, bitPos));   // is false
                    numBytes += 0.125;
                    break;
                case "Byte":
                    numBytes = (int)Math.ceil(numBytes);
                    bytePos = (int)numBytes;
                    bytes[bytePos] = (byte)info.get(structValue);
                    numBytes++;
                    break;
                case "Int":
                    bytes2 = S7Int.ToByteArray((short)info.get(structValue));
                    break;
                case "DInt":
                    bytes2 = S7DInt.ToByteArray((int)info.get(structValue));
                    break;
                case "Double":
                    bytes2 = S7Real.ToByteArray((float)info.get(structValue));
                    break;
            }
            if (bytes2 != null)
            {
                // add them
                numBytes = Math.ceil(numBytes);
                if ((numBytes / 2 - Math.floor(numBytes / 2.0)) > 0)
                    numBytes++;
                bytePos = (int)numBytes;
                for (int bCnt=0; bCnt<bytes2.length; bCnt++)
                    bytes[bytePos + bCnt] = bytes2[bCnt];
                numBytes += bytes2.length;
            }
        }
        return bytes;
    }

   
}
