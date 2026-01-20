# FPSDisplay

A lightweight Minecraft Fabric mod for displaying real-time FPS statistics, including average FPS, 1% low, and 0.1% low values, directly on the HUD.

## Features
- Rolling window FPS calculation for smooth, accurate stats
- 1% and 0.1% low FPS metrics for performance analysis
- Highly efficient ring buffer implementation
- Thread-safe stats collection
- Simple, unobtrusive HUD overlay

## How It Works
- Each frame timestamp is recorded in a ring buffer
- Stats are periodically recalculated (average FPS, 1%/0.1% lows)
- Results are rendered on the HUD using Minecraft's font

## Installation
1. Download the latest release from the [Releases](https://github.com/yourusername/FPSDisplay/releases) page
2. Place the JAR file in your Minecraft `mods` folder
3. Launch Minecraft with Fabric Loader

## Usage
- The FPS stats will appear above the hotbar
- No configuration required

## Development
### Build
```sh
./gradlew build
```
The output JAR will be in `build/libs/`.

### Project Structure
- `src/client/java/net/honeyberries/FPSStats.java`: FPS calculation logic
- `src/client/java/net/honeyberries/FPSRenderer.java`: HUD rendering logic
- `src/client/java/net/honeyberries/FPSDisplayClient.java`: Mod initialization and HUD registration

## Thread Safety
- All stats updates are synchronized for atomicity
- Volatile fields ensure visibility across threads

## License
This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

## Credits
- Inspired by performance metrics in PC gaming
- Built with [Fabric API](https://fabricmc.net/)

---

For questions or contributions, open an issue or pull request on GitHub.
