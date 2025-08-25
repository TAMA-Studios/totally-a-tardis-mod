package com.tdjs.tatm.tardis;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Represents a single TARDIS instance with its own dimensional space.
 * Each TARDIS has a unique ID, exterior location, and interior dimension.
 */
public class TardisInstance {
    private final UUID tardisId;
    private BlockPos exteriorPos;
    private Identifier exteriorDimension;
    private BlockPos interiorPos;
    private Identifier interiorDimension;
    private boolean initialized;

    public TardisInstance(UUID tardisId) {
        this.tardisId = tardisId;
        this.initialized = false;
    }

    public TardisInstance(UUID tardisId, BlockPos exteriorPos, Identifier exteriorDimension) {
        this.tardisId = tardisId;
        this.exteriorPos = exteriorPos;
        this.exteriorDimension = exteriorDimension;
        this.initialized = false;
    }

    /**
     * Initialize the TARDIS interior dimension and position
     */
    public void initializeInterior(BlockPos interiorPos, Identifier interiorDimension) {
        this.interiorPos = interiorPos;
        this.interiorDimension = interiorDimension;
        this.initialized = true;
    }

    /**
     * Check if this TARDIS is fully initialized with both exterior and interior
     */
    public boolean isInitialized() {
        return initialized && exteriorPos != null && exteriorDimension != null 
               && interiorPos != null && interiorDimension != null;
    }

    // Getters
    public UUID getTardisId() { return tardisId; }
    public BlockPos getExteriorPos() { return exteriorPos; }
    public Identifier getExteriorDimension() { return exteriorDimension; }
    public BlockPos getInteriorPos() { return interiorPos; }
    public Identifier getInteriorDimension() { return interiorDimension; }

    // Setters for exterior (when TARDIS moves)
    public void setExteriorLocation(BlockPos pos, Identifier dimension) {
        this.exteriorPos = pos;
        this.exteriorDimension = dimension;
    }

    /**
     * Serialize this TARDIS instance to NBT for persistence
     */
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid("tardis_id", tardisId);
        nbt.putBoolean("initialized", initialized);
        
        if (exteriorPos != null) {
            nbt.putLong("exterior_pos", exteriorPos.asLong());
        }
        if (exteriorDimension != null) {
            nbt.putString("exterior_dimension", exteriorDimension.toString());
        }
        if (interiorPos != null) {
            nbt.putLong("interior_pos", interiorPos.asLong());
        }
        if (interiorDimension != null) {
            nbt.putString("interior_dimension", interiorDimension.toString());
        }
        
        return nbt;
    }

    /**
     * Deserialize a TARDIS instance from NBT
     */
    public static TardisInstance fromNbt(NbtCompound nbt) {
        UUID tardisId = nbt.getUuid("tardis_id");
        TardisInstance instance = new TardisInstance(tardisId);
        
        instance.initialized = nbt.getBoolean("initialized");
        
        if (nbt.contains("exterior_pos")) {
            instance.exteriorPos = BlockPos.fromLong(nbt.getLong("exterior_pos"));
        }
        if (nbt.contains("exterior_dimension")) {
            instance.exteriorDimension = new Identifier(nbt.getString("exterior_dimension"));
        }
        if (nbt.contains("interior_pos")) {
            instance.interiorPos = BlockPos.fromLong(nbt.getLong("interior_pos"));
        }
        if (nbt.contains("interior_dimension")) {
            instance.interiorDimension = new Identifier(nbt.getString("interior_dimension"));
        }
        
        return instance;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TardisInstance that = (TardisInstance) obj;
        return tardisId.equals(that.tardisId);
    }

    @Override
    public int hashCode() {
        return tardisId.hashCode();
    }

    @Override
    public String toString() {
        return "TardisInstance{" +
                "tardisId=" + tardisId +
                ", exteriorPos=" + exteriorPos +
                ", exteriorDimension=" + exteriorDimension +
                ", interiorPos=" + interiorPos +
                ", interiorDimension=" + interiorDimension +
                ", initialized=" + initialized +
                '}';
    }
}