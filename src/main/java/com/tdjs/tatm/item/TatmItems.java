package com.tdjs.tatm.item;

import com.tdjs.tatm.block.TatmBlocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Registration class for TARDIS items
 */
public class TatmItems {
    
    public static final Item TARDIS_ITEM = new TardisItem(TatmBlocks.TARDIS_BLOCK, new FabricItemSettings());

    public static void register() {
        Registry.register(Registries.ITEM, new Identifier("tatm", "tardis"), TARDIS_ITEM);
    }
}