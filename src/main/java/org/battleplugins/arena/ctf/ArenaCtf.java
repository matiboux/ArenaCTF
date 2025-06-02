package org.battleplugins.arena.ctf;

import org.battleplugins.arena.BattleArena;
import org.battleplugins.arena.config.ArenaConfigParser;
import org.battleplugins.arena.config.ParseException;
import org.battleplugins.arena.ctf.action.PlaceFlagsAction;
import org.battleplugins.arena.ctf.action.RemoveFlagsAction;
import org.battleplugins.arena.ctf.arena.CtfArena;
import org.battleplugins.arena.ctf.event.ArenaFlagCaptureEvent;
import org.battleplugins.arena.ctf.event.ArenaFlagDropEvent;
import org.battleplugins.arena.ctf.event.ArenaFlagPickupEvent;
import org.battleplugins.arena.ctf.event.ArenaFlagReturnEvent;
import org.battleplugins.arena.ctf.event.ArenaFlagStealEvent;
import org.battleplugins.arena.event.action.EventActionType;
import org.battleplugins.arena.event.ArenaEventType;
import org.battleplugins.arena.stat.ArenaStat;
import org.battleplugins.arena.stat.ArenaStats;
import org.battleplugins.arena.stat.SimpleArenaStat;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class ArenaCtf extends JavaPlugin {
    public static final ArenaStat<Integer> FLAGS_CAPTURED_STAT = ArenaStats.register(new SimpleArenaStat<>("flags-captured", "Flags Captured", 0, Integer.class));

    public static final EventActionType<PlaceFlagsAction> PLACE_FLAGS_ACTION = EventActionType.create("place-flags", PlaceFlagsAction.class, PlaceFlagsAction::new);
    public static final EventActionType<RemoveFlagsAction> REMOVE_FLAGS_ACTION = EventActionType.create("remove-flags", RemoveFlagsAction.class, RemoveFlagsAction::new);

    public static final ArenaEventType<ArenaFlagCaptureEvent> FLAG_CAPTURE_EVENT = ArenaEventType.create("on-flag-capture", ArenaFlagCaptureEvent.class);
    public static final ArenaEventType<ArenaFlagPickupEvent> FLAG_PICKUP_EVENT = ArenaEventType.create("on-flag-pickup", ArenaFlagPickupEvent.class);
    public static final ArenaEventType<ArenaFlagStealEvent> FLAG_STEAL_EVENT = ArenaEventType.create("on-flag-steal", ArenaFlagStealEvent.class);
    public static final ArenaEventType<ArenaFlagDropEvent> FLAG_DROP_EVENT = ArenaEventType.create("on-flag-drop", ArenaFlagDropEvent.class);
    public static final ArenaEventType<ArenaFlagReturnEvent> FLAG_RETURN_EVENT = ArenaEventType.create("on-flag-return", ArenaFlagReturnEvent.class);

    private static ArenaCtf instance;

    private CtfConfig config;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();

        File configFile = new File(this.getDataFolder(), "config.yml");
        Configuration config = YamlConfiguration.loadConfiguration(configFile);
        try {
            this.config = ArenaConfigParser.newInstance(configFile.toPath(), CtfConfig.class, config);
        } catch (ParseException e) {
            ParseException.handle(e);

            this.getSLF4JLogger().error("Failed to load CTF configuration! Disabling plugin.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        CtfMessages.init();

        Path dataFolder = this.getDataFolder().toPath();
        Path arenasPath = dataFolder.resolve("arenas");
        if (Files.notExists(arenasPath)) {
            this.saveResource("arenas/ctf.yml", false);
        }

        BattleArena.getInstance().registerArena(this, "CTF", CtfArena.class, CtfArena::new);
    }

    public CtfConfig getMainConfig() {
        return this.config;
    }

    public static ArenaCtf getInstance() {
        return instance;
    }
}
