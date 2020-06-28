# SpeedrunnersMapDecompiler
A basic java library for reading and writing maps from the game [Speedrunners](https://store.steampowered.com/app/207140).

## Usage
The library contains several data type classes used in Speedrunner maps that can be located in the `de.vrihi.speedrunners.mapdecompiler.data` package. Classes are mutable, and data is exposed via public fields. Additionally data can be read into the classes using its `read(InputStream)` method and written using its `write(OutputStream)` method.

Because official map files are always compressed using gzip, this library also offers a `Converter` class for convenient reading and writing.
`Converter` has 2 methods. `read(byte[])` reads data into a new `SpeedrunnersMapData` instance, while automatically handling if the supplied data is gzip compressed. `write(SpeedrunnersMapData, boolean)` writes and gzip compresses the supplied `SpeedrunnersMapData` instance to a byte array.

**Example:**
```java
// Reading using Converter.read(byte[])
byte[] gzipMapData = ...;
SpeedrunnersMapData map1 = Converter.read(gzipMapData); // works
byte[] uncompressedMapData = ...;
SpeedrunnersMapData map2 = Converter.read(uncompressedMapData); // also works

// Reading manually
InputStream uncompressedMapInput = ...;
SpeedrunnersMapData map4 = new SpeedrunnersMapData();
map4.read(uncompressedMapInput); // Does work


// Writing using Converter.write(byte[], boolean)
SpeedrunnersMapData map5 = ...;
byte[] gzipMapDataOut = Converter.write(map5, false);

// Writing manually
SpeedrunnersMapData map6 = ...;
OutputStream uncompressedMapDataOut = ...;
map6.write(uncompressedMapDataOut);
```