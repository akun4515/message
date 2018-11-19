package cn.dface.app.message.service;

/**
 * @author akun
 * @create 2018-10-20 下午5:01
 **/
public interface KfService {

    void sendText(String openId,String content);

    void send(String openId,String media_id);
}
