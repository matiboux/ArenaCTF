package org.battleplugins.arena.ctf;

import org.battleplugins.arena.config.ArenaOption;

import java.time.Duration;

public class CtfConfig {

    @ArenaOption(name = "capture-time", description = "How long a player must be in the capture of a region to capture the flag.", required = true)
    private Duration captureTime;

    @ArenaOption(name = "drop-time", description = "How long until a flag is returned back to it's home base once a player drops it.", required = true)
    private Duration dropTime;

    public Duration getCaptureTime() {
        return this.captureTime;
    }

    public Duration getDropTime() {
        return this.dropTime;
    }
}
