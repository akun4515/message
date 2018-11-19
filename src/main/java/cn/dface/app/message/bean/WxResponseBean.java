package cn.dface.app.message.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author akun
 * @create 2018-10-20 下午2:27
 **/
@Data
public class WxResponseBean implements Serializable {

    private static final long serialVersionUID = -5162575633143459009L;

    private Integer  errcode;
    private String   errmsg;
}
