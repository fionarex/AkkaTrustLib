package com.fionarex.rules;

import com.fionarex.*;

import java.util.Set;

public final class TypeWhitelistRule<T> implements TrustRule<T> {

    private final Set<Class<?>> allowed;

    public TypeWhitelistRule(Set<Class<?>> allowed) {
        this.allowed = allowed;
    }

    @Override
    public TrustEvent evaluate(TrustedBehavior.Envelope<T> env, TrustContext ctx) {
        T payload = env.payload();
        if (payload == null)
            return null;

        if (!allowed.contains(payload.getClass())) {
            return new TrustEvent(TrustEvent.Type.MALFORMED);
        }

        return null;
    }
}
