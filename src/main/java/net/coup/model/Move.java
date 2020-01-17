package net.coup.model;

public class Move {
    private final String source;
    private final String target;
    private final Action action;

    public Move(final String source, final String target, final Action action) {
        this.action = action;
        this.source = source;
        this.target = target;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public Action getAction() {
        return action;
    }

    @Override
    public String toString() {
        return String.format("%1$s performing %2$s on %3$s", source, action, target);
    }
}
