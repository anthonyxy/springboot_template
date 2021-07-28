package com.xyz.task;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.hutool.core.date.DateUtil;

// 定时任务
@EnableScheduling
@Component
@Transactional(rollbackFor = Exception.class)
public class Task {

    private static final Logger logger = LoggerFactory.getLogger(Task.class);

    // 项目启动时执行一次
    @PostConstruct
    public void init() {
        logger.warn("程序已启动>>>" + DateUtil.now());
    }

    // 定时任务测试每半点一次
    @Scheduled(cron = "0 30 * * * ?")
    public void testTask() {
        logger.warn("定时任务开始-测试>>>" + DateUtil.now());
    }

}
