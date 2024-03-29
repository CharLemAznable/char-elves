package com.github.charlemaznable.core.net;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.Socket;
import java.net.URL;

import static com.github.charlemaznable.core.codec.Bytes.bytes;
import static com.github.charlemaznable.core.lang.Mapp.newHashMap;
import static com.github.charlemaznable.core.lang.Mapp.of;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@SuppressWarnings("HttpUrlsUsage")
public class HttpReqTest {

    private static final SSLSocketFactory sslSocketFactory = new SSLSocketFactory() {
        @Override
        public Socket createSocket(String s, int i) {
            return null;
        }

        @Override
        public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) {
            return null;
        }

        @Override
        public Socket createSocket(InetAddress inetAddress, int i) {
            return null;
        }

        @Override
        public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) {
            return null;
        }

        @Override
        public String[] getDefaultCipherSuites() {
            return new String[0];
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return new String[0];
        }

        @Override
        public Socket createSocket(Socket socket, String s, int i, boolean b) {
            return null;
        }
    };
    private static final HostnameVerifier hostnameVerifier = (s, sslSession) -> false;

    @SneakyThrows
    @Test
    public void testHttpReqGet() {
        val doGetUrlTemp = "http://test.addr:8080%s";
        val dogetPath = "/doGet";
        val responseString = "RESPONSE";
        @Cleanup val responseStream = new ByteArrayInputStream(bytes(responseString));

        try (val mockUrlStatic = mockStatic(Url.class)) {
            val mockHttpsURLConnection = mock(HttpsURLConnection.class);
            when(mockHttpsURLConnection.getResponseCode()).thenReturn(HttpStatus.OK.value());
            when(mockHttpsURLConnection.getHeaderField("Content-Type")).thenReturn("text/plain; charset=UTF-8");
            when(mockHttpsURLConnection.getInputStream()).thenReturn(responseStream);
            val mockURL = mock(URL.class);
            when(mockURL.openConnection()).thenReturn(mockHttpsURLConnection);
            mockUrlStatic.when(() -> Url.build(format(doGetUrlTemp, dogetPath))).thenReturn(mockURL);

            val result = new HttpReq(doGetUrlTemp, dogetPath)
                    .sslSocketFactory(sslSocketFactory)
                    .hostnameVerifier(hostnameVerifier).get();
            assertEquals(responseString, result);
        }
    }

    @SneakyThrows
    @Test
    public void testHttpReqGetProxy() {
        val doGetUrlTemp = "http://test.addr:8080%s";
        val dogetPath = "/doGet";
        val responseString = "RESPONSE";
        @Cleanup val responseStream = new ByteArrayInputStream(bytes(responseString));
        val doGetProxy = new Proxy(Type.HTTP, new InetSocketAddress("127.0.0.1", 8090));

        try (val mockUrlStatic = mockStatic(Url.class)) {
            val mockHttpURLConnection = mock(HttpURLConnection.class);
            when(mockHttpURLConnection.getResponseCode()).thenReturn(HttpStatus.OK.value());
            when(mockHttpURLConnection.getHeaderField("Content-Type")).thenReturn("text/plain; charset=UTF-8");
            when(mockHttpURLConnection.getInputStream()).thenReturn(responseStream);
            val mockURL = mock(URL.class);
            when(mockURL.openConnection(doGetProxy)).thenReturn(mockHttpURLConnection);
            mockUrlStatic.when(() -> Url.build(format(doGetUrlTemp, dogetPath))).thenReturn(mockURL);

            val result = new HttpReq(doGetUrlTemp, dogetPath).proxy(doGetProxy).get();
            assertEquals(responseString, result);
        }
    }

    @SneakyThrows
    @Test
    public void testHttpReqGetError() {
        val doGetUrlTemp = "http://test.addr:8080%s";
        val dogetPath = "/doGet";

        try (val mockUrlStatic = mockStatic(Url.class)) {
            val mockHttpURLConnection = mock(HttpURLConnection.class);
            when(mockHttpURLConnection.getResponseCode()).thenReturn(HttpStatus.OK.value());
            when(mockHttpURLConnection.getInputStream()).thenThrow(new IOException());
            val mockURL = mock(URL.class);
            when(mockURL.openConnection()).thenReturn(mockHttpURLConnection);
            mockUrlStatic.when(() -> Url.build(format(doGetUrlTemp, dogetPath))).thenReturn(mockURL);

            assertNull(HttpReq.get(doGetUrlTemp, dogetPath));
        }
    }

    @SneakyThrows
    @Test
    public void testHttpReqPost() {
        val doPostRoot = "http://test.addr:8080";
        val doPostPath = "/doPost";
        @Cleanup val requestStream = new ByteArrayOutputStream();
        val responseString = "RESPONSE";
        @Cleanup val responseStream = new ByteArrayInputStream(bytes(responseString));

        try (val mockUrlStatic = mockStatic(Url.class)) {
            val mockHttpsURLConnection = mock(HttpsURLConnection.class);
            when(mockHttpsURLConnection.getOutputStream()).thenReturn(requestStream);
            when(mockHttpsURLConnection.getResponseCode()).thenReturn(HttpStatus.OK.value());
            when(mockHttpsURLConnection.getHeaderField("Content-Type")).thenReturn("text/plain; charset=UTF-8");
            when(mockHttpsURLConnection.getInputStream()).thenReturn(responseStream);
            val mockURL = mock(URL.class);
            when(mockURL.openConnection()).thenReturn(mockHttpsURLConnection);
            mockUrlStatic.when(() -> Url.build(doPostRoot)).thenReturn(mockURL);
            mockUrlStatic.when(() -> Url.build(doPostRoot + doPostPath)).thenReturn(mockURL);
            mockUrlStatic.when(() -> Url.encode(anyString())).thenCallRealMethod();

            String result = new HttpReq(doPostRoot)
                    .sslSocketFactory(sslSocketFactory)
                    .hostnameVerifier(hostnameVerifier).post();
            assertEquals(responseString, result);

            responseStream.reset();

            result = new HttpReq(doPostRoot)
                    .req(doPostPath)
                    .cookie(null)
                    .cookie("TestCookie")
                    .params(of("AAA", "aaa", "BBB", "bbb", "Z", "", "", "z"))
                    .requestBody("CCC=ccc")
                    .post();
            assertEquals(responseString, result);
            assertEquals("AAA=aaa&BBB=bbb&CCC=ccc", requestStream.toString());
        }
    }

    @SneakyThrows
    @Test
    public void testHttpReqPostError() {
        val doPostRoot = "http://test.addr:8080";
        val doPostPath = "/doPost";
        @Cleanup val requestStream = new ByteArrayOutputStream();
        val responseString = "RESPONSE";
        @Cleanup val responseStream = new ByteArrayInputStream(bytes(responseString));

        try (val mockUrlStatic = mockStatic(Url.class)) {
            val mockHttpURLConnection = mock(HttpURLConnection.class);
            when(mockHttpURLConnection.getOutputStream()).thenReturn(requestStream);
            when(mockHttpURLConnection.getResponseCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.value());
            when(mockHttpURLConnection.getHeaderField("Content-Type")).thenReturn("text/plain; charset=UTF-8");
            when(mockHttpURLConnection.getErrorStream()).thenReturn(responseStream);
            when(mockHttpURLConnection.getHeaderFields()).thenReturn(newHashMap());
            val mockURL = mock(URL.class);
            when(mockURL.openConnection()).thenReturn(mockHttpURLConnection);
            mockUrlStatic.when(() -> Url.build(doPostRoot + doPostPath)).thenReturn(mockURL);
            mockUrlStatic.when(() -> Url.encode(anyString())).thenCallRealMethod();

            val result = new HttpReq(doPostRoot)
                    .req(doPostPath)
                    .cookie(null)
                    .cookie("TestCookie")
                    .requestBody(null)
                    .requestBody("CCC=ccc")
                    .params(of("AAA", "aaa", "BBB", "bbb", "Z", "", "", "z"))
                    .post();
            assertNull(result);
            assertEquals("CCC=ccc&AAA=aaa&BBB=bbb", requestStream.toString());
        }
    }

    @SneakyThrows
    @Test
    public void testHttpReqPostError2() {
        val doPostRoot = "http://test.addr:8080";
        val doPostPath = "/doPost";
        @Cleanup val requestStream = new ByteArrayOutputStream();

        try (val mockUrlStatic = mockStatic(Url.class)) {
            val mockHttpURLConnection = mock(HttpURLConnection.class);
            when(mockHttpURLConnection.getOutputStream()).thenReturn(requestStream);
            when(mockHttpURLConnection.getResponseCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.value());
            when(mockHttpURLConnection.getHeaderField("Content-Type")).thenReturn("text/plain; charset=UTF-8");
            when(mockHttpURLConnection.getHeaderFields()).thenReturn(newHashMap());
            val mockURL = mock(URL.class);
            when(mockURL.openConnection()).thenReturn(mockHttpURLConnection);
            mockUrlStatic.when(() -> Url.build(doPostRoot)).thenReturn(mockURL);
            mockUrlStatic.when(() -> Url.encode(anyString())).thenCallRealMethod();

            val result = new HttpReq(doPostRoot).post();
            assertNull(result);
        }
    }

    @SneakyThrows
    @Test
    public void testHttpReqPostError3() {
        val doPostRoot = "http://test.addr:8080";
        val doPostPath = "/doPost";
        @Cleanup val requestStream = new ByteArrayOutputStream();

        try (val mockUrlStatic = mockStatic(Url.class)) {
            val mockHttpURLConnection = mock(HttpURLConnection.class);
            when(mockHttpURLConnection.getOutputStream()).thenReturn(requestStream);
            when(mockHttpURLConnection.getResponseCode()).thenReturn(HttpStatus.OK.value());
            when(mockHttpURLConnection.getHeaderField("Content-Type")).thenReturn("text/plain; charset=UTF-8");
            when(mockHttpURLConnection.getInputStream()).thenThrow(new IOException());
            val mockURL = mock(URL.class);
            when(mockURL.openConnection()).thenReturn(mockHttpURLConnection);
            mockUrlStatic.when(() -> Url.build(doPostRoot)).thenReturn(mockURL);
            mockUrlStatic.when(() -> Url.encode(anyString())).thenCallRealMethod();

            val result = new HttpReq(doPostRoot).post();
            assertNull(result);
        }
    }
}
