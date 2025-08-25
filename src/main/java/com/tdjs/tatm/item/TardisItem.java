package com.tdjs.tatm.item;

import com.tdjs.tatm.block.TardisBlockEntity;
import com.tdjs.tatm.block.TatmBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Item for placing TARDIS blocks
 */
public class TardisItem extends BlockItem {

    public TardisItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    protected boolean postPlacement(BlockPos pos, World world, net.minecraft.entity.player.PlayerEntity player, 
                                   net.minecraft.item.ItemStack stack, net.minecraft.block.BlockState state) {
        boolean result = super.postPlacement(pos, world, player, stack, state);
        
        // Initialize the TARDIS after placement
        if (result && !world.isClient) {
            if (world.getBlockEntity(pos) instanceof TardisBlockEntity tardisBlockEntity) {
                tardisBlockEntity.initializeWithNewTardis();
            }
        }
        
        return result;
    }
}