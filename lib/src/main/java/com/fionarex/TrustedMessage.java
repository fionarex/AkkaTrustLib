package com.fionarex;

public final class TrustedMessage<T> {
    private final T payload;
    private final TrustValue trust;

    public TrustedMessage(T payload, TrustValue trust) {
        this.payload = payload;
        this.trust = trust;
    }

    public T payload() {
        return payload;
    }

    public TrustValue trust() {
        return trust;
    }

    public TrustedMessage<T> withTrust(TrustValue newTrust) {
        return new TrustedMessage<>(payload, newTrust);
    }

    @Override
    public String toString() {
        return "TrustedMessage(payload=" + payload + ", trust=" + trust + ")";
    }
}
