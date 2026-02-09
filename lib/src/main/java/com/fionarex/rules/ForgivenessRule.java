package com.fionarex.rules;

import com.fionarex.*;
import akka.actor.typed.ActorRef;

import java.util.HashMap;
import java.util.Map;

public final class ForgivenessRule<T> implements TrustRule<T> {

    private final double forgivenessAmount;
    private final long cooldownMillis;

    private final Map<ActorRef<?>, Long> lastForgive = new HashMap<>();

    public ForgivenessRule(double forgivenessAmount, long cooldownMillis) {
        this.forgivenessAmount = forgivenessAmount;
        this.cooldownMillis = cooldownMillis;
    }

    @Override
    public TrustEvent evaluate(TrustedBehavior.Envelope<T> env, TrustContext ctx) {
        ActorRef<?> sender = env.sender();
        if (sender == null)
            return null;

        long now = System.currentTimeMillis();
        long last = lastForgive.getOrDefault(sender, 0L);

        if (now - last < cooldownMillis) {
            return null;
        }

        TrustValue trust = ctx.get(sender);

        if (trust.value() < 0.0) {
            lastForgive.put(sender, now);
            return new TrustEvent(TrustEvent.Type.FORGIVE, forgivenessAmount);
        }

        return null;
    }
}
