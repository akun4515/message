package cn.dface.app.message.service;

import cn.dface.app.message.bean.TemplateMessageBean;

/**
 * @author akun
 * @create 2018-10-23 下午4:38
 **/
public interface TemplateMessageService {

    void send(TemplateMessageBean templateMessageBean);
}
