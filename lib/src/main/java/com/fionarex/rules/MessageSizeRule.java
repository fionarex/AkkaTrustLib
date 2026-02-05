package com.fionarex.rules;

import com.fionarex.*;

public final class MessageSizeRule<T> implements TrustRule<T> {

    private final int maxBytes;

    public MessageSizeRule(int maxBytes) {
        this.maxBytes = maxBytes;
    }

    @Override
    public TrustEvent evaluate(TrustedBehavior.Envelope<T> env, TrustContext ctx) {
        T payload = env.payload();
        if (payload == null)
            return null;

        int size = payload.toString().getBytes().length;

        if (size > maxBytes) {
            return new TrustEvent(TrustEvent.Type.FAILURE);
        }

        return null;
    }
}
