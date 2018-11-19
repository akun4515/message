package cn.dface.app.message.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author akun
 * @create 2018-02-06 下午2:27
 **/
@Data
public class OpenIdVo implements Serializable {

    private List<String> openid;
}
