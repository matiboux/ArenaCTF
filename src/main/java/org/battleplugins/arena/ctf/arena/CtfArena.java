package org.battleplugins.arena.ctf.arena;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.command.ArenaCommandExecutor;
import org.battleplugins.arena.competition.map.MapFactory;
import org.battleplugins.arena.competition.phase.CompetitionPhaseType;
import org.battleplugins.arena.ctf.CtfExecutor;
import org.battleplugins.arena.event.ArenaEventHandler;
import org.battleplugins.arena.event.arena.ArenaPhaseCompleteEvent;
import org.battleplugins.arena.event.arena.ArenaPhaseStartEvent;
import org.battleplugins.arena.event.player.ArenaDeathEvent;
import org.battleplugins.arena.event.player.ArenaLeaveEvent;

public class CtfArena extends Arena {

    @Override
    public ArenaCommandExecutor createCommandExecutor() {
        return new CtfExecutor(this);
    }

    @Override
    public MapFactory getMapFactory() {
        return CtfMap.FACTORY;
    }

    @ArenaEventHandler
    public void onPhaseStart(ArenaPhaseStartEvent event) {
        if (!CompetitionPhaseType.INGAME.equals(event.getPhase().getType())) {
            return;
        }

        if (event.getCompetition() instanceof CtfCompetition ctfCompetition) {
            ctfCompetition.startTickingFlags();
        }
    }

    @ArenaEventHandler
    public void onPhaseComplete(ArenaPhaseCompleteEvent event) {
        if (!CompetitionPhaseType.INGAME.equals(event.getPhase().getType())) {
            return;
        }

        if (event.getCompetition() instanceof CtfCompetition ctfCompetition) {
            ctfCompetition.stopTickingFlags();
        }
    }

    @ArenaEventHandler
    public void onDeath(ArenaDeathEvent event) {
        if (event.getCompetition() instanceof CtfCompetition ctfCompetition) {
            ctfCompetition.dropFlag(event.getArenaPlayer());
        }
    }

    @ArenaEventHandler
    public void onDeath(ArenaLeaveEvent event) {
        if (event.getCompetition() instanceof CtfCompetition ctfCompetition) {
            ctfCompetition.dropFlag(event.getArenaPlayer());
        }
    }
}
