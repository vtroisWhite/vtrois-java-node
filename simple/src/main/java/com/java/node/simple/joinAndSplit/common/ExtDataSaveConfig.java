package com.java.node.simple.joinAndSplit.common;

import cn.hutool.core.codec.Base64;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 *
 */
public class ExtDataSaveConfig<Entity, T> implements ExtDataHandleFunc<Entity, T> {

    private static final String delimiter = ",";
    @Getter
    private final String desc;
    @Getter
    private final int saveIdx;
    @Getter
    private final Function<Entity, String> getExtFunc;
    @Getter
    private final BiConsumer<Entity, String> setExtFunc;
    @Getter
    private final Function<T, String> formatExtDataFunc;
    @Getter
    private final Function<String, T> parseExtDataFunc;

    public ExtDataSaveConfig(String desc, int saveIdx,
                             Function<Entity, String> getExtFunc, BiConsumer<Entity, String> setExtFunc,
                             Function<T, String> formatExtDataFunc, Function<String, T> parseExtDataFunc) {
        this.desc = desc;
        this.saveIdx = saveIdx;
        this.getExtFunc = getExtFunc;
        this.setExtFunc = setExtFunc;
        this.formatExtDataFunc = formatExtDataFunc;
        this.parseExtDataFunc = parseExtDataFunc;
    }

    private static String[] splitExt(String ext) {
        if (ext == null) {
            return null;
        }
        return ext.split("(?<!\\\\)" + delimiter);
    }

    private static String convertDelimiter(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll(delimiter, "\\\\" + delimiter);
    }

    private static String reconvertDelimiter(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("\\\\" + delimiter, delimiter);
    }

    public static <Entity> ExtDataSaveConfig<Entity, Integer> buildInteger(String desc, int saveIdx,
                                                                           Function<Entity, String> getExtFunc, BiConsumer<Entity, String> setExtFunc) {
        return new ExtDataSaveConfig<>(desc, saveIdx, getExtFunc, setExtFunc, x -> x == null ? null : x.toString(), Integer::valueOf);
    }

    public static <Entity> ExtDataSaveConfig<Entity, String> buildString(String desc, int saveIdx,
                                                                         Function<Entity, String> getExtFunc, BiConsumer<Entity, String> setExtFunc) {
        return new ExtDataSaveConfig<>(desc, saveIdx, getExtFunc, setExtFunc, Function.identity(), Function.identity());
    }

    public static <Entity> ExtDataSaveConfig<Entity, BigDecimal> buildDecimal(String desc, int saveIdx,
                                                                              Function<Entity, String> getExtFunc, BiConsumer<Entity, String> setExtFunc) {
        return new ExtDataSaveConfig<>(desc, saveIdx, getExtFunc, setExtFunc, x -> x.stripTrailingZeros().toPlainString(), BigDecimal::new);
    }

    public static <Entity> ExtDataSaveConfig<Entity, String> buildEncrypt(String desc, int saveIdx,
                                                                          Function<Entity, String> getExtFunc, BiConsumer<Entity, String> setExtFunc) {
        return new ExtDataSaveConfig<>(desc, saveIdx, getExtFunc, setExtFunc,
                str -> str == null ? null : Base64.encode(str),
                str -> str == null ? null : Base64.decodeStr(str));
    }

    public T getExtData(Entity entity) {
        String ext = getGetExtFunc().apply(entity);
        if (ext == null) {
            return null;
        }
        String[] split = splitExt(ext);
        int saveIdx = getSaveIdx();
        if (saveIdx >= split.length || "".equals(split[saveIdx])) {
            return null;
        }
        return getParseExtDataFunc().apply(reconvertDelimiter(split[saveIdx]));
    }

    public void setExtData(Entity entity, T extData) {
        String data = extData == null ? null : getFormatExtDataFunc().apply(extData);
        String ext = getGetExtFunc().apply(entity);
        int saveIdx = getSaveIdx();
        String[] split = Optional.ofNullable(splitExt(ext)).orElseGet(() -> new String[saveIdx + 1]);
        if (split.length <= saveIdx) {
            split = Arrays.copyOf(split, saveIdx + 1);
        }
        split[saveIdx] = convertDelimiter(data);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            sb.append(split[i] == null ? "" : split[i]);
        }
        getSetExtFunc().accept(entity, sb.toString());
    }
}
