package org.battleplugins.arena.ctf;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.command.ArenaCommand;
import org.battleplugins.arena.command.ArenaCommandExecutor;
import org.battleplugins.arena.competition.map.CompetitionMap;
import org.battleplugins.arena.ctf.arena.CtfMap;
import org.battleplugins.arena.ctf.editor.CtfEditorWizards;
import org.battleplugins.arena.team.ArenaTeam;
import org.bukkit.entity.Player;

public class CtfExecutor extends ArenaCommandExecutor {

    public CtfExecutor(Arena arena) {
        super(arena);
    }

    @ArenaCommand(commands = "flag", subCommands = "set", description = "Sets the flag position for a team.", permissionNode = "flag.set")
    public void setFlag(Player player, CompetitionMap map, ArenaTeam team) {
        if (!(map instanceof CtfMap ctfMap)) {
            return; // Should not happen but just incase
        }

        CtfEditorWizards.ADD_FLAG.openWizard(player, this.arena, ctx -> {
            ctx.setMap(ctfMap);
            ctx.setTeam(team);
        });
    }
}
