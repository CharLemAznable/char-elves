package com.github.charlemaznable.core.testing.mockito.test;

import com.github.charlemaznable.core.spring.NeoComponentScan;
import com.github.charlemaznable.core.testing.mockito.MockitoSpyProxyEnabled;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@NeoComponentScan
@MockitoSpyProxyEnabled
public class MockitoSpyProxyConfig {

    @Bean
    public ClassD classD() {
        return Mockito.spy(new ClassD());
    }
}
