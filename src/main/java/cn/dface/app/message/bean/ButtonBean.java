package cn.dface.app.message.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author akun
 * @create 2018-10-26 下午1:52
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ButtonBean implements Serializable {

    private static final long serialVersionUID = -1206699301874503705L;

    private String type;
    private String name;
    private String key;
    private String url;
}
