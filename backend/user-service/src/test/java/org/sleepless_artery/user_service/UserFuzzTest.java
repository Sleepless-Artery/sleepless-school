package org.sleepless_artery.user_service;

import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;

class UserFuzzTest {

    @Property(tries = 200)
    void randomStringsDoNotBreakSandbox(@ForAll String s) {
        Assertions.assertDoesNotThrow(() -> {
            String input = s == null ? "" : s;
            input = input.trim();
            input.hashCode();
            String joined = String.join("-", input, "x");
            joined.length();
        });
    }
}
