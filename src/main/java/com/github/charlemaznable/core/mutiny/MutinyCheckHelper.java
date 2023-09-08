package com.github.charlemaznable.core.mutiny;

import com.github.charlemaznable.core.lang.ClzPath;
import lombok.NoArgsConstructor;

import static com.github.charlemaznable.core.lang.Clz.isAssignable;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class MutinyCheckHelper {

    private static final String MUTINY_UNI = "io.smallrye.mutiny.Uni";

    public static final boolean HAS_MUTINY = ClzPath.classExists(MUTINY_UNI);

    public static boolean checkReturnMutinyUni(Class<?> returnType) {
        return HAS_MUTINY && Object.class != returnType
                && isAssignable(ClzPath.findClass(MUTINY_UNI), returnType);
    }
}
