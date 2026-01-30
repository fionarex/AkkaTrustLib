# AkkaTrustLib

WIP config based runtime library for Akka adding trust policies.

## Quick Start:
### 1. Define a Trust Policy
```java
public final class MyPolicy implements TrustInferencePolicy {
    @Override
    public TrustValue infer(TrustValue current, TrustEvent event) {
        return switch (event.type()) {
            case SUCCESS -> current.combine(TrustValue.of(+0.2), 1, 1);
            case FAILURE -> current.combine(TrustValue.of(-0.3), 1, 1);
            case MALFORMED -> current.combine(TrustValue.of(-0.6), 1, 1);
            case HEARTBEAT -> current.combine(TrustValue.of(+0.05), 1, 1);
        };
    }
}
```

### 2. Wrap your actor with TrustedBehavior
```java
Behavior<TrustedBehavior.Envelope<Command>> behavior =
    TrustedBehavior.create(
        new TrustContext(TrustPolicy.DEFAULT),
        (ctx, env, trustCtx) -> {

            TrustValue trust = trustCtx.get(env.sender());
            ctx.getLog().info("Sender trust = {}", trust.value());

            // your actor logic here
            return Behaviors.same();
        },
        new MyPolicy()
    );
```

### 3. Send trusted messages
```java
server.tell(new TrustedBehavior.Envelope<>(
    new Ping("hello", replyTo),
    TrustValue.unknown(),
    ctx.getSelf()
));

```

## Using A file based approach:
### Example Config File:
```
success=0.15
failure=-0.25
malformed=-0.50
heartbeat=0.05
```

### Loading file:
```java
TrustInferencePolicy policy =
    new FileBasedInferencePolicy("trust-policy.properties");
```

### Using file:
```java
TrustedBehavior.create(initialCtx, logic, policy);
```
