package com.tdjs.tatm.block;

import com.tdjs.tatm.tardis.TardisInstance;
import com.tdjs.tatm.tardis.TardisDimensionManager;
import com.tdjs.tatm.tardis.TardisRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * The TARDIS block that players interact with in the world.
 * Handles right-click to enter TARDIS and manages the exterior appearance.
 */
public class TardisBlock extends BlockWithEntity {

    public TardisBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.FAIL;
        }

        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return ActionResult.FAIL;
        }

        // Get the TARDIS at this location
        TardisRegistry registry = TardisRegistry.get(serverWorld);
        TardisInstance tardis = registry.getTardisByExteriorLocation(pos, world.getRegistryKey().getValue());

        if (tardis == null) {
            // No TARDIS here, this shouldn't happen
            player.sendMessage(Text.literal("No TARDIS found at this location!"), false);
            return ActionResult.FAIL;
        }

        if (!tardis.isInitialized()) {
            // TARDIS interior not yet initialized
            player.sendMessage(Text.literal("TARDIS is not ready yet!"), false);
            return ActionResult.FAIL;
        }

        // Attempt to teleport player into TARDIS
        boolean success = TardisDimensionManager.teleportIntoTardis(serverPlayer, tardis);
        
        if (success) {
            player.sendMessage(Text.literal("Welcome aboard the TARDIS!"), false);
            return ActionResult.SUCCESS;
        } else {
            player.sendMessage(Text.literal("Failed to enter TARDIS!"), false);
            return ActionResult.FAIL;
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            // Handle TARDIS destruction
            TardisRegistry registry = TardisRegistry.get(serverWorld);
            TardisInstance tardis = registry.getTardisByExteriorLocation(pos, world.getRegistryKey().getValue());
            
            if (tardis != null) {
                // TODO: In a full implementation, you might want to:
                // 1. Eject any players inside the TARDIS
                // 2. Save TARDIS contents 
                // 3. Provide a way to rebuild the TARDIS
                registry.removeTardis(tardis.getTardisId());
                
                if (player != null) {
                    player.sendMessage(Text.literal("TARDIS destroyed!"), false);
                }
            }
        }
        
        super.onBreak(world, pos, state, player);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TardisBlockEntity(pos, state);
    }

    // Make the block solid but not full
    @Override
    public boolean isTranslucent(BlockState state, net.minecraft.world.BlockView world, BlockPos pos) {
        return true;
    }
}