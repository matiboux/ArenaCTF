package org.battleplugins.arena.ctf.editor;

import org.battleplugins.arena.BattleArena;
import org.battleplugins.arena.competition.map.options.Bounds;
import org.battleplugins.arena.config.ParseException;
import org.battleplugins.arena.ctf.CtfMessages;
import org.battleplugins.arena.ctf.arena.CtfMap;
import org.battleplugins.arena.editor.ArenaEditorWizard;
import org.battleplugins.arena.editor.ArenaEditorWizards;
import org.battleplugins.arena.editor.stage.PositionInputStage;
import org.battleplugins.arena.editor.stage.SpawnInputStage;
import org.battleplugins.arena.editor.type.MapOption;
import org.battleplugins.arena.messages.Messages;

import java.io.IOException;

public final class CtfEditorWizards {
    public static final ArenaEditorWizard<FlagContext> ADD_FLAG = ArenaEditorWizards.createWizard(FlagContext::new)
            .addStage(FlagOption.FLAG_POSITION, new SpawnInputStage<>(CtfMessages.FLAG_SET_POSITION, "flag", ctx -> ctx::setFlagPosition))
            .addStage(MapOption.MIN_POS, new PositionInputStage<>(CtfMessages.FLAG_SET_MIN_POSITION, ctx -> ctx::setMin))
            .addStage(MapOption.MAX_POS, new PositionInputStage<>(CtfMessages.FLAG_SET_MAX_POSITION, ctx -> ctx::setMax))
            .onCreationComplete(ctx -> {
                Bounds bounds = new Bounds(ctx.getMin(), ctx.getMax());

                CtfMap map = ctx.getMap();
                CtfMap.Flag flag = new CtfMap.Flag(ctx.getFlagPosition(), bounds);

                map.setFlag(ctx.getTeam().getName(), flag);

                try {
                    map.save();
                } catch (ParseException | IOException e) {
                    BattleArena.getInstance().error("Failed to save map file for arena {}", ctx.getArena().getName(), e);
                    Messages.MAP_FAILED_TO_SAVE.send(ctx.getPlayer(), map.getName());
                    return;
                }

                CtfMessages.FLAG_SET.send(ctx.getPlayer());
            });
}
