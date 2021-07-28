package com.xyz.util;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import cn.hutool.json.JSONUtil;

/**
 * Redis工具，使用Redis时需要在类中用@Autowired注入
 *
 * @author anydeer
 */
@Component
public class RedisUtil {

	@Autowired
	private RedisTemplate<String, String> redis;

	// 存值
	public void set(String key, String value) throws Exception {
		redis.opsForValue().set(key, value);
	}

	// 存值并设置过期时间
	public void set(String key, String value, long time) throws Exception {
		redis.opsForValue().set(key, value, time, TimeUnit.SECONDS);
	}

	// 通过key取值
	public String get(String key) throws Exception {
		return redis.opsForValue().get(key);
	}

	// 删值
	public boolean delete(String key) throws Exception {
		return redis.delete(key);
	}

	// 通过key设置过期时间
	public void setOutTime(String key, long time) throws Exception {
		redis.expire(key, time, TimeUnit.SECONDS);
	}

	// 将java对象转为json字符串存入
	public void setObject(String key, Object obj) throws Exception {
		String json = JSONUtil.toJsonStr(obj);
		redis.opsForValue().set(key, json);
	}
	
	 // 将java对象转为json字符串存入并设置过期时间
    public void setObject(String key, Object obj, long time) throws Exception {
        String json = JSONUtil.toJsonStr(obj);
        redis.opsForValue().set(key, json, time, TimeUnit.SECONDS);
    }

	// 通过key取json字符串转为java对象
	public <T> T getObject(String key, Class<T> clazz) throws Exception {
		String json = redis.opsForValue().get(key);
		return JSONUtil.toBean(json, clazz);
	}
	

}
