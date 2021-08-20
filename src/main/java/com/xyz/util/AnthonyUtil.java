package com.xyz.util;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.xyz.util.dto.DataResult;
import org.springframework.web.servlet.ModelAndView;

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

    /**
     * 混合加密
     *
     * @param str 原字符串
     * @return String 加密后字符串
     */
    public static String mixedEncryption(String str) {
        return SecureUtil.md5(SecureUtil.sha1("Z" + SecureUtil.md5("X" + str) + "Y"));
    }

    /**
     * 同步登录验证
     *
     * @param LoginStatus 登录状态，0未登录，2登陆过期
     * @return ModelAndView 未登录时设置viewName，登录时不设置viewName
     */
    public static ModelAndView createForSync(int LoginStatus) {
        if (LoginStatus == 0) {
            return new ModelAndView("/login");
        } else if (LoginStatus == 2) {
            return new ModelAndView("/login");
        }
        return new ModelAndView();
    }

}
