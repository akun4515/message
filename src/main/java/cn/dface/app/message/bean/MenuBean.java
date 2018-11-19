package cn.dface.app.message.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author akun
 * @create 2018-10-26 下午1:51
 **/
@Data
public class MenuBean implements Serializable {
    private static final long serialVersionUID = -1932235976837067342L;

    private List<ButtonBean> button;

}
