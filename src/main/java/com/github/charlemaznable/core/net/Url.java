package com.github.charlemaznable.core.net;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import static com.github.charlemaznable.core.lang.Str.isBlank;
import static java.nio.charset.StandardCharsets.UTF_8;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.replace;

@NoArgsConstructor(access = PRIVATE)
public final class Url {

    @SneakyThrows
    public static String encode(String s) {
        return URLEncoder.encode(s, UTF_8);
    }

    @SneakyThrows
    public static String decode(String s) {
        return URLDecoder.decode(s, UTF_8);
    }

    public static String encodeDotAndColon(String s) {
        return replace(replace(s, ".", "-"), ":", "_");
    }

    public static String decodeDotAndColon(String s) {
        return replace(replace(s, "-", "."), "_", ":");
    }

    public static String concatUrlQuery(String url, String query) {
        if (isBlank(query)) return url;
        return url + (url.contains("?") ? "&" : "?") + query;
    }

    @SneakyThrows
    public static URL build(String urlString) {
        return new URL(urlString);
    }
}
