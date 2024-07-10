package org.battleplugins.arena.ctf.action;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.ctf.arena.CtfCompetition;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;

import java.util.Map;

public class RemoveFlagsAction extends EventAction {

    public RemoveFlagsAction(Map<String, String> params) {
        super(params);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
    }

    @Override
    public void postProcess(Arena arena, Competition<?> competition, Resolvable resolvable) {
        if (!(competition instanceof CtfCompetition ctfCompetition)) {
            return;
        }

        ctfCompetition.removeFlags();
    }
}
