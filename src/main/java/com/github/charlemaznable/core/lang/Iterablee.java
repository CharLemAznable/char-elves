package com.github.charlemaznable.core.lang;

import lombok.NoArgsConstructor;
import lombok.val;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Iterablee {

    public static final int NOT_FOUND = -1;

    public static <E> void forEach(Iterable<? extends E> elements,
                                   BiConsumer<Integer, ? super E> action) {
        requireNonNull(elements);
        requireNonNull(action);

        int index = 0;
        for (val element : elements) {
            action.accept(index++, element);
        }
    }

    public static <E> int find(Iterable<? extends E> elements,
                               BiPredicate<Integer, ? super E> action) {
        requireNonNull(elements);
        requireNonNull(action);

        int index = 0;
        for (val element : elements) {
            if (action.test(index, element)) return index;
            index++;
        }
        return NOT_FOUND;
    }

    public static <E> int find(Iterable<? extends E> elements,
                               Predicate<? super E> action) {
        requireNonNull(elements);
        requireNonNull(action);

        int index = 0;
        for (val element : elements) {
            if (action.test(element)) return index;
            index++;
        }
        return NOT_FOUND;
    }
}
