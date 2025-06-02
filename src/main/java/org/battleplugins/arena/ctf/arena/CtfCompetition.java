package org.battleplugins.arena.ctf.arena;

import net.kyori.adventure.text.Component;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.CompetitionType;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.ctf.ArenaCtf;
import org.battleplugins.arena.ctf.CtfMessages;
import org.battleplugins.arena.ctf.CtfUtil;
import org.battleplugins.arena.ctf.event.ArenaFlagCaptureEvent;
import org.battleplugins.arena.ctf.event.ArenaFlagDropEvent;
import org.battleplugins.arena.ctf.event.ArenaFlagPickupEvent;
import org.battleplugins.arena.ctf.event.ArenaFlagReturnEvent;
import org.battleplugins.arena.ctf.event.ArenaFlagStealEvent;
import org.battleplugins.arena.messages.Message;
import org.battleplugins.arena.team.ArenaTeam;
import org.battleplugins.arena.util.ItemColor;
import org.bukkit.block.data.Rotatable;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.Sound;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CtfCompetition extends LiveCompetition<CtfCompetition> {
    private final CtfArena arena;
    private final CtfMap map;

    private final List<ActiveFlag> flags = new ArrayList<>();

    private final Map<ArenaPlayer, ActiveFlag> capturedFlags = new HashMap<>();
    private final Map<ArenaPlayer, CaptureInfo> capturingFlags = new HashMap<>();

    private BukkitTask tickTask;

    public CtfCompetition(CtfArena arena, CompetitionType type, CtfMap map) {
        super(arena, type, map);

        this.arena = arena;
        this.map = map;
    }

    public void placeFlags() {
        for (Map.Entry<String, CtfMap.Flag> flag : map.getFlags().entrySet()) {
            ArenaTeam team = this.getTeamManager().getTeams().stream()
                    .filter(t -> t.getName().equals(flag.getKey()))
                    .findFirst()
                    .orElse(null);

            if (team == null) {
                arena.getPlugin().warn("Could not find team with name {} for flag!", flag.getKey());
                continue;
            }

            this.flags.add(new ActiveFlag(team, flag.getValue()));
        }

        for (ActiveFlag flag : this.flags) {
            flag.placeFlag();
        }
    }

    public void removeFlags() {
        for (ActiveFlag flag : this.flags) {
            flag.removeFlag();
        }

        this.flags.clear();
    }

    public void tickFlags() {
        // See if any players are capturing flags
        for (ActiveFlag flag : List.copyOf(this.flags)) {
            if (flag.placed) {
                flag.playAnimation(flag.flagLocation);
            }

            if (flag.dropTime != -1) {
                // Flag has been dropped, see if we need to return it
                if (Duration.ofMillis(System.currentTimeMillis() - flag.dropTime).compareTo(ArenaCtf.getInstance().getMainConfig().getDropTime()) >= 0) {
                    this.returnFlag(flag, null);
                }
            }

            for (ArenaPlayer arenaPlayer : this.getPlayers()) {
                Player player = arenaPlayer.getPlayer();

                // Flag is dropped - let's see if anyone is here
                // to return it or pick it back up
                if (flag.dropTime != -1) {
                    if (flag.flagLocation.distanceSquared(player.getLocation()) <= 1 && !player.isDead()) {
                        if (flag.team.equals(arenaPlayer.getTeam())) {
                            this.returnFlag(flag, arenaPlayer);
                        } else {
                            // Player is holding a flag, don't let them pick it up
                            if (this.capturedFlags.containsKey(arenaPlayer)) {
                                continue;
                            }

                            this.pickUpFlag(arenaPlayer, flag);
                        }
                    }

                    continue;
                }

                if (flag.flag.getCaptureRegion().isInside(player.getLocation().toBlockLocation())) {
                    if (flag.team.equals(arenaPlayer.getTeam())) {
                        // Check to see if a player is holding the opposite team's flag
                        if (this.capturedFlags.containsKey(arenaPlayer)) {
                            this.captureFlag(arenaPlayer);
                        }

                        continue;
                    }

                    boolean hasAFriendCapturingFlag = false;
                    for (ArenaPlayer anyPlayer : this.getPlayers()) {
                        if (anyPlayer.getTeam().equals(arenaPlayer.getTeam())) {
                            // If a player on the same team is already capturing the flag, don't allow another player to capture it
                            if (this.capturingFlags.containsKey(anyPlayer) || this.capturedFlags.containsKey(anyPlayer)) {
                                hasAFriendCapturingFlag = true;
                                break;
                            }
                        }
                    }

                    // Already capturing (or holds a captured flag)
                    if (hasAFriendCapturingFlag) {
                        continue;
                    }

                    this.capturingFlags.put(arenaPlayer, new CaptureInfo(flag, System.currentTimeMillis()));
                    this.broadcast(CtfMessages.FLAG_BEING_TAKEN, flag.team.getFormattedName(), Component.text(arenaPlayer.getPlayer().getName()));
                } else if (!flag.team.equals(arenaPlayer.getTeam())) {
                    // No longer in capture region - cancel
                    this.capturingFlags.remove(arenaPlayer);
                }
            }
        }

        Duration captureTime = ArenaCtf.getInstance().getMainConfig().getCaptureTime();
        for (Map.Entry<ArenaPlayer, CaptureInfo> entry : this.capturingFlags.entrySet()) {
            // Check if the player has been capturing the flag for the required time
            if (Duration.ofMillis(System.currentTimeMillis() - entry.getValue().startTime).compareTo(captureTime) >= 0) {
                this.stealFlag(entry.getKey(), entry.getValue().flag);
            } else {
                // Send animation
                Component progressBar = CtfUtil.getProgressBar('■', entry.getValue().startTime(), captureTime);
                Component component = CtfMessages.CAPTURE_PROGRESS.toComponent(progressBar);
                entry.getKey().getPlayer().sendActionBar(component);

                // Play flame particles at the flag
                entry.getValue().flag.flagLocation.getWorld().spawnParticle(Particle.FLAME, entry.getValue().flag.flagLocation.toCenterLocation(), 5, 0.3, 0.75, 0.3, 0);
            }
        }

        for (Map.Entry<ArenaPlayer, ActiveFlag> entry : this.capturedFlags.entrySet()) {
            entry.getValue().playAnimation(entry.getKey().getPlayer().getLocation(), 2, 20, 3);
        }
    }

    private void stealFlag(ArenaPlayer player, ActiveFlag flag) {
        this.capturedFlags.put(player, flag);
        this.capturingFlags.remove(player);

        this.getArena().getEventManager().callEvent(new ArenaFlagStealEvent(player));
        this.broadcast(CtfMessages.FLAG_STOLEN, flag.team.getFormattedName(), Component.text(player.getPlayer().getName()));
        this.broadcastSounds(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.BLOCK_ANVIL_PLACE, 0.0f);

        flag.removeFlag();
    }

    private void pickUpFlag(ArenaPlayer player, ActiveFlag flag) {
        flag.dropTime = -1;

        this.capturedFlags.put(player, flag);
        this.getArena().getEventManager().callEvent(new ArenaFlagPickupEvent(player));
        this.broadcast(CtfMessages.FLAG_PICKED_UP, flag.team.getFormattedName(), Component.text(player.getPlayer().getName()));
        this.broadcastSounds(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.ENTITY_ALLAY_DEATH, 1.0f);

        flag.removeFlag();
    }

    public void dropFlag(ArenaPlayer player) {
        ActiveFlag flag = this.capturedFlags.remove(player);
        if (flag == null) {
            return;
        }

        this.getArena().getEventManager().callEvent(new ArenaFlagDropEvent(player));
        this.broadcast(CtfMessages.FLAG_DROPPED, flag.team.getFormattedName(), Component.text(player.getPlayer().getName()));
        this.broadcastSounds(player, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1.0f);

        flag.dropFlag(player.getPlayer().getLocation());
    }

    private void captureFlag(ArenaPlayer player) {
        ActiveFlag flag = this.capturedFlags.remove(player);
        if (flag == null) {
            return;
        }

        flag.resetLocation();
        flag.placeFlag();

        this.getArena().getEventManager().callEvent(new ArenaFlagCaptureEvent(player));
        this.broadcast(CtfMessages.FLAG_CAPTURED, flag.team.getFormattedName(), Component.text(player.getPlayer().getName()));
        this.broadcastSounds(player, Sound.ENTITY_PLAYER_LEVELUP, Sound.ENTITY_IRON_GOLEM_DEATH, 2.0f);

        player.computeStat(ArenaCtf.FLAGS_CAPTURED_STAT, old -> (old == null ? 0 : old) + 1);
    }

    private void returnFlag(ActiveFlag flag, @Nullable ArenaPlayer player) {
        if (player != null) {
            this.getArena().getEventManager().callEvent(new ArenaFlagReturnEvent(player));

            this.broadcast(CtfMessages.FLAG_RETURNED_BY, flag.team.getFormattedName(), Component.text(player.getPlayer().getName()));
            this.broadcastSounds(player, Sound.BLOCK_NOTE_BLOCK_PLING, Sound.ENTITY_BLAZE_DEATH, 1.0f);
        } else {
            this.broadcast(CtfMessages.FLAG_RETURNED, flag.team.getFormattedName());
        }

        flag.removeFlag();
        flag.resetLocation();
        flag.placeFlag();
    }

    public void startTickingFlags() {
        this.tickTask = Bukkit.getScheduler().runTaskTimer(this.arena.getPlugin(), this::tickFlags, 0, 1);
    }

    public void stopTickingFlags() {
        if (this.tickTask != null) {
            this.tickTask.cancel();
            this.tickTask = null;
        }

        this.capturedFlags.clear();
        this.capturingFlags.clear();
    }

    private void broadcast(Message message, Component... replacements) {
        for (ArenaPlayer player : this.getCompetition().getPlayers()) {
            message.send(player.getPlayer(), replacements);
        }

        for (ArenaPlayer player : this.getCompetition().getSpectators()) {
            message.send(player.getPlayer(), replacements);
        }
    }

    private void broadcastSounds(ArenaPlayer player, Sound teamSound, Sound otherSound, float pitch) {
        ArenaTeam team = player.getTeam();
        Set<ArenaPlayer> players = this.getCompetition().getPlayers();
        Set<ArenaPlayer> teamPlayers = this.getCompetition().getTeamManager().getPlayersOnTeam(team);
        for (ArenaPlayer teamPlayer : players) {
            if (teamPlayers.contains(teamPlayer)) {
                teamPlayer.getPlayer().playSound(teamPlayer.getPlayer().getLocation(), teamSound, 1.0f, pitch);
            } else {
                teamPlayer.getPlayer().playSound(teamPlayer.getPlayer().getLocation(), otherSound, 1.0f, pitch);
            }
        }
    }

    public class ActiveFlag {
        private final ArenaTeam team;
        private final CtfMap.Flag flag;

        private Location flagLocation;
        private long dropTime = -1;

        private boolean placed;

        public ActiveFlag(ArenaTeam team, CtfMap.Flag flag) {
            this.team = team;
            this.flag = flag;

            this.flagLocation = this.flag.getPosition().toLocation(map.getWorld());
        }

        public void resetLocation() {
            this.flagLocation = this.flag.getPosition().toLocation(map.getWorld());
            this.dropTime = -1;
        }

        public void placeFlag() {
            this.placeFlag(this.flag.getPosition().getYaw());
        }

        public void placeFlag(float yaw) {
            if (this.placed) {
                arena.getPlugin().warn("Flag for team {} has already been placed! Not re-placing flag.", this.team.getName());
                return;
            }

            this.placed = true;
            Color color = this.team.getColor();

            DyeColor closestColor = DyeColor.values()[0];
            double closestDistance = Double.MAX_VALUE;
            for (DyeColor dyeColor : DyeColor.values()) {
                Color dyeColorColor = new Color(dyeColor.getColor().asRGB());
                double distance = Math.pow(dyeColorColor.getRed() - color.getRed(), 2) +
                        Math.pow(dyeColorColor.getGreen() - color.getGreen(), 2) +
                        Math.pow(dyeColorColor.getBlue() - color.getBlue(), 2);

                if (distance < closestDistance) {
                    closestColor = dyeColor;
                    closestDistance = distance;
                }
            }

            Material bannerMaterial = ItemColor.get(closestColor, ItemColor.Category.BANNER);
            if (bannerMaterial == null) {
                arena.getPlugin().warn("Could not find banner for color {}!", closestColor);
                return;
            }

            Rotatable rotatable = (Rotatable) bannerMaterial.createBlockData();
            rotatable.setRotation(this.flag.getBlockFace(yaw));
            this.flagLocation.getBlock().setBlockData(rotatable);
        }

        public void removeFlag() {
            if (!this.placed) {
                arena.getPlugin().warn("Flag for team {} has not been placed! Not removing flag.", this.team.getName());
                return;
            }

            this.placed = false;
            this.flagLocation.getBlock().setType(Material.AIR);
        }

        public void dropFlag(Location location) {
            this.dropTime = System.currentTimeMillis();
            this.updateLocation(location);
            this.placeFlag(location.getYaw());
        }

        public void updateLocation(Location location) {
            int highestBlockY = location.getWorld().getHighestBlockYAt(location) + 1;
            this.flagLocation = new Location(location.getWorld(), location.getX(), highestBlockY, location.getZ(), location.getYaw(), location.getPitch());
        }

        public void playAnimation(Location location) {
            this.playAnimation(location, 1, 1, 1);
        }

        public void playAnimation(Location location, int count, int step, int steps) {
            for (int i = 0; i < steps; i++) {
                int tick = Bukkit.getCurrentTick() + (step * i);

                float x = (float) (Math.sin(tick / 7.0D) * 1.0F);
                float z = (float) (Math.cos(tick / 7.0D) * 1.0F);
                float y = (float) (Math.cos(tick / 17.0D) * 1.0F + 1.0F);

                Color color = team.getColor();

                map.getWorld().spawnParticle(Particle.REDSTONE, location.getX() + x, location.getY() + y, location.getZ() + z, count, 0, 0, 0, new Particle.DustOptions(
                        org.bukkit.Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()), 1
                ));
            }
        }
    }

    public record CaptureInfo(ActiveFlag flag, long startTime) {
    }
}
