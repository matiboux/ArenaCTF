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

@EventTrigger("on-flag-capture")
public class ArenaFlagCaptureEvent extends BukkitArenaPlayerEvent {
    public static final ResolverKey<Integer> FLAGS_CAPTURED = ResolverKey.create("flags-captured", Integer.class);

    private static final HandlerList HANDLERS = new HandlerList();

    public ArenaFlagCaptureEvent(@NotNull ArenaPlayer player) {
        super(player.getArena(), player);
    }

    @Override
    public Resolver resolve() {
        return super.resolve().toBuilder()
                .define(FLAGS_CAPTURED, ResolverProvider.simple(this.getArenaPlayer().getStat(ArenaCtf.FLAGS_CAPTURED_STAT), String::valueOf))
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
