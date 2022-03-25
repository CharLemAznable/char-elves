package com.github.charlemaznable.core.gm;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

class GM {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    GM() {}
}
