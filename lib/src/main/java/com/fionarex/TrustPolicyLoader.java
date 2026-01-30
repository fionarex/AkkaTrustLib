package com.fionarex;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public final class TrustPolicyLoader {

    public static TrustPolicyConfig load(String path) {
        Config config = ConfigFactory.parseFile(new java.io.File(path))
                .resolve()
                .getConfig("trust.inference");

        return new TrustPolicyConfig(
                config.getDouble("success"),
                config.getDouble("failure"),
                config.getDouble("malformed"),
                config.getDouble("heartbeat"));
    }
}
