package org.battleplugins.arena.ctf.arena;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.map.LiveCompetitionMap;
import org.battleplugins.arena.competition.map.MapFactory;
import org.battleplugins.arena.competition.map.MapType;
import org.battleplugins.arena.competition.map.options.Bounds;
import org.battleplugins.arena.competition.map.options.Spawns;
import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.util.PositionWithRotation;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CtfMap extends LiveCompetitionMap {
    static final MapFactory FACTORY = MapFactory.create(CtfMap.class, CtfMap::new);

    @ArenaOption(name = "flags", description = "The flags for this CTF map.")
    private Map<String, Flag> flags = new HashMap<>();

    public CtfMap() {
    }

    public CtfMap(String name, Arena arena, MapType type, String world, @Nullable Bounds bounds, @Nullable Spawns spawns) {
        super(name, arena, type, world, bounds, spawns);
    }

    public void setFlag(String team, Flag flag) {
        if (this.flags == null) {
            this.flags = new HashMap<>();
        }

        this.flags.put(team, flag);
    }

    public Map<String, Flag> getFlags() {
        return Map.copyOf(this.flags);
    }

    @Nullable
    public Flag getFlag(String team) {
        return this.flags.get(team);
    }

    public void removeFlag(String team) {
        this.flags.remove(team);
    }

    @Override
    public Competition<?> createCompetition(Arena arena) {
        if (!(arena instanceof CtfArena ctfArena)) {
            throw new IllegalArgumentException("Arena must be a CTF arena!");
        }

        return new CtfCompetition(ctfArena, arena.getType(), this);
    }

    public static class Flag {
        private static final BlockFace[] FACES = {
                BlockFace.NORTH,
                BlockFace.NORTH_EAST,
                BlockFace.EAST,
                BlockFace.SOUTH_EAST,
                BlockFace.SOUTH,
                BlockFace.SOUTH_WEST,
                BlockFace.WEST,
                BlockFace.NORTH_WEST
        };

        @ArenaOption(name = "position", description = "The position of the flag.", required = true)
        private PositionWithRotation position;

        @ArenaOption(name = "capture-region", description = "The capture region of the flag.", required = true)
        private Bounds captureRegion;

        public Flag() {
        }

        public Flag(PositionWithRotation position, Bounds captureRegion) {
            this.position = position;
            this.captureRegion = captureRegion;
        }

        public PositionWithRotation getPosition() {
            return this.position;
        }

        public BlockFace getBlockFace(float yaw) {
            return toBlockFace(yaw);
        }

        public Bounds getCaptureRegion() {
            return this.captureRegion;
        }

        public static BlockFace toBlockFace(float yaw) {
            return FACES[Math.round(yaw / 45f) & 0x7].getOppositeFace();
        }
    }
}
