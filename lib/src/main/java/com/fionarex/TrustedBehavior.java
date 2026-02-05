package com.fionarex;

import akka.actor.typed.Behavior;
import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.Receive;

import java.util.List;

public final class TrustedBehavior<T> extends AbstractBehavior<TrustedBehavior.Envelope<T>> {
    public static final class Envelope<T> {
        private final T payload;
        private final TrustValue trust;
        private final ActorRef<?> sender;

        public Envelope(T payload, TrustValue trust, ActorRef<?> sender) {
            this.payload = payload;
            this.trust = trust;
            this.sender = sender;
        }

        public T payload() {
            return payload;
        }

        public TrustValue trust() {
            return trust;
        }

        public ActorRef<?> sender() {
            return sender;
        }
    }

    public interface Logic<T> {
        Behavior<Envelope<T>> onMessage(
                ActorContext<Envelope<T>> ctx,
                Envelope<T> msg,
                TrustContext trustContext);
    }

    private TrustContext trustContext;
    private final Logic<T> logic;
    private final TrustInferencePolicy inferencePolicy;
    private final TrustRuleEngine<T> ruleEngine;

    private TrustedBehavior(
            ActorContext<Envelope<T>> ctx,
            TrustContext initialTrustContext,
            Logic<T> logic,
            TrustInferencePolicy inferencePolicy,
            TrustRuleEngine<T> ruleEngine) {
        super(ctx);
        this.trustContext = initialTrustContext;
        this.logic = logic;
        this.inferencePolicy = inferencePolicy;
        this.ruleEngine = ruleEngine;
    }

    // Force explicit policy + rules
    public static <T> Behavior<Envelope<T>> create(
            TrustContext initialTrustContext,
            Logic<T> logic,
            TrustInferencePolicy inferencePolicy,
            List<TrustRule<T>> rules) {
        return Behaviors.setup(ctx -> new TrustedBehavior<>(
                ctx,
                initialTrustContext,
                logic,
                inferencePolicy,
                new TrustRuleEngine<>(rules)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Receive<Envelope<T>> createReceive() {
        return newReceiveBuilder()
                .onMessage(Envelope.class, this::onEnvelope)
                .build();
    }

    private Behavior<Envelope<T>> onEnvelope(Envelope<T> env) {

        ActorRef<?> sender = env.sender();
        if (sender != null) {
            trustContext = trustContext.updated(sender, env.trust());

            TrustEvent inferredEvent = ruleEngine.evaluate(env, trustContext);
            if (inferredEvent != null) {
                trustContext = trustContext.inferred(sender, inferredEvent, inferencePolicy);
            }
        }
        return logic.onMessage(getContext(), env, trustContext);
    }
}
