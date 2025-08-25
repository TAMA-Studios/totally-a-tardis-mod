package com.tdjs.tatm.tardis;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;

import java.util.Set;
import java.util.UUID;

/**
 * Manages TARDIS dimensional operations including teleportation and dimension creation.
 * Handles moving entities between TARDIS exterior and interior spaces.
 */
public class TardisDimensionManager {

    /**
     * Teleport an entity into a TARDIS (from exterior to interior)
     */
    public static boolean teleportIntoTardis(Entity entity, TardisInstance tardis) {
        if (!tardis.isInitialized()) {
            return false;
        }

        MinecraftServer server = entity.getServer();
        if (server == null) {
            return false;
        }

        ServerWorld interiorWorld = server.getWorld(tardis.getInteriorDimension());
        if (interiorWorld == null) {
            return false;
        }

        return teleportEntity(entity, interiorWorld, tardis.getInteriorPos().toCenterPos());
    }

    /**
     * Teleport an entity out of a TARDIS (from interior to exterior)
     */
    public static boolean teleportOutOfTardis(Entity entity, TardisInstance tardis) {
        if (!tardis.isInitialized()) {
            return false;
        }

        MinecraftServer server = entity.getServer();
        if (server == null) {
            return false;
        }

        ServerWorld exteriorWorld = server.getWorld(tardis.getExteriorDimension());
        if (exteriorWorld == null) {
            return false;
        }

        // Position entity slightly in front of the TARDIS exterior
        BlockPos exteriorPos = tardis.getExteriorPos();
        Vec3d targetPos = new Vec3d(exteriorPos.getX() + 0.5, exteriorPos.getY() + 1, exteriorPos.getZ() + 1.5);

        return teleportEntity(entity, exteriorWorld, targetPos);
    }

    /**
     * Generic entity teleportation method
     */
    private static boolean teleportEntity(Entity entity, ServerWorld targetWorld, Vec3d targetPos) {
        if (entity instanceof ServerPlayerEntity player) {
            return teleportPlayer(player, targetWorld, targetPos);
        } else {
            return teleportNonPlayerEntity(entity, targetWorld, targetPos);
        }
    }

    /**
     * Teleport a player entity with proper handling
     */
    private static boolean teleportPlayer(ServerPlayerEntity player, ServerWorld targetWorld, Vec3d targetPos) {
        try {
            // Use Minecraft's built-in teleport method for players
            player.teleport(targetWorld, targetPos.x, targetPos.y, targetPos.z, 
                           Set.of(PositionFlag.X, PositionFlag.Y, PositionFlag.Z), 
                           player.getYaw(), player.getPitch());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Teleport a non-player entity
     */
    private static boolean teleportNonPlayerEntity(Entity entity, ServerWorld targetWorld, Vec3d targetPos) {
        try {
            // For non-player entities, we need to handle dimension transfer manually
            if (entity.world != targetWorld) {
                Entity newEntity = entity.moveToWorld(targetWorld);
                if (newEntity != null) {
                    newEntity.refreshPositionAndAngles(targetPos.x, targetPos.y, targetPos.z, 
                                                     entity.getYaw(), entity.getPitch());
                    return true;
                }
                return false;
            } else {
                // Same dimension, just move position
                entity.refreshPositionAndAngles(targetPos.x, targetPos.y, targetPos.z, 
                                              entity.getYaw(), entity.getPitch());
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Create a unique dimension identifier for a TARDIS
     */
    public static Identifier createTardisDimension(UUID tardisId) {
        return new Identifier("tatm", "tardis_" + tardisId.toString().replace("-", "_"));
    }

    /**
     * Generate interior spawn position for a new TARDIS
     * In a full implementation, this would create the interior structure
     */
    public static BlockPos generateInteriorSpawnPos() {
        // For now, return a simple position
        // In a full implementation, this would create the TARDIS interior structure
        return new BlockPos(0, 64, 0);
    }

    /**
     * Check if an entity is inside a TARDIS interior dimension
     */
    public static boolean isInTardisInterior(Entity entity) {
        if (entity.world instanceof ServerWorld serverWorld) {
            TardisRegistry registry = TardisRegistry.get(serverWorld);
            return registry.getTardisByInteriorDimension(serverWorld.getRegistryKey().getValue()) != null;
        }
        return false;
    }

    /**
     * Get the TARDIS instance that contains the given entity (if in a TARDIS interior)
     */
    public static TardisInstance getTardisContaining(Entity entity) {
        if (entity.world instanceof ServerWorld serverWorld) {
            TardisRegistry registry = TardisRegistry.get(serverWorld);
            return registry.getTardisByInteriorDimension(serverWorld.getRegistryKey().getValue());
        }
        return null;
    }

    /**
     * Check if a position is occupied by a TARDIS exterior
     */
    public static boolean isTardisExteriorAt(ServerWorld world, BlockPos pos) {
        TardisRegistry registry = TardisRegistry.get(world);
        return registry.getTardisByExteriorLocation(pos, world.getRegistryKey().getValue()) != null;
    }

    /**
     * Get the TARDIS at a specific exterior location
     */
    public static TardisInstance getTardisAt(ServerWorld world, BlockPos pos) {
        TardisRegistry registry = TardisRegistry.get(world);
        return registry.getTardisByExteriorLocation(pos, world.getRegistryKey().getValue());
    }
}