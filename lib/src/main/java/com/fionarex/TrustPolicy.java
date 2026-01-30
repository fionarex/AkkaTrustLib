package com.fionarex;

public interface TrustPolicy {
    TrustValue update(TrustValue current, TrustValue incoming);

    TrustPolicy DEFAULT = (current, incoming) -> {
        if (current.isUnknown()) {
            return incoming;
        }
        return current.combine(incoming, 1.0, 1.0);
    };
}
