package com.xyz.util;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import cn.hutool.core.util.RandomUtil;

/**
 * 其他工具
 */
public class AnthonyUtil {

    /**
     * 生成特定位数的随机数
     *
     * @param digit 生成随机数的位数
     * @return int 随机数
     */
    public static int random(int digit) {
        int min = (int) Math.pow(10, digit - 1);
        int max = (int) Math.pow(10, digit);
        return RandomUtil.randomInt(min, max);
    }

    /**
     * 拼音首字母排序
     *
     * @param data 需要排序的List<String>
     * @return List<String> 排序后的List
     */
    public static List<String> initialSort(List<String> data) {
        if (data == null || data.size() == 0) {
            return null;
        }
        String[] array = new String[data.size()];
        data.toArray(array);
        Comparator<Object> comparator = Collator.getInstance(java.util.Locale.CHINA);
        Arrays.sort(array, comparator);
        return Arrays.asList(array);
    }

}
