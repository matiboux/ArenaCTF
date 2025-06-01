package org.battleplugins.arena.ctf.event;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.ctf.ArenaCtf;
import org.battleplugins.arena.event.EventTrigger;
import org.battleplugins.arena.event.player.BukkitArenaPlayerEvent;
import org.battleplugins.arena.resolver.Resolver;
import org.battleplugins.arena.resolver.ResolverKey;
import org.battleplugins.arena.resolver.ResolverProvider;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import org.battleplugins.arena.stat.ArenaStat;
import org.battleplugins.arena.team.ArenaTeam;
import java.util.Set;

@EventTrigger("on-flag-capture")
public class ArenaFlagCaptureEvent extends BukkitArenaPlayerEvent {
    public static final ResolverKey<Integer> FLAGS_CAPTURED = ResolverKey.create("flags-captured", Integer.class);
    public static final ResolverKey<Integer> FLAGS_TOTAL_CAPTURED = ResolverKey.create("flags-total-captured", Integer.class);

    private static final HandlerList HANDLERS = new HandlerList();

    public ArenaFlagCaptureEvent(@NotNull ArenaPlayer player) {
        super(player.getArena(), player);
    }

    public int computeFlagsTotalCaptured(ArenaPlayer player) {
        ArenaTeam team = player.getTeam();
        if (team == null) {
            return -1;
        }

        ArenaStat<Number> stat = (ArenaStat<Number>) ArenaStats.get("flags-captured");

        Set<ArenaPlayer> players = this.competition.getTeamManager().getPlayersOnTeam(team);
        int score = 0;
        for (ArenaPlayer teamPlayer : players) {
            score += teamPlayer.stat(stat).orElse(0).intValue();
        }

        return score;
    }

    @Override
    public Resolver resolve() {
        return super.resolve().toBuilder()
                .define(FLAGS_CAPTURED, ResolverProvider.simple(this.getArenaPlayer().getStat(ArenaCtf.FLAGS_CAPTURED_STAT), String::valueOf))
                .define(FLAGS_TOTAL_CAPTURED, ResolverProvider.simple(computeFlagsTotalCaptured(this.getArenaPlayer()), String::valueOf))
                .build();
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
