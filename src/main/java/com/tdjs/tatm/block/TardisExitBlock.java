package com.tdjs.tatm.block;

import com.tdjs.tatm.tardis.TardisInstance;
import com.tdjs.tatm.tardis.TardisDimensionManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A door block inside TARDIS that allows players to exit back to the exterior
 */
public class TardisExitBlock extends Block {

    public TardisExitBlock(Settings settings) {
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

        // Check if player is inside a TARDIS
        TardisInstance tardis = TardisDimensionManager.getTardisContaining(serverPlayer);
        
        if (tardis == null) {
            player.sendMessage(Text.literal("This exit door is not connected to a TARDIS!"), false);
            return ActionResult.FAIL;
        }

        // Attempt to teleport player out of TARDIS
        boolean success = TardisDimensionManager.teleportOutOfTardis(serverPlayer, tardis);
        
        if (success) {
            player.sendMessage(Text.literal("Exiting TARDIS..."), false);
            return ActionResult.SUCCESS;
        } else {
            player.sendMessage(Text.literal("Failed to exit TARDIS!"), false);
            return ActionResult.FAIL;
        }
    }
}