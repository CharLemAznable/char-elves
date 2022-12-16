package com.github.charlemaznable.core.spring;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ActiveProfilesThreadLocal {

    private static final ThreadLocal<String[]> local = new InheritableThreadLocal<>();

    public static void set(String[] activeProfiles) {
        local.set(activeProfiles);
    }

    public static String[] get() {
        return local.get();
    }

    public static void unload() {
        local.remove();
    }
}
