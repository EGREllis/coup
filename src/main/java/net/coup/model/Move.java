package net.coup.model;

public class Move {
    private final Player source;
    private final Player target;
    private final Action action;

    public Move(final Player source, final Player target, final Action action) {
        this.action = action;
        this.source = source;
        this.target = target;
    }

    public Player getSource() {
        return source;
    }

    public Player getTarget() {
        return target;
    }

    public Action getAction() {
        return action;
    }

    @Override
    public String toString() {
        return String.format("Player(%1$s) performing %2$s on player(%3$s)", source.getName(), action, target.getName());
    }
}
