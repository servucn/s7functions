package com.cn.ucasp.net;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.InetSocketAddress;
import java.net.Socket;

import javax.print.attribute.standard.MediaSize.Other;

import com.cn.ucasp.net.types.ByteArray;
import com.cn.ucasp.net.types.S7Boolean;
import com.cn.ucasp.net.types.S7Byte;
import com.cn.ucasp.net.types.S7Counter;
import com.cn.ucasp.net.types.S7DInt;
import com.cn.ucasp.net.types.S7DWord;
import com.cn.ucasp.net.types.S7Int;
import com.cn.ucasp.net.types.S7Real;
import com.cn.ucasp.net.types.S7Timer;
import com.cn.ucasp.net.types.S7Word;
import com.cn.ucasp.net.types.S7String;

import lombok.*;

public class S7TCPConnection {

	int slot;
	int rack;
	String ip;
	CpuType cpuType;
	int port;

	private Socket socket;
	private OutputStream out = null;
	private InputStream in = null;

	public S7TCPConnection() {
		slot = 2;
		rack = 0;
		cpuType = CpuType.S7300;
		ip = "127.0.0.1";
		port = 102;
	}

	public S7TCPConnection withSolt(int slot) {
		this.slot = slot;
		return this;
	}

	public S7TCPConnection withRack(int rack) {
		this.rack = rack;
		return this;
	}

	public S7TCPConnection withIp(String ip) {
		this.ip = ip;
		return this;
	}

	public S7TCPConnection withPort(int port) {
		this.port = port;
		return this;
	}

	public S7TCPConnection withCpu(CpuType cType) {
		this.cpuType = cType;
		return this;
	}

	@Setter
	@Getter
	private ErrorCode errorCode;

	public S7TCPConnection Open() throws IOException {
		byte[] bReceive = new byte[256];
		Ping ping = new Ping();
		if (!ping.isReachable(ip)) {
			errorCode = ErrorCode.IPAdressNotAvailable;
			return this;
		}
		try {
			this.socket = new Socket();
			this.socket.setSoTimeout(10 * 2000);
			this.socket.connect(new InetSocketAddress(this.ip, this.port));
		} catch (Exception e) {
			errorCode = ErrorCode.ConnectionError;
			return this;
		}
		byte[] bSend1 = { (byte) 3, 0, 0, 22, 17, (byte) 224, 0, 0, 0, 46, 0, (byte) 193, 2, 1, 0, (byte) 194, 2, 3, 0,
				(byte) 192, 1, 9 };
		switch (this.cpuType) {
		case S7200:
			bSend1[11] = (byte) 193;
			bSend1[12] = 2;
			bSend1[13] = 16;
			bSend1[14] = 0;
			bSend1[15] = (byte) 194;
			bSend1[16] = 2;
			bSend1[17] = 16;
			bSend1[18] = 0;
			break;
		case S71200:
		case S7300:
		case S7400:
			bSend1[11] = (byte) 193;
			bSend1[12] = 2;
			bSend1[13] = 1;
			bSend1[14] = 0;
			bSend1[15] = (byte) 194;
			bSend1[16] = 2;
			bSend1[17] = 3;
			bSend1[18] = (byte) (rack * 2 * 16 + slot);
			break;
		case S71500:
			bSend1[11] = (byte) 194;
			bSend1[12] = 2;
			bSend1[13] = 1;
			bSend1[14] = 0;
			bSend1[15] = (byte) 194;
			bSend1[16] = 2;
			bSend1[17] = 3;
			bSend1[18] = (byte) (rack * 2 * 16 + slot);
			break;
		default:
			errorCode = ErrorCode.WrongCPU_Type;
			return this;
		}

		socket.setSendBufferSize(1024);
		socket.setReceiveBufferSize(1024);
		socket.setTcpNoDelay(true);
		out = socket.getOutputStream();
		in = socket.getInputStream();
		//  ‰≥ˆ«Î«Û
		out.write(bSend1);

		if (in.read(bReceive) != 22) {
			errorCode = ErrorCode.WrongNumberReceivedBytes;
			return this;
		}
		byte[] bSend2 = { 3, 0, 0, 25, 2, (byte) 240, (byte) 128, 50, 1, 0, 0, (byte) 255, (byte) 255, 0, 8, 0, 0,
				(byte) 240, 0, 0, 3, 0, 3, 1, 0 };
		out.write(bSend2);
		if (in.read(bReceive) != 27) {
			errorCode = ErrorCode.WrongNumberReceivedBytes;
			return this;
		}
		return this;
	}

	public byte[] ReadBytes(DataType dataType, int DB, int startByteAdr, int count) throws IOException {
		byte[] bytes = new byte[count];
		int packageSize = 31;
		ByteArray packageArray = new ByteArray(packageSize);
		packageArray.Add(new byte[] { 3, 0, 0 });
		packageArray.Add((byte) packageSize);
		packageArray.Add(new byte[] { 0x02, (byte) 0xf0, (byte) 0x80, 0x32, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0e,
				0x00, 0x00, 0x04, 0x01, 0x12, 0x0a, 0x10 });
		switch (dataType) {
		case Timer:
		case Counter:
			packageArray.Add((byte) dataType.getIndex());
			break;

		default:
			packageArray.Add((byte) 2);
			break;
		}
		packageArray.Add(S7Word.ToByteArray(count));
		packageArray.Add(S7Word.ToByteArray((DB)));
		packageArray.Add((byte) dataType.getIndex());
		int overflow = (int) (startByteAdr * 8 / 0xffff);
		packageArray.Add((byte) overflow);
		switch (dataType) {
		case Timer:
		case Counter:
			packageArray.Add(S7Word.ToByteArray(startByteAdr));
			break;
		default:
			packageArray.Add(S7Word.ToByteArray(startByteAdr * 8));
			break;
		}
		try {
			out.write(packageArray.getArray());
			byte[] bReceive = new byte[512];
			int numReceive = in.read(bReceive);
			for (int cnt = 0; cnt < count; cnt++)
				bytes[cnt] = bReceive[cnt + 25];
			errorCode = ErrorCode.NoError;

		} catch (Exception e) {
			errorCode = ErrorCode.ConnectionError;
		}
		return bytes;
	}

	public Object Read(DataType dataType, int db, int startByteAdr, VarType varType, int varCount) throws IOException {
		byte[] bytes = null;
		int cntBytes = 0;

		switch (varType) {
		case S7Byte:
			cntBytes = varCount;
			if (cntBytes < 1)
				cntBytes = 1;
			bytes = ReadBytes(dataType, db, startByteAdr, cntBytes);
			if (bytes == null)
				return null;
			if (varCount == 1)
				return bytes[0];
			else
				return bytes;
		case S7Word:
			cntBytes = varCount * 2;
			bytes = ReadBytes(dataType, db, startByteAdr, cntBytes);
			if (bytes == null)
				return null;

			if (varCount == 1)
				return S7Word.FromByteArray(bytes);
			else
				return S7Word.ToArray(bytes);
		case S7Int:
			cntBytes = varCount * 2;
			bytes = ReadBytes(dataType, db, startByteAdr, cntBytes);
			if (bytes == null)
				return null;

			if (varCount == 1)
				return S7Int.FromByteArray(bytes);
			else
				return S7Int.ToArray(bytes);
		case S7DWord:
			cntBytes = varCount * 4;
			bytes = ReadBytes(dataType, db, startByteAdr, cntBytes);
			if (bytes == null)
				return null;

			if (varCount == 1)
				return S7DWord.FromByteArray(bytes);
			else
				return S7DWord.ToArray(bytes);
		case S7DInt:
			cntBytes = varCount * 4;
			bytes = ReadBytes(dataType, db, startByteAdr, cntBytes);
			if (bytes == null)
				return null;

			if (varCount == 1)
				return S7DInt.FromByteArray(bytes);
			else
				return S7DInt.ToArray(bytes);
		case S7Real:
			cntBytes = varCount * 4;
			bytes = ReadBytes(dataType, db, startByteAdr, cntBytes);
			if (bytes == null)
				return null;

			if (varCount == 1)
				return com.cn.ucasp.net.types.S7Real.FromByteArray(bytes);
			else
				return com.cn.ucasp.net.types.S7Real.ToArray(bytes);
		case S7String:
			cntBytes = varCount;
			bytes = ReadBytes(dataType, db, startByteAdr, cntBytes);
			if (bytes == null)
				return null;

			return S7String.FromByteArray(bytes);
		case S7Timer:
			cntBytes = varCount * 2;
			bytes = ReadBytes(dataType, db, startByteAdr, cntBytes);
			if (bytes == null)
				return null;

			if (varCount == 1)
				return S7Timer.FromByteArray(bytes);
			else
				return S7Timer.ToArray(bytes);
		case S7Counter:
			cntBytes = varCount * 2;
			bytes = ReadBytes(dataType, db, startByteAdr, cntBytes);
			if (bytes == null)
				return null;

			if (varCount == 1)
				return S7Counter.FromByteArray(bytes);
			else
				return S7Counter.ToArray(bytes);
		default:
			return null;
		}
	}

	public Object Read(String variable) {
		DataType mDataType;
		int mDB;
		int mByte;
		int mBit;
		byte objByte;
		short objShort;
		int objInt;
		float objReal;

		String txt = variable.toUpperCase();
		txt = txt.replace(" ", ""); // remove spaces

		try {
			switch (txt.substring(0, 2)) {
			case "DB":
				String[] strings = txt.split("\\.");
				if (strings.length < 2)
					throw new Exception();

				mDB = Integer.parseInt(strings[0].substring(2));
				String dbType = strings[1].substring(0, 3);
				int dbIndex = Integer.parseInt(strings[1].substring(3));

				switch (dbType) {
				case "DBB":
					byte obj = (byte) Read(DataType.DataBlock, mDB, dbIndex, VarType.S7Byte, 1);
					return obj;
				case "DBW":
					int objI = (int) Read(DataType.DataBlock, mDB, dbIndex, VarType.S7Word, 1);
					return objI;
				case "DBD":
					int objU = (int) Read(DataType.DataBlock, mDB, dbIndex, VarType.S7DWord, 1);
					return objU;
				case "DBX":
					mByte = dbIndex;
					mBit = Integer.parseInt(strings[2]);
					if (mBit > 7)
						throw new Exception();
					byte obj2 = (byte) Read(DataType.DataBlock, mDB, mByte, VarType.S7Byte, 1);
					return S7Boolean.GetValue(obj2, mBit);
				default:
					throw new Exception();
				}
			case "EB":
				// Input byte
				objByte = (byte) Read(DataType.Input, 0, Integer.parseInt(txt.substring(2)), VarType.S7Byte, 1);
				return objByte;
			case "EW":
				// Input word
				objShort = (short) Read(DataType.Input, 0, Integer.parseInt(txt.substring(2)), VarType.S7Word, 1);
				return objShort;
			case "ED":
				// Input double-word
				objInt = (int) Read(DataType.Input, 0, Integer.parseInt(txt.substring(2)), VarType.S7DWord, 1);
				return objInt;
			case "AB":
				// Output byte
				objByte = (byte) Read(DataType.Output, 0, Integer.parseInt(txt.substring(2)), VarType.S7Byte, 1);
				return objByte;
			case "AW":
				// Output word
				objInt = (int) Read(DataType.Output, 0, Integer.parseInt(txt.substring(2)), VarType.S7Word, 1);
				return objInt;
			case "AD":
				// Output double-word
				objInt = (int) Read(DataType.Output, 0, Integer.parseInt(txt.substring(2)), VarType.S7DWord, 1);
				return objInt;
			case "MB":
				// Memory byte
				objByte = (byte) Read(DataType.Memory, 0, Integer.parseInt(txt.substring(2)), VarType.S7Byte, 1);
				return objByte;
			case "MW":
				// Memory word
				objInt = (int) Read(DataType.Memory, 0, Integer.parseInt(txt.substring(2)), VarType.S7Word, 1);
				return objInt;
			case "MD":
				// Memory double-word
				objInt = (int) Read(DataType.Memory, 0, Integer.parseInt(txt.substring(2)), VarType.S7DWord, 1);
				return objInt;
			default:
				switch (txt.substring(0, 1)) {
				case "E":
				case "I":
					// Input
					mDataType = DataType.Input;
					break;
				case "A":
				case "O":
					// Output
					mDataType = DataType.Output;
					break;
				case "M":
					// Memory
					mDataType = DataType.Memory;
					break;
				case "T":
					// Timer
					objReal = (float) Read(DataType.Timer, 0, Integer.parseInt(txt.substring(1)), VarType.S7Timer, 1);
					return objReal;
				case "Z":
				case "C":
					// Counter
					objInt = (int) Read(DataType.Counter, 0, Integer.parseInt(txt.substring(1)), VarType.S7Counter, 1);
					return objInt;
				default:
					throw new Exception();
				}

				String txt2 = txt.substring(1);
				if (txt2.indexOf(".") == -1)
					throw new Exception();

				mByte = Integer.parseInt(txt2.substring(0, txt2.indexOf(".")));
				mBit = Integer.parseInt(txt2.substring(txt2.indexOf(".") + 1));
				if (mBit > 7)
					throw new Exception();
				byte obj3 = (byte) Read(mDataType, 0, mByte, VarType.S7Byte, 1);

				return S7Boolean.ClearBit(obj3, mBit);
			}
		} catch (Exception ex) {
			errorCode = ErrorCode.WrongVarFormat;
			String LastErrorString = "The variable'" + variable
					+ "' could not be read. Please check the syntax and try again.";
			return errorCode;
		}
	}

	public ErrorCode WriteBytes(DataType dataType, int db, int startByteAdr, byte[] value) throws IOException {
		byte[] bReceive = new byte[513];
		int varCount = 0;
		varCount = value.length;
		int packageSize = 35 + value.length;
		ByteArray packageArray = new ByteArray(packageSize);
		packageArray.Add(new byte[] { 3, 0, 0 });
		packageArray.Add((byte) packageSize);
		packageArray.Add(new byte[] { 2, (byte) 0xf0, (byte) 0x80, 0x32, 1, 0, 0 });
		packageArray.Add(S7Word.ToByteArray((varCount - 1)));
		packageArray.Add(new byte[] { 0, 0x0e });
		packageArray.Add(S7Word.ToByteArray((varCount + 4)));
		packageArray.Add(new byte[] { 0x05, 0x01, 0x12, 0x0a, 0x10, 0x02 });
		packageArray.Add(S7Word.ToByteArray(varCount));
		packageArray.Add(S7Word.ToByteArray((db)));
		packageArray.Add((byte) dataType.getIndex());

		int overflow = (int) (startByteAdr * 8 / 0xffff);

		packageArray.Add((byte) overflow);
		packageArray.Add(S7Word.ToByteArray((startByteAdr * 8)));
		packageArray.Add(new byte[] { 0, 4 });
		packageArray.Add(S7Word.ToByteArray((varCount * 8)));

		packageArray.Add(value);

		out.write(packageArray.getArray());
		int numReceived = in.read(bReceive);
		if (numReceived == -1 || bReceive[21] != -1) {
			errorCode = ErrorCode.WrongNumberReceivedBytes;

		} else {
			errorCode = ErrorCode.NoError;
		}

		return errorCode;
	}

	public Object Write(DataType dataType, int db, int startByteAdr, Object value) throws IOException {
		byte[] packageArray = null;
		System.out.println(value.getClass().getSimpleName());
		switch (value.getClass().getSimpleName()) {

		case "Byte":
			packageArray = S7Byte.ToByteArray((byte) value);
			break;
		case "Short":
			packageArray = S7Int.ToByteArray((short) value);
			break;
		case "Integer":
			packageArray = S7DInt.ToByteArray((int) value);
			break;
		case "Float":
			packageArray = S7Real.ToByteArray((Float) value);
			break;
		case "Byte[]":
			packageArray = (byte[]) value;
			break;
		case "Short[]":
			packageArray = S7Int.ToByteArray((int[]) value);
			break;
		case "Integer[]":
			packageArray = S7DInt.ToByteArray((int[]) value);
			break;
		case "Folat[]":
			packageArray = S7Real.ToByteArray((float[]) value);
			break;
		case "String":
			packageArray = S7String.ToByteArray(value.toString());
			break;
		default:
			return ErrorCode.WrongVarFormat;
		}
		return WriteBytes(dataType, db, startByteAdr, packageArray);
	}

	public Object Write(String variable, Object value) {
		DataType mDataType;
		int mDB;
		int mByte;
		int mBit;

		String addressLocation;
		byte _byte;

		String txt = variable.toUpperCase();
		txt = txt.replace(" ", ""); // Remove spaces

		try {
			switch (txt.substring(0, 2)) {
			case "DB":
				String[] strings = txt.split("\\.");
				if (strings.length < 2)
					throw new Exception();

				mDB = Integer.parseInt(strings[0].substring(2));
				String dbType = strings[1].substring(0, 3);
				int dbIndex = Integer.parseInt(strings[1].substring(3));

				switch (dbType) {
				case "DBB":
					return Write(DataType.DataBlock, mDB, dbIndex, Byte.parseByte(value.toString()));
				case "DBW":
					
					return Write(DataType.DataBlock, mDB, dbIndex, Short.parseShort(value.toString()));
				case "DBD":

					return Write(DataType.DataBlock, mDB, dbIndex, Integer.parseInt(value.toString()));
				case "DBX":
					mByte = dbIndex;
					mBit = Integer.parseInt(strings[2]);
					if (mBit > 7) {
						throw new Exception(String.format(
								"Addressing Error: You can only reference bitwise locations 0-7. Address {0} is invalid",
								mBit));
					}
					byte b = (byte) Read(DataType.DataBlock, mDB, mByte, VarType.S7Byte, 1);
					if ((boolean) value)
						b = S7Boolean.SetBit(b, mBit);// (byte)(b |
														// (byte)Math.Pow(2,
														// mBit)); // Bit setzen
					else
						b = S7Boolean.ClearBit(b, mBit); // Bit r®πcksetzen

					return Write(DataType.DataBlock, mDB, mByte, (byte) b);
				case "DBS":
					// DB-String
					return Write(DataType.DataBlock, mDB, dbIndex, value.toString());
				default:
					throw new Exception(String.format(
							"Addressing Error: Unable to parse address {0}. Supported formats include DBB (byte), DBW (word), DBD (dword), DBX (bitwise), DBS (string).",
							dbType));
				}
			case "EB":
				// Input Byte

				return Write(DataType.Input, 0, Integer.parseInt(txt.substring(2)), Byte.parseByte(value.toString()));
			case "EW":
				// Input Word

				return Write(DataType.Input, 0, Integer.parseInt(txt.substring(2)), Short.parseShort(value.toString()));
			case "ED":
				// Input Double-Word

				return Write(DataType.Input, 0, Integer.parseInt(txt.substring(2)), Integer.parseInt(value.toString()));
			case "AB":
				// Output Byte

				return Write(DataType.Output, 0, Integer.parseInt(txt.substring(2)),  Byte.parseByte(value.toString()));
			case "AW":
				// Output Word

				return Write(DataType.Output, 0, Integer.parseInt(txt.substring(2)), Short.parseShort(value.toString()));
			case "AD":
				// Output Double-Word

				return Write(DataType.Output, 0, Integer.parseInt(txt.substring(2)), Integer.parseInt(value.toString()));
			case "MB":
				// Memory Byte

				return Write(DataType.Memory, 0, Integer.parseInt(txt.substring(2)), Byte.parseByte(value.toString()));
			case "MW":
				// Memory Word

				return Write(DataType.Memory, 0, Integer.parseInt(txt.substring(2)), Short.parseShort(value.toString()));
			case "MD":
				// Memory Double-Word
				return Write(DataType.Memory, 0, Integer.parseInt(txt.substring(2)), value);
			default:
				switch (txt.substring(0, 1)) {
				case "E":
				case "I":
					// Input
					mDataType = DataType.Input;
					break;
				case "A":
				case "O":
					// Output
					mDataType = DataType.Output;
					break;
				case "M":
					// Memory
					mDataType = DataType.Memory;
					break;
				case "T":
					// Timer
					return Write(DataType.Timer, 0, Integer.parseInt(txt.substring(1)), Double.parseDouble(value.toString()));
				case "Z":
				case "C":
					// Counter
					return Write(DataType.Counter, 0, Integer.parseInt(txt.substring(1)), Short.parseShort(value.toString()));
				default:
					throw new Exception(String.format("Unknown variable type {0}.", txt.substring(0, 1)));
				}

				addressLocation = txt.substring(1);
				int decimalPointIndex = addressLocation.indexOf(".");
				if (decimalPointIndex == -1) {
					throw new Exception(String.format(
							"Cannot parse variable {0}. Input, Output, Memory Address, Timer, and Counter types require bit-level addressing (e.g. I0.1).",
							addressLocation));
				}

				mByte = Integer.parseInt(addressLocation.substring(0, decimalPointIndex));
				mBit = Integer.parseInt(addressLocation.substring(decimalPointIndex + 1));
				if (mBit > 7) {
					throw new Exception(String.format(
							"Addressing Error: You can only reference bitwise locations 0-7. Address {0} is invalid",
							mBit));
				}

				_byte = (byte) Read(mDataType, 0, mByte, VarType.S7Byte, 1);
				if ((int) value == 1)
					_byte = S7Boolean.SetBit(_byte, mBit);// (byte)(b |
															// (byte)Math.Pow(2,
															// mBit)); // Bit
															// setzen
				else
					_byte = S7Boolean.ClearBit(_byte, mBit); // Bit r®πcksetzen
				return Write(mDataType, 0, mByte, (byte) _byte);
			}
		} catch (Exception ex) {
			errorCode = ErrorCode.WrongVarFormat;

			return errorCode;
		}
	}
}
