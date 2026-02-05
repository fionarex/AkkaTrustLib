package com.fionarex.rules;

import com.fionarex.*;
import akka.actor.typed.ActorRef;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RateLimitRule<T> implements TrustRule<T> {

    private final int maxPerSecond;
    private final Map<ActorRef<?>, Counter> counters = new ConcurrentHashMap<>();

    public RateLimitRule(int maxPerSecond) {
        this.maxPerSecond = maxPerSecond;
    }

    @Override
    public TrustEvent evaluate(TrustedBehavior.Envelope<T> env, TrustContext ctx) {
        ActorRef<?> sender = env.sender();
        if (sender == null)
            return null;

        Counter counter = counters.computeIfAbsent(sender, s -> new Counter());
        long now = System.currentTimeMillis();

        if (now - counter.windowStart > 1000) {
            counter.windowStart = now;
            counter.count = 0;
        }

        counter.count++;

        if (counter.count > maxPerSecond) {
            return new TrustEvent(TrustEvent.Type.FAILURE);
        }

        return null;
    }

    private static final class Counter {
        long windowStart = System.currentTimeMillis();
        int count = 0;
    }
}
