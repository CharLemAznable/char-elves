package com.github.charlemaznable.core.lang;

import com.github.charlemaznable.core.crypto.AES;
import com.github.charlemaznable.core.crypto.PBE;
import lombok.NoArgsConstructor;
import lombok.val;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import static com.github.charlemaznable.core.codec.Base64.unBase64;
import static com.github.charlemaznable.core.lang.Mapp.toMap;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Propertiess {

    @SuppressWarnings("RegExpRedundantEscape")
    private static final Pattern encryptPattern = Pattern.compile("\\{(...)\\}");

    public static Properties parseStringToProperties(String string) {
        val properties = new Properties();
        if (string != null) {
            try {
                properties.load(new StringReader(string));
            } catch (IOException e) {
                // ignore
            }
        }
        return properties;
    }

    public static Properties tryDecrypt(Properties properties, String password) {
        val newProperties = new Properties();
        for (val key : properties.stringPropertyNames()) {
            val property = properties.getProperty(key);
            newProperties.put(key, tryDecrypt(property, password));
        }
        return newProperties;
    }

    public static String tryDecrypt(String original, String password) {
        if (null == original) return null;

        val matcher = encryptPattern.matcher(original);
        if (!matcher.find() || matcher.start() != 0) return original;

        val algrithm = matcher.group(1);
        val encrypted = original.substring(algrithm.length() + 2);
        if ("PBE".equalsIgnoreCase(algrithm)) {
            return PBE.decrypt(unBase64(encrypted), password);
        } else if ("AES".equalsIgnoreCase(algrithm)) {
            return AES.decrypt(unBase64(encrypted), password);
        }
        throw new UnsupportedOperationException(algrithm + " is not supported now");
    }

    public static Map<String, String> ssMap(Properties properties) {
        return properties.stringPropertyNames().stream()
                .collect(toMap(s -> s, properties::getProperty));
    }
}
