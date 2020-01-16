package de.vrihi.speedrunners.mapdecompiler.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * Utility class providing basic read and write operations
 */
public class Util
{
	public static byte[] readBytes(byte[] buffer, InputStream byteIn) throws IOException
	{
		int offset = 0;
		int res;
		do
		{
			res = byteIn.read(buffer);
			offset += res;

			if (res == -1)
				throw new IOException("Stream is out of bytes");
		} while (offset < buffer.length);

		return buffer;
	}

	public static long readUnsignedInt(InputStream byteIn) throws IOException
	{
		return ByteBuffer.wrap(readBytes(new byte[4], byteIn))
				.order(ByteOrder.LITTLE_ENDIAN)
				.getInt() & 0xFFFFFFFFL;
	}

	public static void writeUnsignedInt(OutputStream byteOut, long value) throws IOException
	{
		ByteBuffer buf = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt((int) value);
		byteOut.write(buf.array());
	}

	public static byte readByte(InputStream byteIn) throws IOException
	{
		return readBytes(new byte[1], byteIn)[0];
	}

	public static int read8BitUInt(InputStream byteIn) throws IOException
	{
		return Byte.toUnsignedInt(readBytes(new byte[1], byteIn)[0]);
	}

	public static void write8BitInt(OutputStream byteOut, int value) throws IOException
	{
		byteOut.write(value);
	}

	public static float read32bitFloat(InputStream byteIn) throws IOException
	{
		return ByteBuffer.wrap(readBytes(new byte[4], byteIn))
				.order(ByteOrder.LITTLE_ENDIAN)
				.getFloat();
	}

	public static void write32bitFloat(OutputStream byteOut, float value) throws IOException
	{
		byteOut.write(ByteBuffer.allocate(4)
				.order(ByteOrder.LITTLE_ENDIAN)
				.putFloat(value)
				.array());
	}

	public static long readLong(InputStream byteIn) throws IOException
	{
		return ByteBuffer.wrap(readBytes(new byte[8], byteIn))
				.order(ByteOrder.LITTLE_ENDIAN)
				.getLong();
	}

	public static void writeLong(OutputStream byteOut, long value) throws IOException
	{
		byteOut.write(ByteBuffer.allocate(8)
				.order(ByteOrder.LITTLE_ENDIAN)
				.putLong(value)
				.array());
	}

	public static String read64BitInt(InputStream byteIn) throws IOException
	{
		return Long.toUnsignedString(ByteBuffer.wrap(readBytes(new byte[8], byteIn))
				.order(ByteOrder.LITTLE_ENDIAN)
				.getLong());
	}

	public static String readString(InputStream byteIn, int length) throws IOException
	{
		if (length < 0)
			throw new IllegalArgumentException("Invalid length");

		return new String(ByteBuffer.wrap(readBytes(new byte[length], byteIn)).array());
	}

	public static void writeString(OutputStream byteOut, String value) throws IOException
	{
		CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
		ByteBuffer buf = encoder.encode(CharBuffer.wrap(value.toCharArray()));

		byteOut.write(buf.array(), buf.arrayOffset(), buf.limit());
	}

	public static String readCString(InputStream byteIn) throws IOException
	{
		return readString(byteIn, read8BitUInt(byteIn));
	}

	public static void writeCString(OutputStream byteOut, String value) throws IOException
	{
		write8BitInt(byteOut, value.length());
		writeString(byteOut, value);
	}
}
