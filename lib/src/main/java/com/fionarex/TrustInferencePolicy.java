package com.fionarex;

public interface TrustInferencePolicy {
    TrustValue infer(TrustValue current, TrustEvent event);
}
