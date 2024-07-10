package org.battleplugins.arena.ctf;

import org.battleplugins.arena.messages.Message;
import org.battleplugins.arena.messages.Messages;

public final class CtfMessages {
    public static final Message FLAG_SET_POSITION = Messages.info("editor-flag-set-position", "Type \"flag\" to set the flag location, or \"cancel\" to cancel.");
    public static final Message FLAG_SET_MIN_POSITION = Messages.info("flag-editor-layer-set-min-position", "Click a block to set the minimum (first) position of the flag capture region.");
    public static final Message FLAG_SET_MAX_POSITION = Messages.info("flag-editor-layer-set-max-position", "Click a block to set the maximum (second) position of the flag capture region.");

    public static final Message FLAG_SET = Messages.success("ctf-flag-set", "Flag set successfully!");
    public static final Message FLAG_CAPTURED = Messages.info("ctf-flag-captured", "The {} flag has been <b><green>CAPTURED</green></b> by <secondary>{}</secondary>!");
    public static final Message FLAG_DROPPED = Messages.info("ctf-flag-dropped", "The {} flag has been <b><red>DROPPED</red></b> by <secondary>{}</secondary>!");
    public static final Message FLAG_PICKED_UP = Messages.info("ctf-flag-picked-up", "The {} flag has been <b><green>PICKED UP</green></b> by <secondary>{}</secondary>!");
    public static final Message FLAG_RETURNED_BY = Messages.info("ctf-flag-returned-by", "The {} flag has been <b><gold>RETURNED</gold></b> by <secondary>{}</secondary>!");
    public static final Message FLAG_RETURNED = Messages.info("ctf-flag-returned", "The {} flag has been <b><gold>RETURNED</gold></b>!");
    public static final Message FLAG_STOLEN = Messages.info("ctf-flag-stolen", "The {} flag has been <b><dark_red>STOLEN</dark_red></b> by <secondary>{}</secondary>!");
    public static final Message FLAG_BEING_TAKEN = Messages.info("ctf-flag-being-taken", "The {} flag is being taken by <secondary>{}</secondary>!");

    public static final Message CAPTURE_PROGRESS = Messages.info("ctf-capture-progress", "Capture progress: {}");

    static void init() {
        // no-op
    }
}
