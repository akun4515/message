package cn.dface.app.message.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author akun
 * @create 2018-02-06 下午2:48
 **/
@Data
public class UserInfoVo extends WxResponseBean implements Serializable{

    /**
     * 是否订阅
     */
    private Integer subscribe;
    /**
     * openId
     */
    private String  openid;
    /**
     * 昵称
     */
    private String  nickname;
    /**
     * 性别
     */
    private Integer sex;
    /**
     * 语言
     */
    private String  language;
    /**
     * 城市
     */
    private String  city;
    /**
     * 省份
     */
    private String  province;
    /**
     * 头像
     */
    private String  headimgurl;
    /**
     * 订阅时间
     */
    private Long    subscribe_time;
    /**
     * unionid
     */
    private String unionid;
    /**
     * 手机号码
     */
    private String phone;

}
