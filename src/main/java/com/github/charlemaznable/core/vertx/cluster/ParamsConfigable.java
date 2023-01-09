package com.github.charlemaznable.core.vertx.cluster;

import com.github.charlemaznable.core.lang.Objectt.ParamsAppliable;
import lombok.val;

import static org.apache.commons.lang3.StringUtils.isBlank;

public interface ParamsConfigable extends ParamsAppliable {

    String fetchConfigValue(String[] params);

    void applyConfigValue(String configValue);

    @Override
    default void applyParams(String[] params) {
        val configValue = fetchConfigValue(params);
        // none value or blank value or error params
        if (isBlank(configValue)) return;
        applyConfigValue(configValue);
    }
}
