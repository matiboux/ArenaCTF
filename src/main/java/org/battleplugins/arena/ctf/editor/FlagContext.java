package org.battleplugins.arena.ctf.editor;

import io.papermc.paper.math.Position;
import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ctf.arena.CtfMap;
import org.battleplugins.arena.editor.ArenaEditorWizard;
import org.battleplugins.arena.editor.EditorContext;
import org.battleplugins.arena.team.ArenaTeam;
import org.battleplugins.arena.util.PositionWithRotation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FlagContext extends EditorContext<FlagContext> {
    private CtfMap map;
    private ArenaTeam team;
    private PositionWithRotation flagPosition;
    private Position min;
    private Position max;

    public FlagContext(ArenaEditorWizard<FlagContext> wizard, Arena arena, Player player) {
        super(wizard, arena, player);
    }

    public CtfMap getMap() {
        return this.map;
    }

    public void setMap(CtfMap map) {
        this.map = map;
    }

    public ArenaTeam getTeam() {
        return this.team;
    }

    public void setTeam(ArenaTeam team) {
        this.team = team;
    }

    public PositionWithRotation getFlagPosition() {
        return this.flagPosition;
    }

    public void setFlagPosition(Location flagPosition) {
        this.flagPosition = new PositionWithRotation(flagPosition);
    }

    public Position getMin() {
        return this.min;
    }

    public void setMin(Position min) {
        this.min = min;
    }

    public Position getMax() {
        return this.max;
    }

    public void setMax(Position max) {
        this.max = max;
    }

    @Override
    public boolean isComplete() {
        return this.map != null && this.team != null && this.flagPosition != null
                && this.min != null && this.max != null;
    }
}
