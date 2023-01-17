package com.github.charlemaznable.core.testing.mockito.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClassA {

    @Autowired
    private ClassB b;

    public String doSth() {
        return "A";
    }

    public String complex() {
        return this.doSth() + "&" + b.complex();
    }
}
