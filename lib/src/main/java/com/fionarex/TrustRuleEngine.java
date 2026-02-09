package com.fionarex;

import java.util.List;

public final class TrustRuleEngine<T> {
    private final List<TrustRule<T>> rules;

    public TrustRuleEngine(List<TrustRule<T>> rules) {
        this.rules = List.copyOf(rules);
    }

    public TrustEvent evaluate(TrustedBehavior.Envelope<T> env, TrustContext ctx) {
        for (TrustRule<T> rule : rules) {
            TrustEvent event = rule.evaluate(env, ctx);
            if (event != null) {
                return event;
            }
        }
        return new TrustEvent(TrustEvent.Type.SUCCESS, 0.0);
    }
}
