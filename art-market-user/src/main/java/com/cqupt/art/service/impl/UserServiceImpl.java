package com.cqupt.art.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.art.config.mq.RegisterMqConfig;
import com.cqupt.art.entity.User;
import com.cqupt.art.entity.to.UserLoginTo;
import com.cqupt.art.entity.vo.UserQueryVo;
import com.cqupt.art.entity.vo.UserRegisterVo;
import com.cqupt.art.enu.UserStatusEnum;
import com.cqupt.art.exception.UserException;
import com.cqupt.art.feign.ChainClient;
import com.cqupt.art.feign.TradeClient;
import com.cqupt.art.mapper.PmUserMapper;
import com.cqupt.art.service.UserService;
import com.cqupt.art.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-03
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<PmUserMapper, User> implements UserService {
    @Autowired
    private ChainClient chainClient;
    @Autowired
    private TradeClient tradeClient;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;

    //合法性校验交给校验注解去做，这里只做逻辑校验
    @Override
    public R regist(UserRegisterVo registerVo) {
        log.info("注册用户信息：{}", JSON.toJSONString(registerVo));
        User user = new User();
        //验证码校验
        String key = "nft:sms:content:code:" + registerVo.getPhoneNumber();
        String smsCode = redisTemplate.opsForValue().get(key);
        log.info("redis中的验证码：{}", smsCode);
        if (smsCode == null || registerVo.getSmsCode() == null || !smsCode.equals(registerVo.getSmsCode())) {
            return R.error("验证码已过期或验证码错误！");
        } else {
            redisTemplate.delete(key);
        }
        User userByPhone = baseMapper.selectOne(new QueryWrapper<User>().eq("user_phone", registerVo.getPhoneNumber()));
        if (userByPhone != null) {
            return R.error("手机号已注册！");
        }
        //todo 使用MD5加密，手机号验证，验证码验证
        user.setPassword(registerVo.getPassword());
        user.setUserName("**艺术_" + registerVo.getPhoneNumber().substring(0, 4));
        user.setUserPhone(registerVo.getPhoneNumber());

        user.setUserStatus(UserStatusEnum.UN_AUTHORED_USER.getCode());
        // todo: 生成链上信息，应该交给消息队列，解耦
        rabbitTemplate.convertAndSend(RegisterMqConfig.REGISTER_EXCHANGE, RegisterMqConfig.REGISTER_CONSUME_ROUTING_KEY, registerVo);
//        R result = chainClient.createAccount(registerVo.getPassword());
//        AccountInfoTo ai = result.getData("data", new TypeReference<AccountInfoTo>() {
//        });
//        user.setChainAddress(ai.getAddress());
//        user.setPrivateKey(ai.getPrivateKey());
//        user.setAccountPassword(ai.getPassword());
        log.info("user  {}", user);
        this.save(user);
        return R.ok("注册成功！");
    }

    @Override
    public List<User> userList(int curPage, int capacity) {
        Page<User> userPage = new Page<>(curPage, capacity);
        IPage<User> userIPage = baseMapper.selectPage(userPage, null);
        List<User> records = userIPage.getRecords();
        return records;
    }

    @Override
    public List<User> queryUser(UserQueryVo queryVo) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(queryVo.getUserPhone())) {
            wrapper.eq("user_phone", queryVo.getUserPhone());
        }
        if (queryVo.getUserStatus() != null) {
            wrapper.eq("user_status", queryVo.getUserStatus());
        }
        List<User> users = baseMapper.selectList(wrapper);
        return users;
    }

    @Override
    public int updateUser(User user) {
        if (!StringUtils.isEmpty(user.getUserId())) {
            return baseMapper.updateById(user);
        }
        return 0;
    }

    @Override
    public String airdrop(String uid, String artId) {
        User user = baseMapper.selectById(uid);
        if (user == null || user.getCardID() == null) {
            throw new UserException("用户不存在或未实名！");
        }
        String toAddress = user.getChainAddress();
        log.info("user airdrop uid:{} toAddress {},artId:{}", uid, toAddress, artId);
        R r = tradeClient.airdrop(uid, toAddress, artId, 1);
        if (r != null) {
            String txHash = (String) r.get("data");
            if (!StringUtils.isEmpty(txHash)) {
                return txHash;
            }
            return (String) r.get("msg");
        }
        return null;
    }

    @Override
    public User login(UserLoginTo userLoginVo) {
        User user = baseMapper.selectOne(new QueryWrapper<User>().eq("user_phone", userLoginVo.getPhoneNumber()));
        if (user != null && userLoginVo.getPassword().equals(user.getPassword())) {
            return user;
        }
        return null;
    }
}
