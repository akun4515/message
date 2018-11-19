package cn.dface.app.message.service.impl;

import cn.dface.app.message.bean.MessageContentBean;
import cn.dface.app.message.bean.MessageSendBean;
import cn.dface.app.message.bean.WxAddMaterialBean;
import cn.dface.app.message.bean.WxResponseBean;
import cn.dface.app.message.cache.AccessTokenCacheFacade;
import cn.dface.app.message.constant.WxErrorCode;
import cn.dface.app.message.service.KfService;
import com.alibaba.fastjson.JSON;
import net.dongliu.requests.Requests;
import net.dongliu.requests.body.Part;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author akun
 * @create 2018-10-20 下午5:03
 **/
@Service
public class KfServiceImpl implements KfService {

    private static final String WX_KF_SEND_URL = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=";

    private static final String WX_KF_TEXT_MSG = "{\"touser\":\"%s\",\"msgtype\":\"text\",\"text\":{\"content\":\"%s\"}}";
    @Autowired
    private AccessTokenCacheFacade accessTokenCacheFacade;

    @Override
    public void sendText(String openId, String content) {
        String accessToken = this.accessTokenCacheFacade.getAccessToken();
        String result = Requests.post(WX_KF_SEND_URL + accessToken).body(String.format(WX_KF_TEXT_MSG,openId,content)).send().readToText();
        System.out.println(result);
        WxResponseBean wxResponseBean = JSON.parseObject(result,WxResponseBean.class);
        if(wxResponseBean != null && wxResponseBean.getErrcode() != null && wxResponseBean.getErrcode() ==
                WxErrorCode.ACCESS_TOKEN_ERROR){
            this.accessTokenCacheFacade.refresh();
            this.sendText(openId,content);
        }
    }

    @Override
    public void send(String openId, String media_id) {

        sendMessage(openId,media_id);
    }

    private void sendMessage(String openId, String media_id){
        MessageSendBean messageSendBean = new MessageSendBean();
        messageSendBean.setTouser(openId);
        messageSendBean.setMsgtype("image");
        messageSendBean.setImage(MessageContentBean.builder().media_id(media_id).build());
        String accessToken = this.accessTokenCacheFacade.getAccessToken();
        String result = Requests.post(WX_KF_SEND_URL + accessToken).body(JSON.toJSONString(messageSendBean)).send().readToText();
        System.out.println(result);
        WxResponseBean wxResponseBean = JSON.parseObject(result,WxResponseBean.class);
        if(wxResponseBean != null && wxResponseBean.getErrcode() != null && wxResponseBean.getErrcode() ==
                WxErrorCode.ACCESS_TOKEN_ERROR){
            this.accessTokenCacheFacade.refresh();
            this.sendMessage(openId,media_id);
        }
    }
}
