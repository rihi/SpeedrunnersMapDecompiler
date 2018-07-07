# SpeedrunnersMapDecompiler
A basic java library for reading and writing maps from the game [Speedrunners](https://store.steampowered.com/app/207140).

## Usage
The Library exposes the class `Converter` with two methods `read` and `write`.

**Example:**
```java
// Reading
Path inputPath = Paths.get("map.sr");
SpeedrunnersMapData mapData = Converter.read(inputPath);

// Writing
Path outputPath = Paths.get("newMap.sr");
Converter.write(mapData, outputPath, false)
```