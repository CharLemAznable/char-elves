package com.github.charlemaznable.core.lang;

import com.github.charlemaznable.core.lang.ex.BadConditionException;
import com.github.charlemaznable.core.lang.ex.BlankStringException;
import com.github.charlemaznable.core.lang.ex.EmptyObjectException;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.NoArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.charlemaznable.core.lang.Empty.isEmpty;
import static com.github.charlemaznable.core.lang.Str.isBlank;
import static com.github.charlemaznable.core.lang.Str.isNotBlank;
import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Condition {

    @SafeVarargs
    public static <T> T nonNull(T... objects) {
        for (val object : objects) {
            if (Objects.nonNull(object)) return object;
        }
        return null;
    }

    @SafeVarargs
    public static <T> T nonEmpty(T... objects) {
        for (val object : objects) {
            if (!isEmpty(object)) return object;
        }
        return null;
    }

    public static String nonBlank(String... strings) {
        for (val string : strings) {
            if (isNotBlank(string)) return string;
        }
        return null;
    }

    public static short nonEquals(short base, short... numbers) {
        for (val number : numbers) {
            if (base != number) return number;
        }
        return base;
    }

    public static int nonEquals(int base, int... numbers) {
        for (val number : numbers) {
            if (base != number) return number;
        }
        return base;
    }

    public static long nonEquals(long base, long... numbers) {
        for (val number : numbers) {
            if (base != number) return number;
        }
        return base;
    }

    public static float nonEquals(float base, float... numbers) {
        for (val number : numbers) {
            if (base != number) return number;
        }
        return base;
    }

    public static double nonEquals(double base, double... numbers) {
        for (val number : numbers) {
            if (base != number) return number;
        }
        return base;
    }

    public static byte nonEquals(byte base, byte... bytes) {
        for (val b : bytes) {
            if (base != b) return b;
        }
        return base;
    }

    public static <T, R> R notNullThen(T object, Function<? super T, R> action) {
        return checkNull(object, () -> null, action);
    }

    public static <T, R> R notEmptyThen(T object, Function<? super T, R> action) {
        return checkEmpty(object, () -> null, action);
    }

    public static <R> R notBlankThen(String string, Function<? super String, R> action) {
        return checkBlank(string, () -> null, action);
    }

    public static <T> T nullThen(T object, Supplier<T> action) {
        return checkNull(object, action, t -> t);
    }

    public static <T> T emptyThen(T object, Supplier<T> action) {
        return checkEmpty(object, action, t -> t);
    }

    public static String blankThen(String string, Supplier<String> action) {
        return checkBlank(string, action, s -> s);
    }

    public static <T, R> R checkNull(T object, Supplier<R> nullAction, Function<? super T, R> notNullAction) {
        return isNull(object) ? nullAction.get() : notNullAction.apply(object);
    }

    public static <T, R> R checkEmpty(T object, Supplier<R> emptyAction, Function<? super T, R> notEmptyAction) {
        return isEmpty(object) ? emptyAction.get() : notEmptyAction.apply(object);
    }

    public static <R> R checkBlank(String string, Supplier<R> blankAction, Function<? super String, R> notBlankAction) {
        return isBlank(string) ? blankAction.get() : notBlankAction.apply(string);
    }

    public static <T> void notNullThenRun(T object, Consumer<? super T> action) {
        checkNullRun(object, () -> {}, action);
    }

    public static <T> void notEmptyThenRun(T object, Consumer<? super T> action) {
        checkEmptyRun(object, () -> {}, action);
    }

    public static void notBlankThenRun(String string, Consumer<? super String> action) {
        checkBlankRun(string, () -> {}, action);
    }

    public static <T> void nullThenRun(T object, Runnable action) {
        checkNullRun(object, action, t -> {});
    }

    public static <T> void emptyThenRun(T object, Runnable action) {
        checkEmptyRun(object, action, t -> {});
    }

    public static void blankThenRun(String string, Runnable action) {
        checkBlankRun(string, action, s -> {});
    }

    public static <T> void checkNullRun(T object, Runnable nullAction, Consumer<? super T> notNullAction) {
        if (isNull(object)) nullAction.run();
        else notNullAction.accept(object);
    }

    public static <T> void checkEmptyRun(T object, Runnable emptyAction, Consumer<? super T> notEmptyAction) {
        if (isEmpty(object)) emptyAction.run();
        else notEmptyAction.accept(object);
    }

    public static void checkBlankRun(String string, Runnable blankAction, Consumer<? super String> notBlankAction) {
        if (isBlank(string)) blankAction.run();
        else notBlankAction.accept(string);
    }

    @Contract(value = "null -> fail", pure = true)
    @Nonnull
    @CanIgnoreReturnValue
    public static <T> T checkNotNull(@Nullable T object) {
        if (isNull(object)) {
            throw new NullPointerException();
        }
        return object;
    }

    @Contract(value = "null, _ -> fail", pure = true)
    @Nonnull
    @CanIgnoreReturnValue
    public static <T> T checkNotNull(@Nullable T object, Object errorMessage) {
        if (isNull(object)) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return object;
    }

    @Contract(value = "null, _ -> fail", pure = true)
    @Nonnull
    @CanIgnoreReturnValue
    public static <T> T checkNotNull(@Nullable T object, RuntimeException errorException) {
        if (isNull(object)) {
            throw nullThen(errorException, NullPointerException::new);
        }
        return object;
    }

    @Contract(value = "null, _ -> fail", pure = true)
    @Nonnull
    @CanIgnoreReturnValue
    public static <T> T checkNotNull(@Nullable T object,
                                     Function<NullPointerException, RuntimeException> function) {
        if (isNull(object)) {
            throw Optional.ofNullable(function)
                    .map(f -> f.apply(new NullPointerException()))
                    .orElseGet(NullPointerException::new);
        }
        return object;
    }

    @Contract(value = "null -> fail", pure = true)
    @Nonnull
    @CanIgnoreReturnValue
    public static <T> T checkNotEmpty(@Nullable T object) {
        if (isNull(object) || isEmpty(object))
            throw new EmptyObjectException();
        return object;
    }

    @Contract(value = "null, _ -> fail", pure = true)
    @Nonnull
    @CanIgnoreReturnValue
    public static <T> T checkNotEmpty(@Nullable T object, Object errorMessage) {
        if (isNull(object) || isEmpty(object))
            throw new EmptyObjectException(String.valueOf(errorMessage));
        return object;
    }

    @Contract(value = "null, _ -> fail", pure = true)
    @Nonnull
    @CanIgnoreReturnValue
    public static <T> T checkNotEmpty(@Nullable T object, RuntimeException errorException) {
        if (isNull(object) || isEmpty(object))
            throw nullThen(errorException, EmptyObjectException::new);
        return object;
    }

    @Contract(value = "null, _ -> fail", pure = true)
    @Nonnull
    @CanIgnoreReturnValue
    public static <T> T checkNotEmpty(@Nullable T object,
                                      Function<EmptyObjectException, RuntimeException> function) {
        if (isNull(object) || isEmpty(object))
            throw Optional.ofNullable(function)
                    .map(f -> f.apply(new EmptyObjectException()))
                    .orElseGet(EmptyObjectException::new);
        return object;
    }

    @Contract(value = "null -> fail", pure = true)
    @Nonnull
    @CanIgnoreReturnValue
    public static String checkNotBlank(@Nullable String string) {
        if (isNull(string) || isBlank(string))
            throw new BlankStringException();
        return string;
    }

    @Contract(value = "null, _ -> fail", pure = true)
    @Nonnull
    @CanIgnoreReturnValue
    public static String checkNotBlank(@Nullable String string, Object errorMessage) {
        if (isNull(string) || isBlank(string))
            throw new BlankStringException(String.valueOf(errorMessage));
        return string;
    }

    @Contract(value = "null, _ -> fail", pure = true)
    @Nonnull
    @CanIgnoreReturnValue
    public static String checkNotBlank(@Nullable String string, RuntimeException errorException) {
        if (isNull(string) || isBlank(string))
            throw nullThen(errorException, BlankStringException::new);
        return string;
    }

    @Contract(value = "null, _ -> fail", pure = true)
    @Nonnull
    @CanIgnoreReturnValue
    public static String checkNotBlank(@Nullable String string,
                                       Function<BlankStringException, RuntimeException> function) {
        if (isNull(string) || isBlank(string))
            throw Optional.ofNullable(function)
                    .map(f -> f.apply(new BlankStringException()))
                    .orElseGet(BlankStringException::new);
        return string;
    }

    public static void checkCondition(BooleanSupplier condition) {
        if (!condition.getAsBoolean()) throw new BadConditionException();
    }

    public static void checkCondition(BooleanSupplier condition, Object errorMessage) {
        if (!condition.getAsBoolean()) throw new BadConditionException(String.valueOf(errorMessage));
    }

    public static void checkCondition(BooleanSupplier condition, RuntimeException errorException) {
        if (!condition.getAsBoolean()) throw nullThen(errorException, BadConditionException::new);
    }

    public static void checkCondition(BooleanSupplier condition,
                                      Function<BadConditionException, RuntimeException> function) {
        if (!condition.getAsBoolean())
            throw Optional.ofNullable(function)
                    .map(f -> f.apply(new BadConditionException()))
                    .orElseGet(BadConditionException::new);
    }

    public static void checkConditionRun(BooleanSupplier condition, Runnable runnable) {
        if (!condition.getAsBoolean()) throw new BadConditionException();
        runnable.run();
    }

    public static void checkConditionRun(BooleanSupplier condition, Runnable runnable, Object errorMessage) {
        if (!condition.getAsBoolean()) throw new BadConditionException(String.valueOf(errorMessage));
        runnable.run();
    }

    public static void checkConditionRun(BooleanSupplier condition, Runnable runnable, RuntimeException errorException) {
        if (!condition.getAsBoolean()) throw nullThen(errorException, BadConditionException::new);
        runnable.run();
    }

    public static void checkConditionRun(BooleanSupplier condition, Runnable runnable,
                                         Function<BadConditionException, RuntimeException> function) {
        if (!condition.getAsBoolean())
            throw Optional.ofNullable(function)
                    .map(f -> f.apply(new BadConditionException()))
                    .orElseGet(BadConditionException::new);
        runnable.run();
    }

    @CanIgnoreReturnValue
    public static <T> T checkConditionThen(BooleanSupplier condition, Supplier<T> action) {
        if (!condition.getAsBoolean()) throw new BadConditionException();
        return action.get();
    }

    @CanIgnoreReturnValue
    public static <T> T checkConditionThen(BooleanSupplier condition, Supplier<T> action, Object errorMessage) {
        if (!condition.getAsBoolean()) throw new BadConditionException(String.valueOf(errorMessage));
        return action.get();
    }

    @CanIgnoreReturnValue
    public static <T> T checkConditionThen(BooleanSupplier condition, Supplier<T> action, RuntimeException errorException) {
        if (!condition.getAsBoolean()) throw nullThen(errorException, BadConditionException::new);
        return action.get();
    }

    @CanIgnoreReturnValue
    public static <T> T checkConditionThen(BooleanSupplier condition, Supplier<T> action,
                                           Function<BadConditionException, RuntimeException> function) {
        if (!condition.getAsBoolean())
            throw Optional.ofNullable(function)
                    .map(f -> f.apply(new BadConditionException()))
                    .orElseGet(BadConditionException::new);
        return action.get();
    }
}
