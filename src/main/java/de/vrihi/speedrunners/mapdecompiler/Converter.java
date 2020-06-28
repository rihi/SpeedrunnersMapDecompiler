package de.vrihi.speedrunners.mapdecompiler;

import de.vrihi.speedrunners.mapdecompiler.data.SpeedrunnersMapData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * <p>Class for converting Speedrunner maps to readable data objects and back to file format.</p>
 * <p>It contains two static methods for decompiling/compiling</p>
 */
public class Converter
{

	/**
	 * Reads a map file to a {@link SpeedrunnersMapData} object
	 *
	 * @param path A {@link Path} object representing the map file
	 * @return {@link SpeedrunnersMapData}
	 * @throws IOException if an I/O error occurs
	 *
	 * @deprecated This method can trivially be achieved by using {@link Files#readAllBytes(Path)}
	 */
	@Deprecated(since = "1.1", forRemoval = true)
	public static SpeedrunnersMapData read(Path path) throws IOException
	{
		return read(Files.readAllBytes(path));
	}

	/**
	 * Reads data from the supplied byte array into a new {@link SpeedrunnersMapData} instance.
	 * This method automatically determines if the data source is compressed with gzip compression.
	 *
	 * @param bytes A byte array of the map file
	 * @return {@link SpeedrunnersMapData}
	 * @throws IOException if an I/O error occurs
	 */
	public static SpeedrunnersMapData read(byte[] bytes) throws IOException
	{
		if (bytes.length < 4 * 4)	// 4 Unsigned integers (4 * 4bytes)
			throw new IOException("Invalid file, size to small");

		int magic = Short.toUnsignedInt(ByteBuffer.wrap(bytes, 0, 2)
				.order(ByteOrder.LITTLE_ENDIAN)
				.getShort());

		if (magic == GZIPInputStream.GZIP_MAGIC)
		{
			try (GZIPInputStream gzipIn = new GZIPInputStream(new ByteArrayInputStream(bytes)))
			{
				bytes = gzipIn.readAllBytes();
			}
		}

		try (ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes))
		{
			SpeedrunnersMapData map = new SpeedrunnersMapData();
			map.read(byteIn);
			return map;
		}
	}

	/**
	 * Writes a {@link SpeedrunnersMapData} object to a byte array
	 *
	 * @param map A {@link SpeedrunnersMapData} object representing a map
	 * @param writeWorkshop if workshop properties (author, mapName, workshopId) should be written
	 * @return a byte array of the specified {@code map}
	 * @throws IOException if an I/O error occurs
	 */
	public static byte[] write(SpeedrunnersMapData map, boolean writeWorkshop) throws IOException
	{
		try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream())
		{
			try (GZIPOutputStream gzipOut = new GZIPOutputStream(byteStream))
			{
				map.writeStream(gzipOut, writeWorkshop);
			}
			return byteStream.toByteArray();
		}
	}

	/**
	 * Writes a {@link SpeedrunnersMapData} object to a specified path
	 *
	 * @param map A {@link SpeedrunnersMapData} object representing a map
	 * @param path A {@link Path} object representing the file that gets written
	 * @param writeWorkshop if workshop properties (author, mapName, workshopId) should be written
	 * @throws IOException if an I/O error occurs
	 *
	 * @deprecated This method can trivially be achieved by using {@link Files#write(Path, byte[], OpenOption...)}
	 */
	@Deprecated(since = "1.1", forRemoval = true)
	public static void write(SpeedrunnersMapData map, Path path, boolean writeWorkshop) throws IOException
	{
		Files.write(path, write(map, writeWorkshop));
	}
}
