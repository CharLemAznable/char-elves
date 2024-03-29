package com.github.charlemaznable.core.lang;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Extends {@code ParametersAreNonnullByDefault} to also apply to Method results and fields.
 *
 * @see javax.annotation.ParametersAreNonnullByDefault
 */
@Documented
@Nonnull
@TypeQualifierDefault({
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
public @interface EverythingIsNonNull {
}
