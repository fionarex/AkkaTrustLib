package com.fionarex;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrustValueTest {

    @Test
    void createsValidTrustValues() {
        TrustValue t = TrustValue.of(0.5);
        assertEquals(0.5, t.value());
    }

    @Test
    void rejectsOutOfRangeValues() {
        assertThrows(IllegalArgumentException.class, () -> TrustValue.of(2.0));
        assertThrows(IllegalArgumentException.class, () -> TrustValue.of(-2.0));
    }

    @Test
    void unknownTrustIsZero() {
        assertTrue(TrustValue.unknown().isUnknown());
        assertEquals(0.0, TrustValue.unknown().value());
    }

    @Test
    void combineAveragesValues() {
        TrustValue a = TrustValue.of(1.0);
        TrustValue b = TrustValue.of(-1.0);

        TrustValue combined = a.combine(b, 1.0, 1.0);
        assertEquals(0.0, combined.value());
    }

    @Test
    void clampKeepsValuesInRange() {
        TrustValue t = TrustValue.of(1.0).clamp();
        assertEquals(1.0, t.value());
    }
}
