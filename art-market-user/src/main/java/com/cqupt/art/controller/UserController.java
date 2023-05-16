package com.cqupt.art.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.cqupt.art.entity.User;
import com.cqupt.art.entity.to.UserTo;
import com.cqupt.art.entity.vo.UserQueryVo;
import com.cqupt.art.service.UserService;
import com.cqupt.art.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-03
 */
@CrossOrigin
@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {
    @Autowired
    UserService userService;


//    @GetMapping("/testSession")
//    public R testSession(HttpSession session){
//        User loginUser = (User)session.getAttribute("loginUser");
//        log.info("session中的loginUser：{}",JSON.toJSONString(loginUser));
//        return R.ok().put("loginUser",loginUser);
//    }

    @GetMapping("/list/{curPage}/{capacity}")
    public R userList(@PathVariable("curPage") int curPage, @PathVariable("capacity") int capacity) {
        List<User> users = userService.userList(curPage, capacity);
        return R.ok().put("items", users).put("total", users.size());
    }

    @PostMapping("/queryUser")
    public R queryUser(@RequestBody UserQueryVo queryVo) {
        List<User> users = userService.queryUser(queryVo);
        return R.ok().put("items", users).put("total", users.size());
    }

    @GetMapping("/getUserByPhone/{phone}")
    public R getUserByPhone(@PathVariable("phone") String phone) {
        User user = userService.getOne(new QueryWrapper<User>().eq("user_phone", phone));
        if (user != null) {
            return R.ok().put("data", user);
        } else {
            return R.error("查询用户失败！");
        }
    }

    @PostMapping("/updateUser")
    public R updateUser(@RequestBody User user) {
        int flag = userService.updateUser(user);
        if (flag > 0) {
            return R.ok("更新用户成功！");
        } else {
            return R.error("更新用户失败!");
        }
    }

    @GetMapping("/deleteUser/{uid}")
    public R deleteUser(@PathVariable("uid") String uid) {
        // todo: 实际删除，应该改为逻辑删除
        boolean deleted = userService.remove(new QueryWrapper<User>().eq("user_id", uid));
        if (deleted) {
            return R.ok("删除成功！");
        } else {
            return R.error("删除失败！");
        }
    }

    @GetMapping("/airdrop/{id}/{artId}")
    public R airdrop(@PathVariable("id") String uid, @PathVariable("artId") String artId) {
        String txHash = userService.airdrop(uid, artId);
        if (txHash.substring(0, 2).equals("0x")) {
            return R.ok().put("data", txHash);
        } else {
            return R.error(txHash);
        }
    }

    /**
     * @author :lhx
     * 加的接口，通过用户id返回用户的手机号和区块链地址
     */
    @GetMapping("/getPhoneAndAddById")
    public R getPhoneAndAddById(@RequestParam("/id") String uid) {
        User byId = userService.getById(uid);
        UserTo userTo = new UserTo();
        userTo.setUserPhone(byId.getUserPhone());
        userTo.setChainAddress(byId.getChainAddress());
        return R.ok().put("data", userTo);
    }
}
