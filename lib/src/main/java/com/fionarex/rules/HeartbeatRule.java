package com.fionarex.rules;

import com.fionarex.*;

public final class HeartbeatRule<T> implements TrustRule<T> {

    private final Class<?> heartbeatType;

    public HeartbeatRule(Class<?> heartbeatType) {
        this.heartbeatType = heartbeatType;
    }

    @Override
    public TrustEvent evaluate(TrustedBehavior.Envelope<T> env, TrustContext ctx) {
        T payload = env.payload();
        if (payload == null)
            return null;

        if (heartbeatType.isInstance(payload)) {
            return new TrustEvent(TrustEvent.Type.HEARTBEAT);
        }

        return null;
    }
}
