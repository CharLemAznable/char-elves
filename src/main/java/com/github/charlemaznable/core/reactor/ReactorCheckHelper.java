package com.github.charlemaznable.core.reactor;

import com.github.charlemaznable.core.lang.ClzPath;
import lombok.NoArgsConstructor;

import static com.github.charlemaznable.core.lang.Clz.isAssignable;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ReactorCheckHelper {

    private static final String REACTOR_MONO = "reactor.core.publisher.Mono";

    public static final boolean HAS_REACTOR = ClzPath.classExists(REACTOR_MONO);

    public static boolean checkReturnReactorMono(Class<?> returnType) {
        return HAS_REACTOR && Object.class != returnType
                && isAssignable(ClzPath.findClass(REACTOR_MONO), returnType);
    }
}
