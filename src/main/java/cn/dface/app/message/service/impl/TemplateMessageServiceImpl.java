package cn.dface.app.message.service.impl;

import cn.dface.app.message.bean.TemplateMessageBean;
import cn.dface.app.message.bean.WxResponseBean;
import cn.dface.app.message.cache.AccessTokenCacheFacade;
import cn.dface.app.message.constant.WxErrorCode;
import cn.dface.app.message.service.TemplateMessageService;
import com.alibaba.fastjson.JSON;
import net.dongliu.requests.Requests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author akun
 * @create 2018-10-23 下午4:39
 **/
@Service
public class TemplateMessageServiceImpl implements TemplateMessageService {

    private static final String TEMPLATE_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";
    @Autowired
    private AccessTokenCacheFacade   accessTokenCacheFacade;
    @Override
    public void send(TemplateMessageBean templateMessageBean) {

        String accessToken = accessTokenCacheFacade.getAccessToken();
        String result = Requests.post(TEMPLATE_MESSAGE_URL + accessToken ).body(JSON.toJSONString(templateMessageBean)).send().readToText();
        System.out.println(result);
        WxResponseBean wxResponseBean = JSON.parseObject(result,WxResponseBean.class);
        if(wxResponseBean != null && wxResponseBean.getErrcode() == WxErrorCode.ACCESS_TOKEN_ERROR){
            accessTokenCacheFacade.refresh();
            this.send(templateMessageBean);
        }
    }

}
