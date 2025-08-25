package com.tdjs.tatm.block;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Registration class for TARDIS block entities
 */
public class TatmBlockEntities {
    
    public static final BlockEntityType<TardisBlockEntity> TARDIS_BLOCK_ENTITY = 
        BlockEntityType.Builder.create(TardisBlockEntity::new, TatmBlocks.TARDIS_BLOCK).build(null);

    public static void register() {
        Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("tatm", "tardis_block_entity"), TARDIS_BLOCK_ENTITY);
    }
}