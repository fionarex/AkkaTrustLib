package com.fionarex.rules;

import com.fionarex.*;

public final class NullPayloadRule<T> implements TrustRule<T> {

    @Override
    public TrustEvent evaluate(TrustedBehavior.Envelope<T> env, TrustContext ctx) {
        if (env.payload() == null) {
            return new TrustEvent(TrustEvent.Type.MALFORMED);
        }
        return null;
    }
}
