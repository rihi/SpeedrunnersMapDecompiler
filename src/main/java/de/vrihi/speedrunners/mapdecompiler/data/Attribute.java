package de.vrihi.speedrunners.mapdecompiler.data;

import de.vrihi.speedrunners.mapdecompiler.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Class representing an attribute of an entity
 * <p></p>
 * <p>An attribute consist out of a {@code key} and a {@code value}</p>
 */
public class Attribute
{
	public String key;
	public String value;

	public Attribute()
	{
		this("", "");
	}

	public Attribute(String key, String value)
	{
		Objects.requireNonNull(key);
		Objects.requireNonNull(value);

		this.key = key;
		this.value = value;
	}

	/**
	 * Reads attribute information from an {@link InputStream} and stores it into variables
	 *
	 * @param stream info as non compressed byte stream
	 * @throws IOException if an I/O error occurs
	 */
	public void read(InputStream stream) throws IOException
	{
		key = Util.readCString(stream);
		value = Util.readCString(stream);
	}

	/**
	 * Writes attribute information into an {@link OutputStream}
	 *
	 * @param stream {@link OutputStream} in which the data gets written
	 * @throws IOException if an I/O error occurs
	 */
	public void writeStream(OutputStream stream) throws IOException
	{
		Util.writeCString(stream, key);
		Util.writeCString(stream, value);
	}

	@Override
	public String toString()
	{
		return new StringJoiner(", ", Attribute.class.getSimpleName() + "[", "]")
				.add("key='" + key + "'")
				.add("value='" + value + "'")
				.toString();
	}
}
