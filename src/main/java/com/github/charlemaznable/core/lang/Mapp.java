package com.github.charlemaznable.core.lang;

import com.google.common.collect.Maps;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.github.charlemaznable.core.lang.Condition.notNullThenRun;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;

@SuppressWarnings("rawtypes")
@NoArgsConstructor(access = PRIVATE)
public final class Mapp {

    public static <K, V> Map<K, V> of(K k1, V v1) {
        Map<K, V> map = newHashMap();
        map.put(k1, v1);

        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
        Map<K, V> map = of(k1, v1);
        map.put(k2, v2);

        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        Map<K, V> map = of(k1, v1, k2, v2);
        map.put(k3, v3);

        return map;
    }

    @SuppressWarnings("DuplicatedCode")
    @SafeVarargs
    public static <T> Map<T, T> of(T... keyAndValues) {
        Map<T, T> map = newHashMap();
        for (int i = 0; i < keyAndValues.length; i += 2) {
            val key = keyAndValues[i];
            val value = i + 1 < keyAndValues.length ? keyAndValues[i + 1] : null;
            map.put(key, value);
        }

        return map;
    }

    @SuppressWarnings("DuplicatedCode")
    public static Map<Object, Object> map(Object... keyAndValues) {
        Map<Object, Object> map = newHashMap();
        for (int i = 0; i < keyAndValues.length; i += 2) {
            val key = keyAndValues[i];
            val value = i + 1 < keyAndValues.length ? keyAndValues[i + 1] : null;
            map.put(key, value);
        }

        return map;
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return isNull(map) || map.isEmpty();
    }

    public static String getStr(Map m, Object key) {
        return getStr(m, key, null);
    }

    public static String getStr(Map m, Object key, String defaultValue) {
        if (isNull(m)) return defaultValue;
        val value = m.get(key);
        if (isNull(value)) return defaultValue;
        return value.toString();
    }

    @SneakyThrows
    public static Number getNum(Map m, Object key) {
        if (isNull(m)) return null;
        val value = m.get(key);
        if (isNull(value)) return null;
        if (value instanceof Number nNumber) return nNumber;
        if (!(value instanceof String nString)) return null;
        return NumberFormat.getInstance().parse(nString);
    }

    public static Boolean getBool(Map m, Object key) {
        return getBool(m, key, false);
    }

    public static Boolean getBool(Map m, Object key, Boolean defaultValue) {
        if (isNull(m)) return defaultValue;
        val value = m.get(key);
        if (isNull(value)) return defaultValue;
        if (value instanceof Boolean bValue) return bValue;
        if (value instanceof Number bNumber) return bNumber.intValue() != 0;
        if (!(value instanceof String bString)) return defaultValue;
        return "true".equalsIgnoreCase(bString)
                || "yes".equalsIgnoreCase(bString)
                || "on".equalsIgnoreCase(bString);
    }

    public static Integer getInt(Map m, Object key) {
        return getInt(m, key, null);
    }

    public static Integer getInt(Map m, Object key, Integer defaultValue) {
        val value = getNum(m, key);
        if (isNull(value)) return defaultValue;
        return value instanceof Integer iValue ? iValue : Integer.valueOf(value.intValue());
    }

    public static Long getLong(Map m, Object key) {
        return getLong(m, key, null);
    }

    public static Long getLong(Map m, Object key, Long defaultValue) {
        val value = getNum(m, key);
        if (isNull(value)) return defaultValue;
        return value instanceof Long lValue ? lValue : Long.valueOf(value.longValue());
    }

    public static <T> Map<T, T> mapFromList(List<Map<String, T>> list, String keyKey, String valueKey) {
        if (isNull(list)) return newHashMap();

        Map<T, T> result = newHashMap();
        for (val map : list) {
            if (!map.containsKey(keyKey) || StringUtils.isEmpty(getStr(map, keyKey)) ||
                    !map.containsKey(valueKey) || StringUtils.isEmpty(getStr(map, valueKey))) continue;
            result.put(map.get(keyKey), map.get(valueKey));
        }
        return result;
    }

    public static <K, V> Map<K, V> newHashMap() {
        return Maps.newHashMap();
    }

    public static <K, V> Map<K, V> newHashMap(Map<? extends K, ? extends V> map) {
        return isNull(map) ? Maps.newHashMap() : Maps.newHashMap(map);
    }

    public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Class<K> type) {
        return Maps.newEnumMap(type);
    }

    public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Class<K> type, Map<K, ? extends V> map) {
        EnumMap<K, V> enumMap = newEnumMap(type);
        notNullThenRun(map, enumMap::putAll);
        return enumMap;
    }

    public static <K extends Comparable, V> Map<K, V> newTreeMap(Map<? extends K, ? extends V> map,
                                                                 Comparator<? super K> comparator) {
        val treeMap = new TreeMap<K, V>(comparator);
        notNullThenRun(map, treeMap::putAll);
        return treeMap;
    }

    @SafeVarargs
    public static <K, V> Map<K, V> combineMaps(Map<? extends K, ? extends V>... maps) {
        Map<K, V> result = Maps.newHashMap();
        ArrayUtils.reverse(maps);
        for (val map : maps) {
            if (nonNull(map)) result.putAll(map);
        }
        return result;
    }

    public static <T, K, U>
    Collector<T, ?, Map<K, U>> toMap(Function<? super T, ? extends K> keyMapper,
                                     Function<? super T, ? extends U> valueMapper) {
        return Collectors.toMap(keyMapper, valueMapper, defaultMerger(), MergeHashMap::new);
    }

    public static <T, K, U>
    Collector<T, ?, ConcurrentMap<K, U>> toConcurrentMap(Function<? super T, ? extends K> keyMapper,
                                                         Function<? super T, ? extends U> valueMapper) {
        return Collectors.toConcurrentMap(keyMapper, valueMapper, defaultMerger(), ConcurrentMergeHashMap::new);
    }

    private static <T> BinaryOperator<T> defaultMerger() {
        return (u, v) -> v;
    }

    private static class MergeHashMap<K, V> extends HashMap<K, V> {

        @Serial
        private static final long serialVersionUID = 8293495763789556890L;

        public MergeHashMap() {
            super();
        }

        @Override
        public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            if (isNull(value)) return remove(key);
            return super.merge(key, value, remappingFunction);
        }
    }

    private static class ConcurrentMergeHashMap<K, V> extends ConcurrentHashMap<K, V> {

        @Serial
        private static final long serialVersionUID = 7370568525584783098L;

        public ConcurrentMergeHashMap() {
            super();
        }

        @Override
        public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            if (isNull(value)) return remove(key);
            return super.merge(key, value, remappingFunction);
        }
    }
}
