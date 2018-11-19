package cn.dface.app.message.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author akun
 * @create 2018-10-23 下午4:41
 **/
@Data
public class TemplateMessageBean implements Serializable {

    private String touser;
    private String template_id;
    private String url;
    private Map<String,TemplateDataBean> data;

}
