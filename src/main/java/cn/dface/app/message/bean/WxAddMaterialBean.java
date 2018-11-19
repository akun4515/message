package cn.dface.app.message.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author akun
 * @create 2018-10-23 下午1:17
 **/
@Data
public class WxAddMaterialBean extends WxResponseBean implements Serializable {
    private static final long serialVersionUID = 4393461558193508582L;

    private String media_id;
    private String url;
    private String type;
    private long   created_at;
}
