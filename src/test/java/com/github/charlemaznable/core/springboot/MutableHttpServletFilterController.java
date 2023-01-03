package com.github.charlemaznable.core.springboot;

import lombok.val;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.github.charlemaznable.core.codec.Json.json;
import static com.github.charlemaznable.core.net.Http.fetchParameterMap;
import static com.github.charlemaznable.core.net.Http.responseJson;
import static com.github.charlemaznable.core.spring.MutableHttpServletElf.mutableRequest;
import static com.github.charlemaznable.core.spring.MutableHttpServletElf.mutableResponse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Controller
public class MutableHttpServletFilterController {

    @RequestMapping("/mutable-filter")
    public void mutable(HttpServletRequest request, HttpServletResponse response) {
        assertNotNull(mutableRequest(request));
        assertNotNull(mutableResponse(response));
        val requestMap = fetchParameterMap(request);
        requestMap.put("IN_CONTROLLER", "TRUE");
        responseJson(response, json(requestMap));
    }
}
