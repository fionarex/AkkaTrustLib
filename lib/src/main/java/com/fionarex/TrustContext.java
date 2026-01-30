package com.fionarex;

import akka.actor.typed.ActorRef;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class TrustContext {
    private final Map<ActorRef<?>, TrustValue> trustByRef;
    private final TrustPolicy policy;

    public TrustContext(TrustPolicy policy) {
        this.trustByRef = new HashMap<>();
        this.policy = policy;
    }

    public TrustValue get(ActorRef<?> ref) {
        return trustByRef.getOrDefault(ref, TrustValue.unknown());
    }

    public TrustContext updated(ActorRef<?> ref, TrustValue incoming) {
        TrustValue current = get(ref);
        TrustValue updated = policy.update(current, incoming);
        Map<ActorRef<?>, TrustValue> copy = new HashMap<>(trustByRef);
        copy.put(ref, updated);
        return new TrustContext(policy, copy);
    }

    private TrustContext(TrustPolicy policy, Map<ActorRef<?>, TrustValue> trustByRef) {
        this.policy = policy;
        this.trustByRef = Collections.unmodifiableMap(trustByRef);
    }

    public Map<ActorRef<?>, TrustValue> snapshot() {
        return trustByRef;
    }

    public TrustPolicy policy() {
        return policy;
    }

    public TrustContext inferred(ActorRef<?> ref, TrustEvent event, TrustInferencePolicy inference) {
        TrustValue current = get(ref);
        TrustValue updated = inference.infer(current, event);
        Map<ActorRef<?>, TrustValue> copy = new HashMap<>(trustByRef);
        copy.put(ref, updated);
        return new TrustContext(policy, copy);
    }
}
