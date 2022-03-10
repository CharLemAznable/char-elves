package com.github.charlemaznable.core.lang;

import com.google.common.io.Resources;
import lombok.NoArgsConstructor;
import org.apache.commons.text.StringSubstitutor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static com.github.charlemaznable.core.lang.Propertiess.parseStringToProperties;
import static com.github.charlemaznable.core.lang.Propertiess.ssMap;
import static com.github.charlemaznable.core.lang.Str.isEmpty;
import static com.google.common.io.Resources.readLines;
import static java.lang.Class.forName;
import static java.lang.Thread.currentThread;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ClzPath {

    public static boolean classExists(String className) {
        try {
            forName(className, false, getClassLoader());
            return true;
        } catch (Exception ignored) { // including ClassNotFoundException
            return false;
        }
    }

    public static Class<?> findClass(String className) {
        if (isEmpty(className)) return null;

        try {
            return forName(className, false, getClassLoader());
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }

    /*
     * Load a class given its name. BL: We wan't to use a known ClassLoader--hopefully the heirarchy is set correctly.
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> tryLoadClass(String className) {
        if (isEmpty(className)) return null;

        try {
            return (Class<T>) getClassLoader().loadClass(className);
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }

    /**
     * Return the context classloader. BL: if this is command line operation, the classloading issues are more sane.
     * During servlet execution, we explicitly set the ClassLoader.
     *
     * @return The context classloader.
     */
    public static ClassLoader getClassLoader() {
        return currentThread().getContextClassLoader();
    }

    public static URL classResource(String classPath) {
        return getClassLoader().getResource(classPath);
    }

    public static boolean classResourceExists(String classPath) {
        return nonNull(classResource(classPath));
    }

    public static InputStream classResourceAsInputStream(String classPath) {
        return getClassLoader().getResourceAsStream(classPath);
    }

    public static String classResourceAsString(String classPath) {
        return urlAsString(classResource(classPath));
    }

    public static List<String> classResourceAsLines(String classPath) {
        return urlAsLines(classResource(classPath));
    }

    public static Properties classResourceAsProperties(String classPath) {
        return urlAsProperties(classResource(classPath));
    }

    public static StringSubstitutor classResourceAsSubstitutor(String classPath) {
        return urlAsSubstitutor(classResource(classPath));
    }

    public static InputStream urlAsInputStream(URL url) {
        try {
            return nonNull(url) ? url.openStream() : null;
        } catch (IOException e) {
            return null;
        }
    }

    public static String urlAsString(URL url) {
        try {
            return nonNull(url) ? Resources.toString(url, UTF_8) : null;
        } catch (IOException e) {
            return null;
        }
    }

    public static List<String> urlAsLines(URL url) {
        try {
            return nonNull(url) ? readLines(url, UTF_8) : newArrayList();
        } catch (IOException e) {
            return newArrayList();
        }
    }

    public static Properties urlAsProperties(URL url) {
        return parseStringToProperties(urlAsString(url));
    }

    public static StringSubstitutor urlAsSubstitutor(URL url) {
        return new StringSubstitutor(ssMap(urlAsProperties(url)));
    }
}
