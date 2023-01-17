package com.github.charlemaznable.core.testing.mockito.test;

import com.github.charlemaznable.core.testing.mockito.MockitoSpyForTesting;
import org.springframework.stereotype.Component;

@MockitoSpyForTesting
@Component
public class ClassC {

    public String doSth() {
        return "C";
    }
}
