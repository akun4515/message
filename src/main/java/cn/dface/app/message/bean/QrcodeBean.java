package cn.dface.app.message.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author akun
 * @create 2018-10-20 下午2:57
 **/
@Data
public class QrcodeBean extends WxResponseBean implements Serializable {
    private static final long serialVersionUID = -9155856584880775165L;

    private String ticket;
    private long   expire_seconds;
    private String url;
}
