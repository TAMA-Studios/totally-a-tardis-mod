package com.tdjs.tatm.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Registration class for TARDIS blocks
 */
public class TatmBlocks {
    
    public static final Block TARDIS_BLOCK = new TardisBlock(
        FabricBlockSettings.of(Material.METAL)
            .strength(4.0f)
            .requiresTool()
            .nonOpaque()
    );

    public static void register() {
        Registry.register(Registries.BLOCK, new Identifier("tatm", "tardis"), TARDIS_BLOCK);
    }
}