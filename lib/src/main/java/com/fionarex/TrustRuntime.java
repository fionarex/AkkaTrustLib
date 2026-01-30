package com.fionarex;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

public class TrustRuntime {
    public static <T> Behavior<T> trusted(
            Class<T> messageClass,
            TrustContext initialTrustContext,
            TrustedBehavior.Logic<T> logic) {
        return Behaviors.setup(ctx -> Behaviors.receive(messageClass)
                .onMessage(messageClass, msg -> {
                    TrustedBehavior.Envelope<T> env = new TrustedBehavior.Envelope<>(msg, TrustValue.unknown(), null);
                    ctx.getSelf().unsafeUpcast().tell(env);
                    return Behaviors.same();
                })
                .build());
    }
}
