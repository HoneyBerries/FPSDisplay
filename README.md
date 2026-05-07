# FPS Display

A lightweight, highly customizable Minecraft Fabric mod that displays real-time FPS statistics directly on your HUD. Track not just average FPS, but also 1% and 0.1% low values for comprehensive performance analysis.

[![Build](https://img.shields.io/github/actions/workflow/status/HoneyBerries/FPSDisplay/.github/workflows/build.yml?branch=dev&label=build&logo=github)](https://github.com/HoneyBerries/FPSDisplay/actions)
[![Release](https://img.shields.io/github/v/release/HoneyBerries/FPSDisplay?color=brightgreen&logo=github)](https://github.com/HoneyBerries/FPSDisplay/releases)
[![License](https://img.shields.io/badge/license-GPL--3.0-blue.svg)](LICENSE)
[![Minecraft](https://img.shields.io/badge/Minecraft-26.1.2-green.svg)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/Loader-Fabric-orange.svg)](https://fabricmc.net/)

## 📖 Table of Contents
- [Features](#-features)
- [Installation](#-installation)
- [Usage](#-usage)
- [Development](#-development)
- [Contributing](#-contributing)
- [License](#-license)
- [Support & Links](#-support--links)

## ✨ Features

### Performance Metrics
- **Average FPS**: Real-time frames per second calculation
- **1% Low FPS**: Average FPS of the slowest 1% of frames
- **0.1% Low FPS**: Average FPS of the slowest 0.1% of frames
- **Rolling Window**: 2-second time window for smooth, accurate statistics
- **Efficient Implementation**: Ring buffer with minimal CPU overhead

### Customization Options
- **Position**: Freely adjust X and Y coordinates on screen
- **Scale**: Resize the HUD from 0.5x to 3.0x
- **Colors**: Full RGBA color picker for text and background
- **Text Shadow**: Optional shadow for better readability
- **Toggle Features**: Enable/disable FPS display and advanced stats independently

### Technical Highlights
- Thread-safe statistics collection with synchronized access
- Volatile fields for cross-thread visibility
- Periodic recalculation (500ms intervals) to reduce CPU usage
- Respects vanilla debug overlay (F3) and hidden GUI settings
- Integration with Fabric HUD API

## 📦 Installation

### Requirements
- **Minecraft**: 26.1.2 (exact version)
- **Fabric Loader**: 0.19.2 or newer
- **Java**: 21 or newer
- **Fabric API**: 0.148.0+ for Minecraft 26.1.2
- **YACL3 (Yet Another Config Lib)**: 3.9.3+26.1-fabric or newer (required for configuration GUI)
- **ModMenu**: 18.0.0-alpha.8 or newer (required for config access)

### Quick Installation Steps
1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 26.1.2
2. Download the following mods from [Modrinth](https://modrinth.com):
   - [Fabric API](https://modrinth.com/mod/fabric-api/versions?g=26.1.2)
   - [YACL3](https://modrinth.com/mod/yacl/versions?g=26.1.2)
   - [ModMenu](https://modrinth.com/mod/modmenu/versions?g=26.1.2)
3. Place all downloaded `.jar` files in your `.minecraft/mods` folder
4. Download the [latest FPS Display release](https://github.com/HoneyBerries/FPSDisplay/releases/latest) and place in your `mods` folder
5. Launch Minecraft

### Downloads
- **Latest Release**: [v1.4.0](https://github.com/HoneyBerries/FPSDisplay/releases/tag/v1.4.0)
- **All Releases**: [GitHub Releases](https://github.com/HoneyBerries/FPSDisplay/releases)
- **Modrinth**: [FPS Display Mod Page](https://modrinth.com/mod/fps-display)

## 🎮 Usage

### In-Game Display
Once installed, the FPS counter will automatically appear in the top-left corner of your screen (default position). The display shows:
```
FPS: 144
1% Low: 95 | 0.1% Low: 72
```

### Configuration
Access the configuration screen through **ModMenu**:
1. Open the Minecraft main menu or pause menu
2. Click "Mods" button
3. Find "FPS Display" in the mod list
4. Click the config button (gear icon) next to the mod name

#### General Settings
- **Enable FPS**: Toggle the entire FPS display on/off
- **Advanced Stats**: Show/hide 1% and 0.1% low FPS values
- **Show 'FPS' Text**: Toggle the "FPS:" label before the numerical value
- **X Offset**: Horizontal position (adjustable based on your screen resolution)
- **Y Offset**: Vertical position (adjustable based on your screen resolution)

#### Appearance Settings
- **HUD Scale**: Size multiplier (0.5x - 3.0x)
- **Text Shadow**: Enable/disable text shadow for readability
- **Text Color**: RGBA color picker for the FPS text
- **Background Color**: RGBA color picker for the background box (alpha controls transparency)

All settings are saved to `config/fps_display_config.json` and persist across game sessions.

## 🛠️ Development

### Building from Source

#### Prerequisites
- Git
- Java Development Kit (JDK) 25 or newer
- Gradle (or use the included gradle wrapper)

#### Build Steps

**Linux/macOS:**
```bash
# Clone the repository
git clone https://github.com/HoneyBerries/FPSDisplay.git
cd FPSDisplay

# Build the mod
./gradlew build

# Output JAR files will be in build/libs/
ls build/libs/
```

**Windows (PowerShell):**
```powershell
# Clone the repository
git clone https://github.com/HoneyBerries/FPSDisplay.git
cd FPSDisplay

# Build the mod
.\gradlew.bat build

# Output JAR files will be in build/libs/
```

#### Project Structure
```
FPSDisplay/
├── src/
│   ├── main/
│   │   ├── java/         # Java source code
│   │   └── resources/    # Configuration and assets
│   └── test/             # Unit tests (if applicable)
├── build.gradle.kts      # Gradle build configuration
├── gradle.properties     # Version and dependency properties
└── README.md            # This file
```


## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Code Style
- Follow existing JavaDoc documentation standards
- Use meaningful variable and method names
- Keep methods focused and single-purpose
- Add comments for complex algorithms

## 📝 License

This project is licensed under the **GNU General Public License v3.0** - see the [LICENSE](LICENSE) file for details.

### What this means:
- ✅ Free to use, modify, and distribute
- ✅ Source code must remain open
- ✅ Modifications must also be GPL-3.0
- ✅ No warranty provided

## 🙏 Credits & Acknowledgments

- **Fabric Team**: For the excellent mod loader and API
- **YACL3**: [isXander](https://github.com/isXander) for the configuration library
- **ModMenu**: [TerraformersMC](https://github.com/TerraformersMC) for mod menu integration
- **Community**: PC gaming performance metrics and tools that inspired this mod

## 📞 Support & Links

| Link | Description |
|------|-------------|
| [GitHub Issues](https://github.com/HoneyBerries/FPSDisplay/issues) | Report bugs or request features |
| [Modrinth](https://modrinth.com/mod/fps-display) | Download and community reviews |
| [GitHub Discussions](https://github.com/HoneyBerries/FPSDisplay/discussions) | Ask questions and share feedback |
| [Releases](https://github.com/HoneyBerries/FPSDisplay/releases) | Download specific versions |

## 🗺️ Roadmap

- [ ] Add more display positions presets (corners, center)
- [ ] Graph overlay for FPS over time
- [ ] Configurable update intervals
- [ ] Export statistics to file
- [ ] Custom color themes/presets

---

**Made with ❤️ for the Minecraft community**

*For bug reports, feature requests, or general questions, please open an issue on GitHub.*
