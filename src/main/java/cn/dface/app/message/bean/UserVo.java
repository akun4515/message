package cn.dface.app.message.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author akun
 * @create 2018-02-06 下午2:25
 **/
@Data
public class UserVo implements Serializable {

    private Integer   total;
    private Integer   count;
    private String    next_openid;
    private OpenIdVo  data;
}
