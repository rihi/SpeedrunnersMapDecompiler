package de.vrihi.speedrunners.mapdecompiler;

import de.vrihi.speedrunners.mapdecompiler.data.SpeedrunnersMapData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
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
	 */
	public static SpeedrunnersMapData read(Path path) throws IOException
	{
		if (Files.isDirectory(path) || Files.notExists(path))
			throw new FileNotFoundException("The specified path doesn't point to a file");

		return read(Files.readAllBytes(path));
	}

	/**
	 * Creates a {@link SpeedrunnersMapData} object, based on the data in the supplied byte array
	 *
	 * @param bytes A byte array of the map file
	 * @return {@link SpeedrunnersMapData}
	 * @throws IOException if an I/O error occurs
	 */
	public static SpeedrunnersMapData read(byte[] bytes) throws IOException
	{
		ByteBuffer byteData = ByteBuffer.wrap(bytes);
		if (byteData.capacity() < 4 * 4)	// 4 Unsigned integers (4 * 4bytes)
			throw new IOException("Invalid file, size to small");

		// Check if the file is compressed with gzip
		if (Byte.toUnsignedInt(byteData.get(0)) == 0x1f && Byte.toUnsignedInt(byteData.get(1)) == 0x8b && Byte.toUnsignedInt(byteData.get(2)) == 0x08 && Byte.toUnsignedInt(byteData.get(3)) == 0x00)
		{
			try (GZIPInputStream gzipIn = new GZIPInputStream(new ByteArrayInputStream(byteData.array()));
				 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream())
			{
				int res1 = 0;
				byte[] buf = new byte[1024];
				while (res1 >= 0)
				{
					res1 = gzipIn.read(buf, 0, buf.length);
					if (res1 > 0)
						byteArrayOutputStream.write(buf, 0, res1);
				}

				byteData = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
			}
		}

		try (ByteArrayInputStream byteIn = new ByteArrayInputStream(byteData.array()))
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
			map.writeStream(byteStream, writeWorkshop);
			ByteArrayOutputStream compressedData = new ByteArrayOutputStream(byteStream.size());
			try (GZIPOutputStream gzipOut = new GZIPOutputStream(compressedData))
			{
				gzipOut.write(byteStream.toByteArray());
			}

			return compressedData.toByteArray();
		}
	}

	/**
	 * Writes a {@link SpeedrunnersMapData} object to a specified path
	 *
	 * @param map A {@link SpeedrunnersMapData} object representing a map
	 * @param path A {@link Path} object representing the file that gets written
	 * @param writeWorkshop if workshop properties (author, mapName, workshopId) should be written
	 * @throws IOException if an I/O error occurs
	 */
	public static void write(SpeedrunnersMapData map, Path path, boolean writeWorkshop) throws IOException
	{
		Files.write(path, write(map, writeWorkshop));
	}
}