package com.fionarex.firewall;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;
import com.fionarex.*;

import java.util.*;

public final class TrustFirewall<T> extends AbstractBehavior<TrustedBehavior.Envelope<T>> {

    private final ActorRef<TrustedBehavior.Envelope<T>> protectedActor;
    private final TrustInferencePolicy policy;
    private final TrustRuleEngine<T> ruleEngine;
    private final double blockThreshold;

    private TrustContext trustContext;

    public static <T> Behavior<TrustedBehavior.Envelope<T>> create(
            ActorRef<TrustedBehavior.Envelope<T>> protectedActor,
            TrustInferencePolicy policy,
            List<TrustRule<T>> rules,
            double blockThreshold
    ) {
        return Behaviors.setup(ctx ->
                new TrustFirewall<>(
                        ctx,
                        protectedActor,
                        policy,
                        new TrustRuleEngine<>(rules),
                        blockThreshold
                )
        );
    }

    private TrustFirewall(
            ActorContext<TrustedBehavior.Envelope<T>> ctx,
            ActorRef<TrustedBehavior.Envelope<T>> protectedActor,
            TrustInferencePolicy policy,
            TrustRuleEngine<T> ruleEngine,
            double blockThreshold
    ) {
        super(ctx);
        this.protectedActor = protectedActor;
        this.policy = policy;
        this.ruleEngine = ruleEngine;
        this.blockThreshold = blockThreshold;
        this.trustContext = new TrustContext(TrustPolicy.DEFAULT);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Receive<TrustedBehavior.Envelope<T>> createReceive() {
        return newReceiveBuilder()
                .onMessage(TrustedBehavior.Envelope.class, this::onEnvelope)
                .build();
    }

    private Behavior<TrustedBehavior.Envelope<T>> onEnvelope(TrustedBehavior.Envelope<T> env) {

        ActorRef<?> sender = env.sender();
        if (sender != null) {

            trustContext = trustContext.updated(sender, env.trust());

            TrustEvent event = ruleEngine.evaluate(env, trustContext);
            if (event != null) {
                trustContext = trustContext.inferred(sender, event, policy);
            }

            TrustValue trust = trustContext.get(sender);
            if (trust.value() < blockThreshold) {
                getContext().getLog().warn(
                        "FIREWALL: Blocking message from {} (trust={})",
                        sender, trust.value()
                );
                return this;
            }
        }

        protectedActor.tell(env);
        return this;
    }
}
