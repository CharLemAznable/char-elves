package com.github.charlemaznable.core.spring;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import static com.github.charlemaznable.core.codec.Bytes.bytes;
import static com.github.charlemaznable.core.lang.Mapp.newHashMap;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public final class MutableHttpServletRequest extends HttpServletRequestWrapper {

    private final Map<String, String[]> params;
    private String content;
    private final Charset charset;

    public MutableHttpServletRequest(HttpServletRequest request) {
        this(request, UTF_8);
    }

    @SneakyThrows
    public MutableHttpServletRequest(HttpServletRequest request, Charset charset) {
        super(request);

        this.params = newHashMap(request.getParameterMap());

        @Cleanup val bufferedReader = new BufferedReader(
                new InputStreamReader(request.getInputStream(), charset));
        val stringBuilder = new StringBuilder();
        String line;
        while (nonNull(line = bufferedReader.readLine())) {
            stringBuilder.append(line);
        }
        this.content = stringBuilder.toString();
        this.charset = charset;
    }

    @Override
    public String getParameter(String name) {
        val values = this.params.get(name);
        if (isNull(values) || values.length == 0) {
            return null;
        }
        return values[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return this.params;
    }

    public void setParameterMap(Map<String, Object> params) {
        for (val param : params.entrySet()) {
            setParameter(param.getKey(), param.getValue());
        }
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(this.params.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return this.params.get(name);
    }

    public void setParameter(String name, Object value) {
        if (nonNull(value)) {
            if (value instanceof String[] strs) {
                this.params.put(name, strs);
            } else if (value instanceof String str) {
                this.params.put(name, new String[]{str});
            } else {
                this.params.put(name, new String[]{String.valueOf(value)});
            }
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        val contentBytes = bytes(this.content, this.charset);
        if (isNull(contentBytes)) return null;
        return new MutableServletInputStream(
                new ByteArrayInputStream(contentBytes));
    }

    @Override
    public BufferedReader getReader() {
        val inputStream = this.getInputStream();
        if (isNull(inputStream)) return null;
        return new BufferedReader(new InputStreamReader(inputStream));
    }

    public String getRequestBody() {
        return this.content;
    }

    public void setRequestBody(String requestBody) {
        this.content = requestBody;
    }

    @AllArgsConstructor
    static final class MutableServletInputStream extends ServletInputStream {

        private ByteArrayInputStream byteArrayInputStream;

        public int read() {
            return this.byteArrayInputStream.read();
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            // ignore ReadListener
        }
    }
}
