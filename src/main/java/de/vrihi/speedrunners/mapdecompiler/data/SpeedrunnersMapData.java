package de.vrihi.speedrunners.mapdecompiler.data;

import de.vrihi.speedrunners.mapdecompiler.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;


/**
 * Class representing the main structure of a map.
 * <p>It consist of several public variables:</p>
 * <ul>
 *     <li>{@link #mapFormat}</li>
 *     <li>{@link #entities}</li>
 *     <li>{@link #layers}</li>
 *     <li>{@link #theme}</li>
 * </ul>
 * <br>
 * <p>Optionally:</p>
 * <ul>
 *     <li>{@link #author}</li>
 *     <li>{@link #mapName}</li>
 *     <li>{@link #workshopId}</li>
 * </ul>
 *
 *
 * <br>
 * <p>{@link #author}, {@link #mapName} and {@link #workshopId}, are only present, if the map was uploaded to the workshop.</p>
 */
public class SpeedrunnersMapData
{
	/**
	 * The map format version. (Seemed to be always 6?)
	 * <p>This is not confirmed, but very likely as it is the first variable</p>
	 */
	public long mapFormat;
	public Entity[] entities;
	public Layer[] layers;

	public String theme;

	/**
	 * The workshops author name
	 */
	public String author;

	/**
	 * The Workshops map name
	 */
	public String mapName;

	/**
	 * The workshop id, saved as an <b>signed</b> long.
	 * <p>When using this value, always make sure to first convert the value to a unsigned long!
	 *
	 * @see #getWorkshopId()
	 */
	public long workshopId;	    // Workshop id as an !!!unsigned long!!!

	/**
	 * Constructs a new instance of this class using default values
	 */
	public SpeedrunnersMapData()
	{
		this(0, new Entity[0], new Layer[0], "");
	}

	/**
	 * Constructs a new instance of this class with the supplied variables.
	 * Remaining variables are set to their default empty value
	 *
	 * @param mapFormat <b>Probably</b> the map version
	 * @param entities An array of entities
	 * @param layers An array of Layers
	 * @param theme The theme (asset palette) that is used for this map
	 */
	public SpeedrunnersMapData(long mapFormat, Entity[] entities, Layer[] layers, String theme)
	{
		this(mapFormat, entities, layers, theme, "", "", 0);
	}

	/**
	 * Constructs a new instance of this object with the supplied variables.
	 *
	 * @param mapFormat <b>Probably</b> the map format version
	 * @param entities An array of entities
	 * @param layers An array of Layers
	 * @param theme The theme (asset palette) that is used for this map
	 * @param author The name of the workshop author
	 * @param mapName The name of the map
	 * @param workshopId Steams workshop id that links to the uploaded version of this map. !!Treated as unsigned long!! (https://steamcommunity.com/sharedfiles/filedetails/?id=...)
	 */
	public SpeedrunnersMapData(long mapFormat, Entity[] entities, Layer[] layers, String theme, String author, String mapName, long workshopId)
	{
		Objects.requireNonNull(entities);
		Objects.requireNonNull(layers);
		Objects.requireNonNull(theme);
		Objects.requireNonNull(author);

		this.mapFormat = mapFormat;
		this.entities = entities;
		this.layers = layers;
		this.theme = theme;
		this.author = author;
		this.mapName = mapName;
		this.workshopId = workshopId;
	}

	/**
	 * Reads map information from an {@link InputStream} and stores it
	 *
	 * @param stream info as non compressed byte stream
	 * @throws IOException if an I/O error occurs
	 */
	public void read(InputStream stream) throws IOException
	{
		mapFormat = Util.readUnsignedInt(stream);

		long elementCount = Util.readUnsignedInt(stream);
		entities = new Entity[Math.toIntExact(elementCount)];

		for (int i = 0; i < entities.length; i++)
		{
			entities[i] = new Entity();
			entities[i].read(stream);
		}

		long layerCount = Util.readUnsignedInt(stream);
		layers = new Layer[Math.toIntExact(layerCount)];

		for (int i = 0; i < layers.length; i++)
		{
			layers[i] = new Layer();
			layers[i].read(stream);
		}

		theme = Util.readCString(stream);

		if (stream.available() >= 3 && Util.read8BitUInt(stream) == 0x00)
		{
			author = Util.readCString(stream);
			mapName = Util.readCString(stream);
		} else {
			mapName = "";
			author = "";
		}

		if (stream.available() >= 9 && Util.read8BitUInt(stream) == 0x00)
		{
			workshopId = Util.readLong(stream);
		} else {
			workshopId = 0;
		}
	}

	/**
	 * Writes map information into an {@link OutputStream}
	 *
	 * @param stream {@link OutputStream} in which the data gets written
	 * @param writeWorkshop if workshop properties (author, mapName, workshopId) should be written
	 * @throws IOException if an I/O error occurs
	 */
	public void writeStream(OutputStream stream, boolean writeWorkshop) throws IOException
	{
		Util.writeUnsignedInt(stream, mapFormat);

		Util.writeUnsignedInt(stream, entities.length);
		for (Entity entity: entities)
		{
			entity.writeStream(stream);
		}

		Util.writeUnsignedInt(stream, layers.length);
		for (Layer layer: layers)
		{
			layer.writeStream(stream);
		}

		Util.writeCString(stream, theme);

		stream.write(0);
		if (writeWorkshop)
		{
			Util.writeCString(stream, author);
			Util.writeCString(stream, mapName);
			stream.write(0);
			Util.writeLong(stream, workshopId);
		}
	}

	public String getWorkshopId()
	{
		return Long.toUnsignedString(workshopId);
	}

	@Override
	public String toString()
	{
		return new StringJoiner(", ", SpeedrunnersMapData.class.getSimpleName() + "[", "]")
				.add("mapFormat=" + mapFormat)
				.add("entities=" + Arrays.toString(entities))
				.add("layers=" + Arrays.toString(layers))
				.add("theme=" + theme)
				.add("author='" + author + "'")
				.add("mapName='" + mapName + "'")
				.add("workshopId=" + getWorkshopId())
				.toString();
	}
}
