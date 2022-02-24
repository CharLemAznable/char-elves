package com.github.charlemaznable.core.net;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UrlTest {

    @Test
    public void testUrl() {
        assertEquals("%E6%B1%89%E5%AD%97", Url.encode("汉字"));
        assertEquals("汉字", Url.decode("%E6%B1%89%E5%AD%97"));

        assertEquals("abc_def---", Url.encodeDotAndColon("abc:def..."));
        assertEquals("abc:def...", Url.decodeDotAndColon("abc_def---"));

        assertEquals("http://a.b.c", Url.concatUrlQuery("http://a.b.c", null));
        assertEquals("http://a.b.c?c=d", Url.concatUrlQuery("http://a.b.c", "c=d"));
        assertEquals("http://a.b.c?c=d&e=f", Url.concatUrlQuery("http://a.b.c?c=d", "e=f"));

        assertNotNull(Url.build("http://a.b.c"));
    }
}
