package de.vrihi.speedrunnersmapdecompiler.data;

import de.vrihi.speedrunnersmapdecompiler.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Class representing an entity in a map
 * <p></p>
 * <p>An entity has a {@code xCoordinate}, {@code yCoordinate}, {@code width}, {@code height}, {@code elementName} and an array of {@link Attribute}'s containing additional info</p>
 */
public class Entity
{
	public float xCoordinate;
	public float yCoordinate;
	public float width;
	public float height;

	public String elementName;
	public Attribute[] attributes;

	public Entity()
	{
		this(0, 0, 0,  0, "", new Attribute[0]);
	}

	/**
	 * Constructs a new entity object with the supplied data
	 *
	 * @param xCoordinate the x coordinate of the entitiy
	 * @param yCoordinate the y coordinate of the entitiy
	 * @param width the width of the entitiy
	 * @param height the height of the entitiy
	 * @param elementName the name of the entitiy
	 * @param attributes an array of attributes of the entity
	 */
	public Entity(float xCoordinate, float yCoordinate, float width, float height, String elementName, Attribute[] attributes)
	{
		Objects.requireNonNull(elementName);
		Objects.requireNonNull(attributes);

		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordinate;
		this.width = width;
		this.height = height;
		this.elementName = elementName;
		this.attributes = attributes.clone();
	}

	/**
	 * Reads map information from an {@link InputStream} and stores it
	 *
	 * @param stream info as non compressed byte stream
	 * @throws IOException if an I/O error occurs
	 */
	public void read(InputStream stream) throws IOException
	{
		xCoordinate = Util.read32bitFloat(stream);
		yCoordinate = Util.read32bitFloat(stream);
		width = Util.read32bitFloat(stream);
		height = Util.read32bitFloat(stream);

		elementName = Util.readCString(stream);

		long attributesLength = Util.readUnsignedInt(stream);
		attributes = new Attribute[Math.toIntExact(attributesLength)];

		for (int i = 0; i < attributes.length; i++)
		{
			attributes[i] = new Attribute();
			attributes[i].read(stream);
		}
	}

	/**
	 * Writes entity information into an {@link OutputStream}
	 *
	 * @param stream {@link OutputStream} in which the data gets written
	 * @throws IOException if an I/O error occurs
	 */
	public void writeStream(OutputStream stream) throws IOException
	{
		Util.write32bitFloat(stream, xCoordinate);
		Util.write32bitFloat(stream, yCoordinate);
		Util.write32bitFloat(stream, width);
		Util.write32bitFloat(stream, height);

		Util.writeCString(stream, elementName);

		Util.writeUnsignedInt(stream, attributes.length);
		for (Attribute attribute: attributes)
		{
			attribute.writeStream(stream);
		}
	}

	@Override
	public String toString()
	{
		return new StringJoiner(", ", Entity.class.getSimpleName() + "[", "]")
				.add("xCoordinate=" + xCoordinate)
				.add("yCoordinate=" + yCoordinate)
				.add("width=" + width)
				.add("height=" + height)
				.add("elementName='" + elementName + "'")
				.add("attributes=" + Arrays.toString(attributes))
				.toString();
	}
}
