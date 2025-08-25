# TARDIS Mod Dimensional Logic

This mod implements a comprehensive dimensional logic system for TARDIS functionality in Minecraft. Each TARDIS has its own unique dimensional space that allows the iconic "bigger on the inside" experience.

## Core Components

### 1. TardisInstance (`tardis/TardisInstance.java`)
Represents a single TARDIS with:
- Unique UUID for identification
- Exterior location (position + dimension)
- Interior location (position + dimension)
- Initialization state tracking
- NBT serialization for persistence

### 2. TardisRegistry (`tardis/TardisRegistry.java`)
Manages all TARDIS instances with:
- Persistent storage using Minecraft's `PersistentState`
- Quick lookup by location, UUID, or dimension
- Thread-safe operations with `ConcurrentHashMap`
- Automatic dirty marking for data consistency

### 3. TardisDimensionManager (`tardis/TardisDimensionManager.java`)
Handles dimensional operations:
- Teleportation between exterior and interior
- Player and entity transport
- Dimension creation and management
- Safety checks for valid dimensions

### 4. TardisBlock (`block/TardisBlock.java`)
The physical TARDIS in the world:
- Right-click to enter TARDIS
- Proper destruction handling
- Integration with block entity system

### 5. TardisBlockEntity (`block/TardisBlockEntity.java`)
Connects blocks to TARDIS data:
- Stores TARDIS UUID
- Initializes new TARDIS instances
- NBT persistence

### 6. TardisExitBlock (`block/TardisExitBlock.java`)
Interior exit mechanism:
- Allows players to exit from inside
- Automatic TARDIS detection
- Safe teleportation back to exterior

## How It Works

1. **TARDIS Placement**: When a TARDIS item is placed, a new `TardisInstance` is created and registered
2. **Dimensional Separation**: Each TARDIS gets a unique dimension identifier based on its UUID
3. **Entry/Exit**: Players can enter by right-clicking the exterior block and exit using interior door blocks
4. **Persistence**: All TARDIS data persists across world saves/loads through the registry system
5. **Multi-TARDIS Support**: Unlimited TARDIS instances can exist simultaneously

## Key Features

- **Unique Dimensions**: Each TARDIS has its own pocket dimension
- **Proper Teleportation**: Handles both players and other entities
- **Data Persistence**: TARDIS locations and states survive server restarts
- **Thread Safety**: Concurrent access to TARDIS registry is safe
- **Modular Design**: Easy to extend with additional TARDIS features

## Usage

1. Place a TARDIS block in the world
2. Right-click to enter the TARDIS interior
3. Use exit blocks inside to return to the exterior
4. Each TARDIS maintains its own dimensional space

This foundation provides the core dimensional logic needed for a full TARDIS mod, with room for expansion into flight, interior customization, and advanced TARDIS features.