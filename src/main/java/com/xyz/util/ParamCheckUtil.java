package com.xyz.util;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ParamCheckUtil {

    public static boolean isMobile(String mobile) {
        String regex = "^(((13[0-9])|(14[579])|(15([0-3]|[5-9]))|(16[6])|(17[0135678])|(18[0-9])|(19[89]))\\d{8})$";
        return ReUtil.isMatch(regex, mobile);
    }

    public static boolean isPinteger(String arg) {
        String regex = "\\d+";
        return ReUtil.isMatch(regex, arg);
    }

    public static boolean isEmall(String emall) {
        String regex = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        return ReUtil.isMatch(regex, emall);
    }

    public static boolean isFormatDate(String dateStr, String regulation) {
        if (StrUtil.isBlank(dateStr)) {
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(regulation);
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isInclude(String param, String regulation) {
        try {
            String[] include = regulation.split(",");
            for (String string : include) {
                if (param.equals(string)) {
                    return true;
                }
            }
            return false;
        } catch (RuntimeException e) {
            return false;
        }
    }

}
