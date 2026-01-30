package com.fionarex;

public final class FileBasedInferencePolicy implements TrustInferencePolicy {

    private final TrustPolicyConfig cfg;

    public FileBasedInferencePolicy(TrustPolicyConfig cfg) {
        this.cfg = cfg;
    }

    @Override
    public TrustValue infer(TrustValue current, TrustEvent event) {
        double delta = switch (event.type()) {
            case SUCCESS -> cfg.success;
            case FAILURE -> cfg.failure;
            case MALFORMED -> cfg.malformed;
            case HEARTBEAT -> cfg.heartbeat;
        };

        return current.combine(TrustValue.of(delta), 1.0, 1.0);
    }
}
