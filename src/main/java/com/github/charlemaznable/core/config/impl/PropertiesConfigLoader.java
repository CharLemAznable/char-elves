package com.github.charlemaznable.core.config.impl;

import com.github.charlemaznable.core.config.ConfigLoader;
import com.github.charlemaznable.core.config.Configable;
import com.google.auto.service.AutoService;

import java.net.URL;
import java.util.List;
import java.util.Properties;

import static com.github.charlemaznable.core.lang.ClzPath.urlAsString;
import static com.github.charlemaznable.core.lang.Propertiess.parseStringToProperties;
import static com.github.charlemaznable.core.spring.ClzResolver.getResources;

@AutoService(ConfigLoader.class)
public final class PropertiesConfigLoader implements ConfigLoader {

    @Override
    public List<URL> loadResources(String basePath) {
        return getResources(basePath, "properties");
    }

    @Override
    public Configable loadConfigable(URL url) {
        return new DefaultConfigable(buildProperties(url));
    }

    private Properties buildProperties(URL url) {
        return parseStringToProperties(urlAsString(url));
    }
}
