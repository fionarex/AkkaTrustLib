package com.fionarex;

public final class TrustPolicyConfig {
    public final double success;
    public final double failure;
    public final double malformed;
    public final double heartbeat;

    public TrustPolicyConfig(double success, double failure, double malformed, double heartbeat) {
        this.success = success;
        this.failure = failure;
        this.malformed = malformed;
        this.heartbeat = heartbeat;
    }
}

