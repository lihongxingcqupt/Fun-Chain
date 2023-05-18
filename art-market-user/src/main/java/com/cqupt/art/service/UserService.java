package com.cqupt.art.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.art.entity.User;
import com.cqupt.art.entity.to.UserLoginTo;
import com.cqupt.art.entity.vo.UserQueryVo;
import com.cqupt.art.entity.vo.UserRegisterVo;
import com.cqupt.art.utils.R;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author lihongxing
 * @since 2022-11-03
 */
public interface UserService extends IService<User> {

    R regist(UserRegisterVo registerVo);

    List<User> userList(int curPage, int capacity);

    List<User> queryUser(UserQueryVo queryVo);

    int updateUser(User user);

    String airdrop(String uid, String artId);

    User login(UserLoginTo userLoginVo);
}
