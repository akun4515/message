package cn.dface.app.message.service;

import cn.dface.app.message.bean.UserInfoVo;

import java.util.List;

/**
 * @author akun
 * @create 2018-10-20 下午5:25
 **/
public interface UserService {

    Integer addUser(String openId);

    void boost(Integer userId,Integer boostUserId);

    void cancelBoost(String openId);

    List<UserInfoVo> getUserList();

    UserInfoVo getUserByOpenId(String openId);

}
