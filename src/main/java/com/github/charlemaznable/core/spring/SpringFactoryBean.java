package com.github.charlemaznable.core.spring;

import lombok.Setter;
import lombok.val;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.Nonnull;

public abstract class SpringFactoryBean implements FactoryBean<Object>, ApplicationContextAware {

    @Setter
    private Class<?> xyzInterface;
    private ApplicationContext applicationContext;

    public abstract Object buildObject(Class<?> xyzInterface);

    @Override
    public Object getObject() {
        val activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
        val temp = ActiveProfilesThreadLocal.get();
        ActiveProfilesThreadLocal.set(activeProfiles);
        try {
            return buildObject(xyzInterface);
        } finally {
            ActiveProfilesThreadLocal.set(temp);
        }
    }

    @Override
    public Class<?> getObjectType() {
        return this.xyzInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        SpringContext.updateApplicationContext(applicationContext);
    }
}
