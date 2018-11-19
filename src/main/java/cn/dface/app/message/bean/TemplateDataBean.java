package cn.dface.app.message.bean;/**
 * Created by akun on 2017/12/27.
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author akun
 * @create 2017-12-27 下午8:54
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplateDataBean implements Serializable{

    /**
     * 关键词替代值
     */
    private String value;
    /**
     * 颜色
     */
    private String color;
}
