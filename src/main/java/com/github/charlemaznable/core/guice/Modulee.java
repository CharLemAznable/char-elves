package com.github.charlemaznable.core.guice;

import com.google.inject.Module;
import com.google.inject.util.Modules;
import lombok.NoArgsConstructor;
import lombok.val;

import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Modulee {

    public static Module combine(Module... modules) {
        return Modules.combine(modules);
    }

    public static Module combine(Iterable<? extends Module> modules) {
        return Modules.combine(modules);
    }

    public static Module override(Module firstModule,
                                  Module secondModule,
                                  Module... modules) {
        return override(firstModule, secondModule, newArrayList(modules));
    }

    public static Module override(Module firstModule,
                                  Module secondModule,
                                  Iterable<? extends Module> modules) {
        Module resultModule = Modules.override(firstModule).with(secondModule);
        for (val module : modules) {
            resultModule = Modules.override(resultModule).with(module);
        }
        return resultModule;
    }
}
