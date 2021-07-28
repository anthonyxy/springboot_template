package com.xyz.service.impl;

import com.xyz.service.UserService;
import org.springframework.stereotype.Service;

import com.xyz.util.dto.DataResult;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public DataResult getCode(String phoneNumber) throws Exception {
        return null;
    }

    @Override
    public DataResult login(String phoneNumber, String code) throws Exception {
        return null ;
    }

    @Override
    public DataResult logout(Long userId) throws Exception {
        return null;
    }

//    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
//
//    @Autowired
//    private UserInfoMapper userInfoMapper;
//
//    @Autowired
//    private RedisUtil redis;
//
//    @Autowired
//    private AliSMSUtil aliSMS;
//
//    // 用户登录短信验证码
//    @Override
//    public DataResult getCode(String phoneNumber) throws Exception {
//        String result = aliSMS.sendCode(phoneNumber, 1);
//        if (!"success".equals(result)) {
//            return DataResult.build250(result);
//        }
//        return DataResult.build100();
//    }
//
//    // 用户登录（第一次登录为注册）
//    @Override
//    public DataResult login(String phoneNumber, String code) throws Exception {
//
//        // 验证短信
//        String result = aliSMS.verifyCode(phoneNumber, 1, code);
//        if (!"success".equals(result)) {
//            return DataResult.build250(result);
//        }
//
//        // 查找该手机号的用户
//        UserInfoExample uiExample = new UserInfoExample();
//        UserInfoExample.Criteria uiCriteria = uiExample.createCriteria();
//        uiCriteria.andPhoneNumberEqualTo(phoneNumber);
//        List<UserInfo> users = userInfoMapper.selectByExample(uiExample);
//
//        // 注册
//        UserInfo ui = null;
//        Map<String, Object> map = new HashMap<>();
//        if (users.size() == 0) { // 不存在，注册
//            BigDecimal bigDecimal = new BigDecimal("0.00");
//            Byte f = 0;
//            Date date = new Date();
//            ui = new UserInfo();
//            ui.setUserExteriorId(0L);
//            ui.setPositionType(0);
//            ui.setPhoneNumber(phoneNumber);
//            ui.setNickName("用户" + phoneNumber);
//            ui.setHeadImgCode("default_head");
//            ui.setIsAuthentication(f);
//            ui.setMoney(bigDecimal);
//            ui.setPointNumber(0);
//            ui.setToyearPointNumber(0);
//            ui.setYesteryearPointNumber(0);
//            ui.setIsGuarantee(f);
//            ui.setIsCaptainGuarantee(f);
//            ui.setIsTeam(f);
//            ui.setIsBan(f);
//            ui.setLastLoginTime(date);
//            ui.setuTime(date);
//            ui.setcTime(date);
//            ui.setIsDelete(f);
//            userInfoMapper.insert(ui);
//            // 生成外部id
//            long exteriorId = ui.getId() * 10 - AnydeerUtil.random(1) + 1235462L;
//            ui.setUserExteriorId(exteriorId);
//            userInfoMapper.updateByPrimaryKeySelective(ui);
//            // 注册弹出完善资料页
//            map.put("isPerfection", 1);
//        } else { // 存在
//            ui = users.get(0);
//            if (ui.getIsBan() == 1) {
//                return DataResult.build250("该账号已被禁用");
//            }
//            map.put("isPerfection", 0);
//        }
//
//        // 原账号挤下线，删除原token
//        String oldTolen = redis.get("User" + ui.getId());
//        if (StrUtil.isNotBlank(oldTolen)) {
//            redis.delete(oldTolen);
//            redis.delete("User" + ui.getId());
//            logger.warn("用户id" + ui.getId() + "被挤下线");
//        }
//
//        // 生成用户登录token存入redis
//        String token = SecureUtil.md5(ui.getId() + "" + System.currentTimeMillis());
//        // 以token为键存入
//        redis.set(token, ui.getId() + "", SystemConfig.LOGIN_OUT_TIME);
//        // 以token为值存入
//        redis.set("User" + ui.getId(), token, SystemConfig.LOGIN_OUT_TIME);
//        logger.warn("用户id" + ui.getId() + "上线");
//        // 返回数据
//        map.put("id", ui.getId());
//        map.put("exteriorId", ui.getUserExteriorId());
//        map.put("nickName", ui.getNickName());
//        map.put("headImgCode", ui.getHeadImgCode());
//        map.put("userToken", token);
//        return DataResult.build100(map);
//    }
//
//    // 用户登出
//    @Override
//    public DataResult logout(Long userId) throws Exception {
//        UserInfo ui = userInfoMapper.selectByPrimaryKey(userId);
//        // 原账号挤下线，删除原token
//        String oldTolen = redis.get("User" + ui.getId());
//        if (StrUtil.isNotBlank(oldTolen)) {
//            redis.delete(oldTolen);
//            redis.delete("User" + ui.getId());
//            logger.warn("用户id" + ui.getId() + "手动下线");
//        }
//        return DataResult.build100();
//    }

}
