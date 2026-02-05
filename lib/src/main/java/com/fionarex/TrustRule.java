package com.fionarex;

public interface TrustRule<T> {
    TrustEvent evaluate(TrustedBehavior.Envelope<T> env, TrustContext ctx);
}
