package com.github.charlemaznable.core.spring;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.NoArgsConstructor;
import lombok.val;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class MutableHttpServletElf {

    public static MutableHttpServletRequest mutableRequest(HttpServletRequest request) {
        HttpServletRequest internalRequest = request;
        while (internalRequest instanceof HttpServletRequestWrapper requestWrapper) {
            if (internalRequest instanceof MutableHttpServletRequest mutableRequest) return mutableRequest;
            internalRequest = internalRequest(requestWrapper);
        }
        return null;
    }

    public static MutableHttpServletResponse mutableResponse(HttpServletResponse response) {
        HttpServletResponse internalResponse = response;
        while (internalResponse instanceof HttpServletResponseWrapper responseWrapper) {
            if (internalResponse instanceof MutableHttpServletResponse mutableResponse) return mutableResponse;
            internalResponse = internalResponse(responseWrapper);
        }
        return null;
    }

    public static void setRequestBody(HttpServletRequest request, String body) {
        val mutableRequest = mutableRequest(request);
        if (isNull(mutableRequest)) return;
        mutableRequest.setRequestBody(body);
    }

    public static void setRequestParameter(HttpServletRequest request, String name, Object value) {
        val mutableRequest = mutableRequest(request);
        if (isNull(mutableRequest)) return;
        mutableRequest.setParameter(name, value);
    }

    public static void setRequestParameterMap(HttpServletRequest request, Map<String, Object> params) {
        val mutableRequest = mutableRequest(request);
        if (isNull(mutableRequest)) return;
        mutableRequest.setParameterMap(params);
    }

    public static byte[] getResponseContent(HttpServletResponse response) {
        val mutableResponse = mutableResponse(response);
        if (isNull(mutableResponse)) return new byte[0];
        return mutableResponse.getContent();
    }

    public static void setResponseContent(HttpServletResponse response, byte[] content) {
        val mutableResponse = mutableResponse(response);
        if (isNull(mutableResponse)) return;
        mutableResponse.setContent(content);
    }

    public static void appendResponseContent(HttpServletResponse response, byte[] content) {
        val mutableResponse = mutableResponse(response);
        if (isNull(mutableResponse)) return;
        mutableResponse.appendContent(content);
    }

    public static String getResponseContentAsString(HttpServletResponse response) {
        val mutableResponse = mutableResponse(response);
        if (isNull(mutableResponse)) return null;
        return mutableResponse.getContentAsString();
    }

    public static void setResponseContentByString(HttpServletResponse response, String content) {
        val mutableResponse = mutableResponse(response);
        if (isNull(mutableResponse)) return;
        mutableResponse.setContentByString(content);
    }

    public static void appendResponseContentByString(HttpServletResponse response, String content) {
        val mutableResponse = mutableResponse(response);
        if (isNull(mutableResponse)) return;
        mutableResponse.appendContentByString(content);
    }

    public static String getResponseContentAsString(HttpServletResponse response, Charset charset) {
        val mutableResponse = mutableResponse(response);
        if (isNull(mutableResponse)) return null;
        return mutableResponse.getContentAsString(charset);
    }

    public static void setResponseContentByString(HttpServletResponse response, String content, Charset charset) {
        val mutableResponse = mutableResponse(response);
        if (isNull(mutableResponse)) return;
        mutableResponse.setContentByString(content, charset);
    }

    public static void appendResponseContentByString(HttpServletResponse response, String content, Charset charset) {
        val mutableResponse = mutableResponse(response);
        if (isNull(mutableResponse)) return;
        mutableResponse.appendContentByString(content, charset);
    }

    public static void mutateResponse(HttpServletResponse response, Consumer<MutableHttpServletResponse> mutator) {
        if (isNull(mutator)) return;
        val mutableResponse = mutableResponse(response);
        if (isNull(mutableResponse)) return;
        mutator.accept(mutableResponse);
    }

    private static HttpServletRequest internalRequest(HttpServletRequestWrapper requestWrapper) {
        return (HttpServletRequest) requestWrapper.getRequest();
    }

    private static HttpServletResponse internalResponse(HttpServletResponseWrapper responseWrapper) {
        return (HttpServletResponse) responseWrapper.getResponse();
    }
}
