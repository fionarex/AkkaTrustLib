package com.fionarex;

public final class TrustEvent {
    public enum Type {
        SUCCESS,
        FAILURE,
        MALFORMED,
        HEARTBEAT
    }

    private final Type type;

    public TrustEvent(Type type) {
        this.type = type;
    }

    public Type type() {
        return type;
    }
}
