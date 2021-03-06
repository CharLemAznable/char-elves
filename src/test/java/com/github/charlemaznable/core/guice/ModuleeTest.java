package com.github.charlemaznable.core.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModuleeTest {

    @Test
    public void testCombine() {
        val moduleA = new AbstractModule() {
            @Override
            protected void configure() {
                bind(TestService.class).annotatedWith(Names.named("A")).to(TestImplA.class);
            }
        };
        val moduleB = new AbstractModule() {
            @Override
            protected void configure() {
                bind(TestService.class).annotatedWith(Names.named("B")).to(TestImplB.class);
            }
        };

        val injector = Guice.createInjector(Modulee.combine(moduleA, moduleB));
        val implA = injector.getInstance(Key.get(TestService.class, Names.named("A")));
        assertEquals("AAA", implA.string());
        val implB = injector.getInstance(Key.get(TestService.class, Names.named("B")));
        assertEquals("BBB", implB.string());
    }

    @Test
    public void testOverride() {
        val moduleA = new AbstractModule() {
            @Override
            protected void configure() {
                bind(TestService.class).to(TestImplA.class);
            }
        };
        val moduleB = new AbstractModule() {
            @Override
            protected void configure() {
                bind(TestService.class).to(TestImplB.class);
            }
        };
        val moduleC = new AbstractModule() {
            @Override
            protected void configure() {
                bind(TestService.class).to(TestImplC.class);
            }
        };
        val moduleD = new AbstractModule() {
            @Override
            protected void configure() {
                bind(TestService.class).to(TestImplD.class);
            }
        };

        Injector injector = Guice.createInjector(moduleA);
        TestService impl = injector.getInstance(TestService.class);
        assertEquals("AAA", impl.string());

        injector = Guice.createInjector(Modulee.override(moduleA, moduleB));
        impl = injector.getInstance(TestService.class);
        assertEquals("BBB", impl.string());

        injector = Guice.createInjector(Modulee.override(moduleA, moduleB, moduleC));
        impl = injector.getInstance(TestService.class);
        assertEquals("CCC", impl.string());

        injector = Guice.createInjector(Modulee.override(moduleA, moduleB, moduleC, moduleD));
        impl = injector.getInstance(TestService.class);
        assertEquals("DDD", impl.string());
    }

    public interface TestService {

        String string();
    }

    public static class TestImplA implements TestService {

        @Override
        public String string() {
            return "AAA";
        }
    }

    public static class TestImplB implements TestService {

        @Override
        public String string() {
            return "BBB";
        }
    }

    public static class TestImplC implements TestService {

        @Override
        public String string() {
            return "CCC";
        }
    }

    public static class TestImplD implements TestService {

        @Override
        public String string() {
            return "DDD";
        }
    }
}
