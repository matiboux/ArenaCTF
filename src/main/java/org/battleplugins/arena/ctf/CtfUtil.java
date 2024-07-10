package org.battleplugins.arena.ctf;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.time.Duration;

public final class CtfUtil {

    public static Component getProgressBar(char character, long startTime, Duration duration) {
        // Send a progress bar to the player
        long timeLeft = duration.toMillis() - (System.currentTimeMillis() - startTime);
        int progress = (int) ((timeLeft / (double) duration.toMillis()) * 15);
        StringBuilder progressFull = new StringBuilder();
        StringBuilder progressRemaining = new StringBuilder();
        for (int i = 15; i > 0; i--) {
            if (i <= progress) {
                progressRemaining.append(character);
            } else {
                progressFull.append(character);
            }
        }

        return Component.text(progressFull.toString(), NamedTextColor.GREEN).append(Component.text(progressRemaining.toString(), NamedTextColor.GRAY));
    }
}
