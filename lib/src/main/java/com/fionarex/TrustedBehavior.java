package com.fionarex;

import akka.actor.typed.Behavior;
import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.Receive;

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

    private TrustedBehavior(
            ActorContext<Envelope<T>> ctx,
            TrustContext initialTrustContext,
            Logic<T> logic,
            TrustInferencePolicy inferencePolicy) {
        super(ctx);
        this.trustContext = initialTrustContext;
        this.logic = logic;
        this.inferencePolicy = inferencePolicy;
    }

    public static <T> Behavior<Envelope<T>> create(
            TrustContext initialTrustContext,
            Logic<T> logic) {
        throw new IllegalStateException("No default policy is available.");
    }

    public static <T> Behavior<Envelope<T>> create(
            TrustContext initialTrustContext,
            Logic<T> logic,
            TrustInferencePolicy inferencePolicy) {
        return Behaviors.setup(ctx -> new TrustedBehavior<>(ctx, initialTrustContext, logic, inferencePolicy));
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

            TrustEvent inferredEvent = inferEventFromMessage(env);
            if (inferredEvent != null) {
                trustContext = trustContext.inferred(sender, inferredEvent, inferencePolicy);
            }
        }

        return logic.onMessage(getContext(), env, trustContext);
    }

    private TrustEvent inferEventFromMessage(Envelope<T> env) {
        if (env.payload() == null) {
            return new TrustEvent(TrustEvent.Type.MALFORMED);
        }

        return new TrustEvent(TrustEvent.Type.SUCCESS);
    }
}
