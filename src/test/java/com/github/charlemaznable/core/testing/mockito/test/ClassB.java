package com.github.charlemaznable.core.testing.mockito.test;

import com.github.charlemaznable.core.testing.mockito.MockitoSpyForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@MockitoSpyForTesting
@Component
public class ClassB {

    @Autowired
    private ClassC c;

    public String doSth() {
        return "B";
    }

    public String complex() {
        return this.doSth() + "&" + c.doSth();
    }
}
