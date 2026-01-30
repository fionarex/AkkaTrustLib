package com.fionarex;

public final class TrustValue {
    public static final double MIN = -1.0;
    public static final double MAX = 1.0;
    public static final double UNKNOWN = 0.0;

    private final double value;

    private TrustValue(double value) {
        if (value < MIN || value > MAX) {
            throw new IllegalArgumentException("Trust must be between -1.0 and 1.0, was: " + value);
        }
        this.value = value;
    }

    public static TrustValue of(double value) {
        return new TrustValue(value);
    }

    public static TrustValue unknown() {
        return new TrustValue(UNKNOWN);
    }

    public double value() {
        return value;
    }

    public boolean isUnknown() {
        return value == UNKNOWN;
    }

    public TrustValue clamp() {
        if (value < MIN)
            return of(MIN);
        if (value > MAX)
            return of(MAX);
        return this;
    }

    public TrustValue combine(TrustValue other, double weightSelf, double weightOther) {
        double combined = (value * weightSelf + other.value * weightOther) / (weightSelf + weightOther);
        return TrustValue.of(combined).clamp();
    }

    @Override
    public String toString() {
        return "TrustValue(" + value + ")";
    }
}
