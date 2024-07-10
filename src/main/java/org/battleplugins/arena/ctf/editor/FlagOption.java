package org.battleplugins.arena.ctf.editor;

import org.battleplugins.arena.editor.type.EditorKey;

public enum FlagOption implements EditorKey {
    FLAG_POSITION("flagPosition");

    private final String key;

    FlagOption(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}
