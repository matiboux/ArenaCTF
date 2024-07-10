package org.battleplugins.arena.ctf.event;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.ArenaEventType;
import org.battleplugins.arena.event.EventTrigger;
import org.battleplugins.arena.event.player.BukkitArenaPlayerEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@EventTrigger("on-flag-return")
public class ArenaFlagReturnEvent extends BukkitArenaPlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    public ArenaFlagReturnEvent(@NotNull ArenaPlayer player) {
        super(player.getArena(), player);
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
