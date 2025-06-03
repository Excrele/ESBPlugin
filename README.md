# Excrele's System Booster (ESB)

<<<<<<< HEAD
Excrele's System Booster (ESB) is a Spigot plugin designed for Minecraft server administrators to monitor and optimize server performance. Updated for Spigot 1.21.5 (version 1.2), it provides a user-friendly in-game GUI to access tools for server management, including performance metrics, entity and mob scanning, ground clutter management, redstone detection, chunk corruption scanning, chunk regeneration, and a new feature to kill mobs within 20 blocks of the player. This version includes fixes for accurate detection of copper bulb variants in the redstone scan and uses `EntityType.ITEM` for ground clutter management. All commands and features are protected by permissions, with asynchronous operations for most scans to enhance performance.
=======
Excrele's System Booster (ESB) is a Spigot plugin designed for Minecraft server administrators to monitor and optimize server performance. Updated for Spigot 1.21.5, it provides a user-friendly in-game GUI to access tools for server management, including performance metrics, entity and mob scanning, ground clutter management, redstone detection, chunk corruption scanning, and chunk regeneration. This version includes fixes for accurate detection of copper bulb variants in the redstone scan and uses `EntityType.ITEM` for ground clutter management. All commands and features are protected by permissions, with asynchronous operations to enhance performance.
>>>>>>> 790c8f167098c50ef09ce2527bd1b0bb1d22d660

## Features

- **Server Information**: Displays RAM allocation and usage, CPU utilization (using modern metrics), loaded chunks, entities, mobs, connected players, and server TPS (ticks per second).
- **Entity Scan**: Identifies the top 5 chunks with the most entities, listing their coordinates and counts (asynchronous).
- **Mob Scan**: Identifies the top 5 chunks with the most mobs, listing their coordinates and counts (asynchronous).
<<<<<<< HEAD
- **Kill Nearby Mobs**: Kills all mobs within 20 blocks of the player, excluding players and non-living entities.
=======
>>>>>>> 790c8f167098c50ef09ce2527bd1b0bb1d22d660
- **Ground Clutter Management**: Scans for dropped items (`EntityType.ITEM`), assigns a 5-minute despawn timer, and displays a countdown above each item.
- **Redstone Scan**: Identifies the top 5 chunks with the most redstone components, including all copper bulb variants (e.g., `COPPER_BULB`, `EXPOSED_COPPER_BULB`) introduced in 1.21 (asynchronous).
- **Chunk Corruption Scan**: Detects corrupted chunks across loaded worlds with reasons for suspected corruption (asynchronous).
- **Chunk Regeneration**: Checks if the player’s current chunk is corrupted and regenerates it to its original seed state if necessary (asynchronous).
- **GUI Interface**: All features are accessible via an intuitive in-game GUI, opened with the `/esb` command.
- **Permission-Based Access**: All commands and GUI actions require specific permissions, ensuring only authorized users can perform actions.

## Requirements

To use ESB on your Minecraft server, ensure the following:

- **Minecraft Version**: Spigot 1.21.5.
- **Server Software**: Spigot or a Spigot-compatible server (e.g., Paper, CraftBukkit).
- **Java Version**: Java 17 or later (required for Spigot 1.21.5).
- **Permissions Plugin**: A permissions plugin like LuckPerms is recommended to manage permissions.
- **Server Access**: Operator (op) status or appropriate permissions to use the plugin’s features.

## Installation

1. **Download or Build the Plugin**:
   - Download the precompiled `ExcrelesSystemBooster.jar` from the release page (if available) or build it from source.
   - To build from source:
     - Clone the repository or download the source files (`ESBPlugin.java` and `plugin.yml`).
     - Set up a Java project in an IDE (e.g., IntelliJ IDEA, Eclipse).
     - Add the Spigot 1.21.5 API as a dependency. Example Maven dependency:
       ```xml
       <dependency>
           <groupId>org.spigotmc</groupId>
           <artifactId>spigot-api</artifactId>
           <version>1.21.5-R0.1-SNAPSHOT</version>
           <scope>provided</scope>
       </dependency>
       ```
     - Add Spigot’s repository to your build system:
       ```xml
       <repository>
           <id>spigot-repo</id>
           <url>https://hub.spigotmc.org/nexus/content/repositories/snapshots/</url>
       </repository>
       ```
     - Place `ESBPlugin.java` in `src/com/excrele/esb/` and `plugin.yml` in the project root.
     - Ensure the `com.sun.management` package is available (included in standard Java distributions).
     - Build the project to generate `ExcrelesSystemBooster.jar`.

2. **Install the Plugin**:
   - Place the `ExcrelesSystemBooster.jar` file in the `plugins` folder of your Spigot 1.21.5 server.
   - Restart the server or use `/reload` to load the plugin.

3. **Verify Installation**:
   - Check the server console for the message: `Excrele's System Booster enabled for Spigot 1.21.5!`.
   - In-game, use the `/esb` command to confirm the plugin is working (requires `esb.use` permission).

## Usage

### Command
- **Command**: `/esb`
- **Description**: Opens the ESB Control Panel GUI.
- **Permission**: `esb.use` (defaults to operators only).

### GUI Navigation
After running `/esb`, a GUI with 27 slots opens, containing the following options:
1. **Server Information** (Paper icon): Displays server metrics like RAM, CPU (using modern metrics), chunks, entities, mobs, players, and TPS.
   - Permission: `esb.serverinfo`
2. **Scan Entities** (Compass icon): Lists the top 5 chunks with the most entities (asynchronous).
   - Permission: `esb.entityscan`
3. **Scan Mobs** (Zombie Head icon): Lists the top 5 chunks with the most mobs (asynchronous).
   - Permission: `esb.mobscan`
<<<<<<< HEAD
4. **Kill Nearby Mobs** (Skeleton Skull icon): Kills all mobs within 20 blocks of the player.
   - Permission: `esb.killnearbymobs`
5. **Manage Ground Clutter** (Dropper icon): Scans for dropped items (`EntityType.ITEM`) and sets a 5-minute despawn timer with a visible countdown.
   - Permission: `esb.groundclutter`
6. **Scan Active Redstone** (Redstone icon): Lists the top 5 chunks with the most redstone components, including all copper bulb variants (asynchronous).
   - Permission: `esb.redstonescan`
7. **Scan Chunk Corruption** (Barrier icon): Lists all corrupted chunks with reasons for corruption (asynchronous).
   - Permission: `esb.chunkcorruption`
8. **Regenerate Current Chunk** (Grass Block icon): Checks and regenerates the player’s current chunk if corrupted (asynchronous).
=======
4. **Manage Ground Clutter** (Dropper icon): Scans for dropped items (`EntityType.ITEM`) and sets a 5-minute despawn timer with a visible countdown.
   - Permission: `esb.groundclutter`
5. **Scan Active Redstone** (Redstone icon): Lists the top 5 chunks with the most redstone components, including all copper bulb variants (asynchronous).
   - Permission: `esb.redstonescan`
6. **Scan Chunk Corruption** (Barrier icon): Lists all corrupted chunks with reasons for corruption (asynchronous).
   - Permission: `esb.chunkcorruption`
7. **Regenerate Current Chunk** (Grass Block icon): Checks and regenerates the player’s current chunk if corrupted (asynchronous).
>>>>>>> 790c8f167098c50ef09ce2527bd1b0bb1d22d660
   - Permission: `esb.regeneratechunk`

Click an item in the GUI to execute the corresponding action. If you lack the required permission, a message will inform you.

### Permissions
The plugin defines the following permissions, all defaulting to `op`:
- `esb.use`: Allows use of the `/esb` command to open the GUI.
- `esb.serverinfo`: Allows viewing server information.
- `esb.entityscan`: Allows scanning for entities.
- `esb.mobscan`: Allows scanning for mobs.
<<<<<<< HEAD
- `esb.killnearbymobs`: Allows killing mobs within 20 blocks of the player.
=======
>>>>>>> 790c8f167098c50ef09ce2527bd1b0bb1d22d660
- `esb.groundclutter`: Allows managing ground clutter.
- `esb.redstonescan`: Allows scanning for redstone components.
- `esb.chunkcorruption`: Allows scanning for chunk corruption.
- `esb.regeneratechunk`: Allows regenerating chunks.

To assign permissions, use a permissions plugin like LuckPerms. Example command:
```
/lp user <player> permission set esb.use true
```

## How It Works

### Technical Overview
- **Language**: Written in Java for compatibility with the Spigot 1.21.5 API.
- **GUI**: Uses a custom `InventoryHolder` to create a 27-slot inventory GUI, with each slot representing a feature.
- **Event Handling**: Listens for `InventoryClickEvent` to process GUI interactions, ensuring clicks are cancelled to prevent item manipulation.
- **Permissions**: All features are gated behind permissions checked via Spigot’s permission system.
- **Timers**: A `BukkitRunnable` runs every second to manage ground clutter despawn timers and update item countdown displays.
- **Asynchronous Operations**: Entity, mob, redstone, and corruption scans, as well as chunk regeneration, run asynchronously to prevent lag, with results sent back to the main thread for safe messaging.
- **CPU Metrics**: Uses `com.sun.management.OperatingSystemMXBean.getCpuLoad()` for accurate CPU usage, with a fallback to `getSystemLoadAverage()`.

### Feature Details
1. **Server Information**:
   - Collects data using `Runtime` for memory, `OperatingSystemMXBean` for CPU, and Spigot API for chunks, entities, mobs, players, and TPS.
   - Displays metrics in a formatted chat message, with CPU usage shown as “N/A” if unavailable.
2. **Entity Scan**:
   - Asynchronously counts entities in each loaded chunk and sorts to find the top 5.
   - Outputs chunk coordinates, world name, and entity count.
3. **Mob Scan**:
   - Asynchronously counts entities where `isAlive()` is true.
   - Outputs chunk coordinates, world name, and mob count.
<<<<<<< HEAD
4. **Kill Nearby Mobs**:
   - Uses `Player.getNearbyEntities(20, 20, 20)` to find entities within a 20-block radius.
   - Removes entities where `isAlive()` is true, excluding players.
   - Reports the number of mobs killed in a chat message.
5. **Ground Clutter Management**:
   - Scans for `EntityType.ITEM` entities (dropped items).
   - Assigns a 5-minute despawn timer, stored in a `ConcurrentHashMap`.
   - A recurring task updates item names with a countdown (e.g., “Despawns in Xs”) and removes items when the timer expires.
6. **Redstone Scan**:
   - Asynchronously counts blocks with material names containing “REDSTONE”, “PISTON”, “STICKY_PISTON”, “REPEATER”, “COMPARATOR”, or any copper bulb variant (e.g., “COPPER_BULB”, “EXPOSED_COPPER_BULB”).
   - Outputs the top 5 chunks with coordinates and component counts.
7. **Chunk Corruption Scan**:
   - Asynchronously attempts to access block data in each chunk; exceptions indicate potential corruption.
   - Lists corrupted chunks with coordinates, world name, and error message.
8. **Chunk Regeneration**:
=======
4. **Ground Clutter Management**:
   - Scans for `EntityType.ITEM` entities (dropped items).
   - Assigns a 5-minute despawn timer, stored in a `ConcurrentHashMap`.
   - A recurring task updates item names with a countdown (e.g., “Despawns in Xs”) and removes items when the timer expires.
5. **Redstone Scan**:
   - Asynchronously counts blocks with material names containing “REDSTONE”, “PISTON”, “STICKY_PISTON”, “REPEATER”, “COMPARATOR”, or any copper bulb variant (e.g., “COPPER_BULB”, “EXPOSED_COPPER_BULB”).
   - Outputs the top 5 chunks with coordinates and component counts.
6. **Chunk Corruption Scan**:
   - Asynchronously attempts to access block data in each chunk; exceptions indicate potential corruption.
   - Lists corrupted chunks with coordinates, world name, and error message.
7. **Chunk Regeneration**:
>>>>>>> 790c8f167098c50ef09ce2527bd1b0bb1d22d660
   - Checks the player’s current chunk for corruption by accessing block data.
   - If corrupted, asynchronously regenerates the chunk using `World.regenerateChunk()`.

## Limitations and Notes
<<<<<<< HEAD
- **Performance**: Asynchronous scanning minimizes lag, but servers with many loaded chunks may still experience delays. The “Kill Nearby Mobs” feature is synchronous but lightweight, as it only processes nearby entities. Test on a development server before production use.
- **Redstone Detection**: Counts redstone-related blocks, including all 1.21.5 copper bulb variants, but does not detect “active” redstone states due to Spigot API limitations.
- **Chunk Corruption**: The corruption check is basic, relying on exceptions when accessing block data. Advanced corruption detection may require external tools.
- **Holograms**: Ground item timers use custom names for simplicity. For true holograms, consider integrating a plugin like HolographicDisplays.
- **Kill Nearby Mobs**: Affects all living entities except players within 20 blocks. Use cautiously to avoid unintended removal of tamed or named mobs.
=======
- **Performance**: Asynchronous scanning minimizes lag, but servers with many loaded chunks may still experience delays. Test on a development server before production use.
- **Redstone Detection**: Counts redstone-related blocks, including all 1.21.5 copper bulb variants, but does not detect “active” redstone states due to Spigot API limitations.
- **Chunk Corruption**: The corruption check is basic, relying on exceptions when accessing block data. Advanced corruption detection may require external tools.
- **Holograms**: Ground item timers use custom names for simplicity. For true holograms, consider integrating a plugin like HolographicDisplays.
>>>>>>> 790c8f167098c50ef09ce2527bd1b0bb1d22d660
- **Compatibility**: Designed for Spigot 1.21.5 and compatible with Paper 1.21.5. Test thoroughly, as newer versions may introduce breaking changes.

## Troubleshooting
- **Plugin Not Loading**:
   - Check the server console for errors.
   - Ensure the Spigot API version is 1.21.5-R0.1-SNAPSHOT.
   - Verify that `plugin.yml` is correctly placed in the JAR.
- **Permission Issues**:
   - Ensure players have the necessary permissions using a permissions plugin.
<<<<<<< HEAD
   - Check that `esb.use` is granted to access the GUI and `esb.killnearbymobs` for the new feature.
=======
   - Check that `esb.use` is granted to access the GUI.
>>>>>>> 790c8f167098c50ef09ce2527bd1b0bb1d22d660
- **Performance Lag**:
   - Reduce loaded chunks or worlds if scans cause delays.
   - Schedule scans during low-traffic periods.
- **Ground Clutter Not Despawning**:
   - Ensure the server has not crashed or reloaded, interrupting the timer task.
   - Verify that items are detected as `EntityType.ITEM`.
- **CPU Usage Shows “N/A”**:
   - Some Java environments may not support `getCpuLoad()`. The fallback metric may also be unavailable on certain systems.
- **Redstone or Item Scan Errors**:
<<<<<<< HEAD
   - Ensure the Spigot 1.21.5 API is used, as older versions lack copper bulb materials or use outdated entity types.
   - Verify that the build environment includes the correct API dependency.
- **Kill Nearby Mobs Not Working**:
   - Confirm the player has the `esb.killnearbymobs` permission.
   - Ensure mobs are within a 20-block radius and are living entities (e.g., zombies, not armor stands).
=======
   - Ensure the Spigot 1.21.5 API is used, as older versions lack copper bulb materials or use outdated entity types (e.g., `DROPPED_ITEM`).
   - Verify that the build environment includes the correct API dependency.
>>>>>>> 790c8f167098c50ef09ce2527bd1b0bb1d22d660

## Contributing
Contributions are welcome! To contribute:
1. Fork the repository (if hosted).
2. Make changes to the source code.
3. Test thoroughly on a Spigot 1.21.5 server.
4. Submit a pull request with a clear description of changes.

## License
This plugin is released under the MIT License. See the `LICENSE` file for details (if included in the repository).

## Contact
For support or feature requests, contact the developer via the repository’s issues page or your preferred method (if applicable).

---
<<<<<<< HEAD
*Generated on June 2, 2025*
=======
*Generated on June 2, 2025*
>>>>>>> 790c8f167098c50ef09ce2527bd1b0bb1d22d660
