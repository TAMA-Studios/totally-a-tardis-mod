package com.tdjs.tatm.tardis;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry that manages all TARDIS instances in the world.
 * Handles persistence, creation, and retrieval of TARDIS data.
 */
public class TardisRegistry extends PersistentState {
    private static final String DATA_NAME = "tardis_registry";
    
    // Maps TARDIS UUID to TardisInstance
    private final Map<UUID, TardisInstance> tardisInstances = new ConcurrentHashMap<>();
    
    // Maps exterior location to TARDIS UUID for quick lookup
    private final Map<String, UUID> exteriorLocationMap = new ConcurrentHashMap<>();
    
    // Maps interior dimension to TARDIS UUID
    private final Map<Identifier, UUID> interiorDimensionMap = new ConcurrentHashMap<>();

    public TardisRegistry() {
        super();
    }

    /**
     * Get the TARDIS registry for a world
     */
    public static TardisRegistry get(ServerWorld world) {
        PersistentStateManager persistentStateManager = world.getPersistentStateManager();
        return persistentStateManager.getOrCreate(
            TardisRegistry::createFromNbt,
            TardisRegistry::new,
            DATA_NAME
        );
    }

    /**
     * Create a new TARDIS at the specified location
     */
    public TardisInstance createTardis(BlockPos exteriorPos, Identifier exteriorDimension) {
        UUID tardisId = UUID.randomUUID();
        TardisInstance tardis = new TardisInstance(tardisId, exteriorPos, exteriorDimension);
        
        tardisInstances.put(tardisId, tardis);
        updateExteriorLocationMap(tardis);
        
        markDirty();
        return tardis;
    }

    /**
     * Register an existing TARDIS instance
     */
    public void registerTardis(TardisInstance tardis) {
        tardisInstances.put(tardis.getTardisId(), tardis);
        updateExteriorLocationMap(tardis);
        if (tardis.getInteriorDimension() != null) {
            interiorDimensionMap.put(tardis.getInteriorDimension(), tardis.getTardisId());
        }
        markDirty();
    }

    /**
     * Remove a TARDIS from the registry
     */
    public void removeTardis(UUID tardisId) {
        TardisInstance tardis = tardisInstances.remove(tardisId);
        if (tardis != null) {
            removeFromLocationMaps(tardis);
            markDirty();
        }
    }

    /**
     * Get a TARDIS by its UUID
     */
    public TardisInstance getTardis(UUID tardisId) {
        return tardisInstances.get(tardisId);
    }

    /**
     * Get a TARDIS by its exterior location
     */
    public TardisInstance getTardisByExteriorLocation(BlockPos pos, Identifier dimension) {
        String locationKey = createLocationKey(pos, dimension);
        UUID tardisId = exteriorLocationMap.get(locationKey);
        return tardisId != null ? tardisInstances.get(tardisId) : null;
    }

    /**
     * Get a TARDIS by its interior dimension
     */
    public TardisInstance getTardisByInteriorDimension(Identifier dimension) {
        UUID tardisId = interiorDimensionMap.get(dimension);
        return tardisId != null ? tardisInstances.get(tardisId) : null;
    }

    /**
     * Get all TARDIS instances
     */
    public Collection<TardisInstance> getAllTardises() {
        return Collections.unmodifiableCollection(tardisInstances.values());
    }

    /**
     * Update the exterior location of a TARDIS (when it moves)
     */
    public void updateTardisExteriorLocation(UUID tardisId, BlockPos newPos, Identifier newDimension) {
        TardisInstance tardis = tardisInstances.get(tardisId);
        if (tardis != null) {
            // Remove old location mapping
            removeFromExteriorLocationMap(tardis);
            
            // Update TARDIS location
            tardis.setExteriorLocation(newPos, newDimension);
            
            // Add new location mapping
            updateExteriorLocationMap(tardis);
            
            markDirty();
        }
    }

    /**
     * Initialize the interior of a TARDIS
     */
    public void initializeTardisInterior(UUID tardisId, BlockPos interiorPos, Identifier interiorDimension) {
        TardisInstance tardis = tardisInstances.get(tardisId);
        if (tardis != null) {
            tardis.initializeInterior(interiorPos, interiorDimension);
            interiorDimensionMap.put(interiorDimension, tardisId);
            markDirty();
        }
    }

    // Helper methods
    private void updateExteriorLocationMap(TardisInstance tardis) {
        if (tardis.getExteriorPos() != null && tardis.getExteriorDimension() != null) {
            String locationKey = createLocationKey(tardis.getExteriorPos(), tardis.getExteriorDimension());
            exteriorLocationMap.put(locationKey, tardis.getTardisId());
        }
    }

    private void removeFromExteriorLocationMap(TardisInstance tardis) {
        if (tardis.getExteriorPos() != null && tardis.getExteriorDimension() != null) {
            String locationKey = createLocationKey(tardis.getExteriorPos(), tardis.getExteriorDimension());
            exteriorLocationMap.remove(locationKey);
        }
    }

    private void removeFromLocationMaps(TardisInstance tardis) {
        removeFromExteriorLocationMap(tardis);
        if (tardis.getInteriorDimension() != null) {
            interiorDimensionMap.remove(tardis.getInteriorDimension());
        }
    }

    private String createLocationKey(BlockPos pos, Identifier dimension) {
        return dimension.toString() + ":" + pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    // Persistence methods
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList tardisesList = new NbtList();
        
        for (TardisInstance tardis : tardisInstances.values()) {
            tardisesList.add(tardis.toNbt());
        }
        
        nbt.put("tardises", tardisesList);
        return nbt;
    }

    public static TardisRegistry createFromNbt(NbtCompound nbt) {
        TardisRegistry registry = new TardisRegistry();
        
        NbtList tardisesList = nbt.getList("tardises", 10); // 10 = NBT compound type
        for (int i = 0; i < tardisesList.size(); i++) {
            NbtCompound tardisNbt = tardisesList.getCompound(i);
            TardisInstance tardis = TardisInstance.fromNbt(tardisNbt);
            registry.registerTardis(tardis);
        }
        
        return registry;
    }
}