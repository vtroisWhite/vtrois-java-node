package com.java.node.simple.emojiFilter;

import java.util.Arrays;

/**
 * @Description 过滤emoji字符
 */
public class EmojiFilterUtil {

    /**
     * 过滤原理，4字节字符的开头为11110xxx，并且只要是11110xxx便可以保证这个字符的后续四个字节肯定是个特殊符号，所以这样判断来过滤
     *
     * @param
     * @return
     */
    public static String filter(String content) {
        byte[] conbyte = content.getBytes();
        int idx = 0;
        for (int i = 0; i < conbyte.length; ) {
            int charLength = getCharLength(conbyte[i]);
            if (charLength >= 4) {
                i += 4;
                continue;
            }
            conbyte[idx] = conbyte[i];
            i += 1;
            idx++;
        }
        return new String(Arrays.copyOf(conbyte, idx));
    }

    /**
     * 原文链接：https://blog.csdn.net/e19901004/article/details/103880863
     * 1字节 0xxxxxxx
     * 2字节 110xxxxx 10xxxxxx
     * 3字节 1110xxxx 10xxxxxx 10xxxxxx
     * 4字节 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
     * 5字节 111110xx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx
     * 6字节 1111110x 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx
     *
     * @return
     */
    public static int getCharLength(byte b) {
        if ((b & (byte) 0b10000000) == 0) {
            return 1;
        }
        if ((b & (byte) 0b11100000) == (byte) 0b11000000) {
            return 2;
        }
        if ((b & (byte) 0b11110000) == (byte) 0b11100000) {
            return 3;
        }
        if ((b & (byte) 0b11111000) == (byte) 0b11110000) {
            return 4;
        }
        if ((b & (byte) 0b11111100) == (byte) 0b11111000) {
            return 5;
        }
        if ((b & (byte) 0b11111110) == (byte) 0b11111100) {
            return 6;
        }
        return 0;
    }

    /**
     * 将 xF0\x9F\xA4\xA3 这种4字节的数据，转为其原本的内容
     *
     * @param code
     * @return
     */
    private static String coverEmoji(String code) {
        String[] split = code.substring(code.indexOf("x")).split("\\\\");
        byte[] bytes = new byte[split.length];
        for (int i = 0; i < split.length; i++) {
            bytes[i] = Integer.decode("0" + split[i]).byteValue();
        }
        return new String(bytes);
    }

    public static void main(String[] args) {
        String str = "\"key\":\"热带鱼\uD83D\uDC1F童装\"😒🤣🎁🐱‍💻😁🤷‍♀️😜✨😂😊😎🎉💋🤞🙌😃❤😍👌😘💕👍🤦‍♀️🤦‍♂️🌹👏💖😢🎶😉✌🤷‍♂️🎂🤳🐱‍👤🐱‍🏍🐱‍🐉🐱‍👓🐱‍🚀✔🤢🤔😆👀💅⏰📊🍨🍯🚦🚧♎♓💫🕖🗯🔹key\":{\"key\":🔥{\"key1\":\"中国🐷\",\"key2\":\"广👀东省\",\"city\":\"江门市\",\"district\":\"蓬江区\"}},\"deviceIn下😎fo";
        System.out.println("原始字符=" + str);
        System.out.println("过滤结果=" + filter(str));
        String emoji = coverEmoji("xF0\\x9F\\xA4\\xA3");
        System.out.println("原始字符=" + emoji);
        System.out.println("过滤结果=" + filter(emoji));
    }
}

