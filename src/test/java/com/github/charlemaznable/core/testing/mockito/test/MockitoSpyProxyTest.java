package com.github.charlemaznable.core.testing.mockito.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.util.MockUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MockitoSpyProxyConfig.class)
public class MockitoSpyProxyTest {

    @Autowired
    private ClassA a;
    @Autowired
    private ClassB b;
    @Autowired
    private ClassC c;

    @Test
    public void testMockitoSpyProxy() {
        assertFalse(MockUtil.isSpy(a));
        assertTrue(MockUtil.isSpy(b));
        assertTrue(MockUtil.isSpy(c));

        assertEquals("A&B&C", a.complex());

        doReturn("BB").when(b).doSth();
        doReturn("CC").when(c).doSth();

        assertEquals("A&BB&CC", a.complex());
    }
}
