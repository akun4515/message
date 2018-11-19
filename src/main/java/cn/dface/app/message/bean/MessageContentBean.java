package cn.dface.app.message.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author akun
 * @create 2018-10-20 下午5:12
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageContentBean implements Serializable {
    private static final long serialVersionUID = 4161042514244008502L;

    private String media_id;
}
