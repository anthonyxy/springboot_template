package com.xyz.util;

import java.util.Arrays;

import com.xyz.config.SystemConfig;

import cn.hutool.crypto.SecureUtil;

public class SignatureUtil {

	public static boolean checkSignature(String sign, String timestamp, String nonce) {
		String[] arr = new String[] { SystemConfig.SIGN, timestamp, nonce };
		Arrays.sort(arr);
		StringBuilder content = new StringBuilder();
		for (String string : arr) {
			content.append(string);
		}
		String signature = SecureUtil.md5(content.toString()).toUpperCase();
		return sign.equals(signature);
	}

}
