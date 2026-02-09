package com.fionarex;

public final class TrustEvent {
    public enum Type {
        SUCCESS,
        FAILURE,
        MALFORMED,
        HEARTBEAT,
        FORGIVE
    }

    private final Type type;
    private final double amount;

    public TrustEvent(Type type) {
        this(type, 0.0);
    }

    public TrustEvent(Type type, double amount) {
        this.type = type;
        this.amount = amount;
    }

    public Type type() {
        return type;
    }

    public double amount() {
        return amount;
    }
}
