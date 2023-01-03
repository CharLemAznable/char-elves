package com.github.charlemaznable.core.springboot;

import com.github.charlemaznable.core.spring.MutableHttpServletFilter;
import com.github.charlemaznable.core.spring.SpringContext;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.charlemaznable.core.codec.Json.unJson;
import static org.joor.Reflect.onClass;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootTest(classes = {TestApplication.class})
@AutoConfigureMockMvc
public class ElvesBootTest {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private MutableHttpServletFilter mutableHttpServletFilter;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testElvesBoot() {
        assertSame(applicationContext,
                onClass(SpringContext.class).field("applicationContext").get());
        assertNotNull(mutableHttpServletFilter);
    }

    @SneakyThrows
    @Test
    public void testSample() {
        val response = mockMvc.perform(get("/mutable-filter")
                        .param("IN_REQUEST", "TRUE")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        val responseContent = response.getContentAsString();
        val responseMap = unJson(responseContent);
        assertEquals("TRUE", responseMap.get("IN_REQUEST"));
        assertEquals("TRUE", responseMap.get("IN_PREHANDLE"));
        assertEquals("TRUE", responseMap.get("IN_CONTROLLER"));
        assertEquals("TRUE", responseMap.get("IN_POSTHANDLE"));
    }
}
