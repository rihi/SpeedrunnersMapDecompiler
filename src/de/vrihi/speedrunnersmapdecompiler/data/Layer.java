package de.vrihi.speedrunnersmapdecompiler.data;

import de.vrihi.speedrunnersmapdecompiler.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Class representing a layer in a map
 * <p></p>
 * <p>A map contains multiple layers each being a 2 dimensional tile arrays</p>
 * <p>A Layer has a {@code name}, {@code width}, {@code height} and an array of {@code int} representing tiles</p>
 */
public class Layer
{
	public String name;
	public long width;
	public long height;
	public int[] tiles;

	public Layer()
	{
		this("", 0, 0, new int[0]);
	}

	/**
	 * Constructs a new object with the provided data
	 *
	 * @param name the layer name
	 * @param width width in tiles
	 * @param height height in tiles
	 * @param tiles an array containing the tile ids row by row, with a total size of width * height
	 */
	public Layer(String name, long width, long height, int[] tiles)
	{
		Objects.requireNonNull(name);
		Objects.requireNonNull(tiles);

		this.name = name;
		this.width = width;
		this.height = height;

		this.tiles = tiles.clone();
	}

	/**
	 * Constructs a new Layer, reading data from the provided {@link InputStream}
	 *
	 * @param stream info as non compressed byte stream
	 * @throws IOException if an I/O error occurs
	 */
	public void read(InputStream stream) throws IOException
	{
		name = Util.readCString(stream);

		width = Util.readUnsignedInt(stream);
		height = Util.readUnsignedInt(stream);
		tiles = new int[Math.toIntExact(width * height)];

		for (int i = 0; i < tiles.length; i++)
		{
			tiles[i] = Math.toIntExact(Util.readUnsignedInt(stream));
		}
	}

	/**
	 * Writes layer information into an {@link OutputStream}
	 *
	 * @param stream {@link OutputStream} in which the data gets written
	 * @throws IOException if an I/O error occurs
	 */
	public void writeStream(OutputStream stream) throws IOException
	{
		Util.writeCString(stream, name);

		Util.writeUnsignedInt(stream, width);
		Util.writeUnsignedInt(stream, height);

		for (int tile: tiles)
		{
			Util.writeUnsignedInt(stream, tile);
		}
	}

	@Override
	public String toString()
	{
		return new StringJoiner(", ", Layer.class.getSimpleName() + "[", "]")
				.add("name='" + name + "'")
				.add("width=" + width)
				.add("height=" + height)
				.toString();
	}
}
