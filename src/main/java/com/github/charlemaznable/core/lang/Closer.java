package com.github.charlemaznable.core.lang;

import lombok.NoArgsConstructor;
import lombok.val;

import java.io.Closeable;

import static com.github.charlemaznable.core.lang.Clz.getMethod;
import static com.github.charlemaznable.core.lang.Clz.invokeQuietly;
import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Closer {

    /**
     * 关闭对象, 屏蔽所有异常。
     * 调用对象的close方法（如果对象有该方法的话）。
     *
     * @param objs 对象列表
     */
    public static void closeQuietly(Object... objs) {
        for (val obj : objs) {
            closeQuietly(obj);
        }
    }

    /**
     * 关闭对象, 屏蔽所有异常。
     *
     * @param obj 待关闭对象
     */
    public static void closeQuietly(Object obj) {
        if (isNull(obj)) return;

        if (obj instanceof Closeable closeable) {
            try {
                closeable.close();
            } catch (Exception ignored) {
                // ignored
            }
            return;
        }

        try {
            val method = getMethod(obj.getClass(), "close");
            if (method.getParameterTypes().length == 0) {
                invokeQuietly(obj, method);
            }
        } catch (Exception ignored) {
            // ignored
        }
    }
}
