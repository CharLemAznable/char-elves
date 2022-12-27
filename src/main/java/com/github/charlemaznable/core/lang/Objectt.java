package com.github.charlemaznable.core.lang;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.joor.ReflectException;

import javax.annotation.Nullable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.charlemaznable.core.lang.Clz.isAssignable;
import static com.github.charlemaznable.core.lang.ClzPath.findClass;
import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.Character.isJavaIdentifierStart;
import static java.lang.Character.isWhitespace;
import static java.lang.Character.toTitleCase;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.joor.Reflect.on;
import static org.joor.Reflect.onClass;

/**
 * Copy from com.github.bingoohuang:eql:org.n3r.eql.util.O
 */
@Slf4j
@NoArgsConstructor(access = PRIVATE)
public final class Objectt {

    public interface ValueGettable {

        Object getValue(@Nullable Class<?> returnType);
    }

    public interface ParamsAppliable {

        void applyParams(String[] params);
    }

    public static class Spec {

        @Getter
        @Setter
        private String name;
        private final List<String> params = new ArrayList<>();

        public String[] getParams() {
            return params.toArray(new String[0]);
        }

        public void addParam(String param) {
            params.add(param);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean setValue(Object mappedObject, String columnName,
                                   ValueGettable valueGettable) {
        if (mappedObject instanceof Map) {
            ((Map) mappedObject).put(columnName, valueGettable.getValue(null));
            return true;
        }
        val dotPos = columnName.indexOf('.');
        if (dotPos < 0) return setProperty(mappedObject, columnName, valueGettable);
        val property = columnName.substring(0, dotPos);
        val propertyValue = getOrCreateProperty(property, mappedObject);
        if (propertyValue == null) return false;
        val nestProperty = columnName.substring(dotPos + 1);
        return setValue(propertyValue, nestProperty, valueGettable);
    }

    public static Object getOrCreateProperty(String propertyName, Object hostBean) {
        Object property = getProperty(propertyName, hostBean);
        if (property != null) return property;
        // There has to be a method get* matching this segment
        val returnType = getPropertyType(propertyName, hostBean);
        if (Map.class.isAssignableFrom(returnType)) property = Maps.newHashMap();
        if (property == null) property = onClass(returnType).create().get();
        val prop = property;
        setProperty(hostBean, propertyName, rt -> prop);
        return prop;
    }

    public static String getGetMethodName(String propertyName) {
        return "get" + toTitleCase(propertyName.charAt(0)) + propertyName.substring(1);
    }

    public static String getSetMethodName(String propertyName) {
        return "set" + toTitleCase(propertyName.charAt(0)) + propertyName.substring(1);
    }

    @SneakyThrows
    public static Method getAccessibleMethod(Object hostBean, String methodName) {
        return setAccessibleTrue(hostBean.getClass().getMethod(methodName));
    }

    @SneakyThrows
    public static Field getAccessibleField(Object hostBean, String propertyName) {
        return setAccessibleTrue(hostBean.getClass().getDeclaredField(propertyName));
    }

    public static <T extends AccessibleObject> T setAccessibleTrue(T m) {
        if (!m.isAccessible()) m.setAccessible(true);
        return m;
    }

    public static <T> T parseObject(String specContent, Class<T> clazz) {
        if (isBlank(specContent)) return null;
        val spec = parseSpecLeniently(specContent);
        if (isNull(spec)) return null;
        try {
            return createObject(clazz, spec);
        } catch (Exception e) {
            log.error("parse object {} failed by {}", specContent, e.getMessage());
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static boolean setProperty(Object hostBean, String propertyName,
                                       ValueGettable valueGettable) {
        if (hostBean instanceof Map) {
            ((Map) hostBean).put(propertyName, valueGettable.getValue(null));
            return true;
        }
        return setBeanProperty(hostBean, propertyName, valueGettable);
    }

    private static boolean setBeanProperty(Object hostBean, String propertyName,
                                           ValueGettable valueGettable) {
        val methodName = getSetMethodName(propertyName);
        try {
            val m = getAccessibleMethod(hostBean, methodName);
            m.invoke(hostBean, valueGettable.getValue(m.getReturnType()));
            return true;
        } catch (Exception e) {
            log.debug("invoke set method exception", e);
        }
        try {
            val field = getAccessibleField(hostBean, propertyName);
            field.set(hostBean, valueGettable.getValue(field.getType()));
            return true;
        } catch (Exception e) {
            log.debug("invoke set field exception", e);
        }
        return false;
    }

    private static Object getProperty(String propertyName, Object hostBean) {
        val methodName = getGetMethodName(propertyName);
        try {
            return getAccessibleMethod(hostBean, methodName).invoke(hostBean);
        } catch (Exception e) {
            log.debug("invoke get method exception", e);
        }
        try {
            return getAccessibleField(hostBean, propertyName).get(hostBean);
        } catch (Exception e) {
            log.debug("invoke get field exception", e);
        }
        throw new UnsupportedOperationException("unable to get property "
                + propertyName + " value of bean " + hostBean);
    }

    private static Class<?> getPropertyType(String propertyName, Object hostBean) {
        val methodName = getGetMethodName(propertyName);
        try {
            return getAccessibleMethod(hostBean, methodName).getReturnType();
        } catch (Exception e) {
            log.debug("invoke get method exception", e);
        }
        try {
            return getAccessibleField(hostBean, propertyName).getType();
        } catch (Exception e) {
            log.debug("invoke get field exception", e);
        }
        throw new UnsupportedOperationException("unable to get property "
                + propertyName + " type of bean " + hostBean);
    }

    @SuppressWarnings("unchecked")
    private static <T> T createObject(Class<T> clazz, Spec spec) {
        Object object;
        try {
            object = onClass(spec.getName()).create().get();
        } catch (ReflectException e) {
            return null;
        }
        if (!clazz.isInstance(object)) return null;

        val utilsPA = findClass("com.github.bingoohuang.utils.config.utils.ParamsAppliable");
        val eqlPA = findClass("org.n3r.eql.spec.ParamsAppliable");
        val diamondPA = findClass("org.n3r.diamond.client.cache.ParamsAppliable");
        val objectClazz = object.getClass();
        if ((object instanceof ParamsAppliable) ||
                (nonNull(utilsPA) && isAssignable(objectClazz, utilsPA)) ||
                (nonNull(eqlPA) && isAssignable(objectClazz, eqlPA)) ||
                (nonNull(diamondPA) && isAssignable(objectClazz, diamondPA))) {
            on(object).call("applyParams", (Object) spec.getParams());
        }

        return (T) object;
    }

    private static Spec parseSpecLeniently(String spec) {
        val specs = parseSpecs(spec);
        return specs.length == 0 ? null : specs[0];
    }

    @SuppressWarnings("Duplicates")
    public static Spec[] parseSpecs(String specs) {
        char[] chars = specs.toCharArray();
        SpecState specState = SpecState.SpecClose;
        StringBuilder name = new StringBuilder();
        StringBuilder param = new StringBuilder();
        ParamQuoteState paramQuoteState = ParamQuoteState.None;
        ArrayList<Spec> specsDefs = new ArrayList<>();
        Spec spec = null;
        int i = 0;
        char ch = ' ';
        for (int ii = chars.length; i < ii; ++i) {
            ch = chars[i];
            switch (specState) {
                case SpecClose:
                    if (ch == '@') {
                        specState = SpecState.SpecOpen;
                        paramQuoteState = ParamQuoteState.None;
                    } else if (!isWhitespace(ch)) error(specs, i, ch);

                    break;
                case SpecOpen:
                    if (isJavaIdentifierStart(ch)) {
                        specState = SpecState.SpecName;
                        name.append(ch);
                    } else error(specs, i, ch);

                    break;
                case SpecName:
                    if (isJavaIdentifierPart(ch) || ch == '.' || ch == '$') name.append(ch);
                    else if (isWhitespace(ch) || ch == '@' || ch == '(') {
                        while (i < ii && isWhitespace(chars[i])) ++i;
                        if (i < ii) ch = chars[i];

                        specState = ch == '(' ? SpecState.ParamOpen : SpecState.SpecClose;
                        spec = addSpec(name, specsDefs);

                        if (specState == SpecState.SpecClose) --i; // backspace and continue
                    } else error(specs, i, ch);

                    break;
                case ParamOpen:
                    switch (ch) {
                        case ')':
                            addSpecParam(param, paramQuoteState, spec);
                            specState = SpecState.SpecClose;
                            break;
                        case '"':
                            paramQuoteState = ParamQuoteState.Left;
                            specState = SpecState.ParamValue;
                            clear(param);
                            break;
                        case '\\':
                            ch = convertpecialChar(chars[++i]);
                        default:
                            if (!isWhitespace(ch)) {
                                param.append(ch);
                                specState = SpecState.ParamValue;
                            }
                    }
                    break;
                case ParamValue:
                    switch (ch) {
                        case ')':
                            if (paramQuoteState == ParamQuoteState.Left) param.append(ch);
                            else {
                                addSpecParam(param, paramQuoteState, spec);
                                specState = SpecState.SpecClose;
                            }
                            break;
                        case ',':
                            if (paramQuoteState == ParamQuoteState.Left) param.append(ch);
                            else {
                                addSpecParam(param, paramQuoteState, spec);
                                paramQuoteState = ParamQuoteState.None;
                                specState = SpecState.ParamOpen;
                            }
                            break;
                        case '"':
                            if (paramQuoteState == ParamQuoteState.Left)
                                paramQuoteState = ParamQuoteState.Right;
                            else error(specs, i, ch);
                            break;
                        case '\\':
                            ch = convertpecialChar(chars[++i]);
                        default:
                            if (paramQuoteState == ParamQuoteState.Right) {
                                if (!isWhitespace(ch)) error(specs, i, ch);
                            } else param.append(ch);
                    }
                    break;
                default:
                    error(specs, i, ch);
            }
        }
        // Check whether it is normal ended
        switch (specState) {
            case SpecName:
                addSpec(name, specsDefs);
                break;
            case SpecOpen:
            case ParamValue:
            case ParamOpen:
                error(specs, i, ch);
        }
        return specsDefs.toArray(new Spec[0]);
    }

    @SuppressWarnings("Duplicates")
    private static Spec addSpec(StringBuilder name, List<Spec> specsDefs) {
        Spec spec = new Spec();
        spec.setName(name.toString());
        clear(name);
        specsDefs.add(spec);
        return spec;
    }

    private static void addSpecParam(StringBuilder param, ParamQuoteState paramInQuote, Spec spec) {
        spec.addParam(paramInQuote == ParamQuoteState.Right ? param.toString() : trimSubstring(param));
        clear(param);
    }

    private static void clear(StringBuilder param) {
        param.delete(0, param.length());
    }

    private static String trimSubstring(StringBuilder sb) {
        int first = 0;
        int last = sb.length();
        for (int ii = sb.length(); first < ii; first++)
            if (!isWhitespace(sb.charAt(first))) break;
        for (; last > first; last--)
            if (!isWhitespace(sb.charAt(last - 1))) break;
        return sb.substring(first, last);
    }

    private static void error(String specs, int i, char ch) {
        throw new IllegalArgumentException(specs + " is invalid at pos " + i + " with char " + ch);
    }

    @SuppressWarnings("Duplicates")
    private static char convertpecialChar(char aChar) {
        switch (aChar) {
            case 'n':
                return '\n';
            case 'r':
                return '\r';
            case 't':
                return '\t';
            case 'b':
                return '\b';
            case 'f':
                return '\f';
        }
        return aChar;
    }

    private enum SpecState {SpecOpen, SpecName, ParamOpen, ParamValue, SpecClose}

    private enum ParamQuoteState {None, Left, Right}
}
