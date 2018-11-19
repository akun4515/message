package cn.dface.app.message.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author akun
 * @create 2018-10-20 下午2:27
 **/
@Data
public class AccessTokenBean extends WxResponseBean implements Serializable {
    private static final long serialVersionUID = 1203749479696008124L;

    private String access_token;
}
