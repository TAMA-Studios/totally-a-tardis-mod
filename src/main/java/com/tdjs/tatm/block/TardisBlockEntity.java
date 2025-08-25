package com.tdjs.tatm.block;

import com.tdjs.tatm.tardis.TardisInstance;
import com.tdjs.tatm.tardis.TardisDimensionManager;
import com.tdjs.tatm.tardis.TardisRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

/**
 * Block entity for the TARDIS block.
 * Stores the TARDIS UUID and handles the connection between the block and TARDIS data.
 */
public class TardisBlockEntity extends BlockEntity {
    private UUID tardisId;

    public TardisBlockEntity(BlockPos pos, BlockState state) {
        super(TatmBlockEntities.TARDIS_BLOCK_ENTITY, pos, state);
    }

    /**
     * Set the TARDIS ID for this block entity
     */
    public void setTardisId(UUID tardisId) {
        this.tardisId = tardisId;
        markDirty();
    }

    /**
     * Get the TARDIS ID
     */
    public UUID getTardisId() {
        return tardisId;
    }

    /**
     * Get the TARDIS instance associated with this block entity
     */
    public TardisInstance getTardisInstance() {
        if (tardisId == null || !(world instanceof ServerWorld serverWorld)) {
            return null;
        }

        TardisRegistry registry = TardisRegistry.get(serverWorld);
        return registry.getTardis(tardisId);
    }

    /**
     * Initialize this block entity with a new TARDIS
     */
    public void initializeWithNewTardis() {
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }

        TardisRegistry registry = TardisRegistry.get(serverWorld);
        TardisInstance tardis = registry.createTardis(pos, world.getRegistryKey().getValue());
        
        setTardisId(tardis.getTardisId());
        
        // Initialize the interior (in a full implementation, this would create the dimension)
        // For now, we'll use the overworld as a placeholder
        BlockPos interiorPos = TardisDimensionManager.generateInteriorSpawnPos();
        registry.initializeTardisInterior(tardis.getTardisId(), interiorPos, 
                                        TardisDimensionManager.createTardisDimension(tardis.getTardisId()));
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (tardisId != null) {
            nbt.putUuid("tardis_id", tardisId);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("tardis_id")) {
            tardisId = nbt.getUuid("tardis_id");
        }
    }
}