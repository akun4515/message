package cn.dface.app.message.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author akun
 * @create 2018-10-20 下午5:06
 **/
@Data
public class MessageSendBean implements Serializable {

    private static final long serialVersionUID = 6070749011811576013L;

    private String touser;
    private String msgtype;
    private MessageContentBean image;
}
