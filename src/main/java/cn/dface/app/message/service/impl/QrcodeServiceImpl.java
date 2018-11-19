package cn.dface.app.message.service.impl;

import cn.dface.app.message.bean.QrcodeBean;
import cn.dface.app.message.cache.AccessTokenCacheFacade;
import cn.dface.app.message.constant.WxErrorCode;
import cn.dface.app.message.service.QrcodeService;
import com.alibaba.fastjson.JSON;
import net.dongliu.requests.Requests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author akun
 * @create 2018-10-18 下午6:05
 **/
@Service
public class QrcodeServiceImpl implements QrcodeService {

    private static final String WX_QRCODE_URL = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=";

    private static final String WX_QRCODE_TICKET_URL = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=";

    private static final String PARAM = "{\"action_name\": \"QR_LIMIT_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": %d}}}";

    @Autowired
    private AccessTokenCacheFacade accessTokenCacheFacade;
    @Override
    public String getQrcode(Integer userId) {
        return get(userId);
    }

    private String get(Integer userId){

        String accessToken = accessTokenCacheFacade.getAccessToken();
        String result = Requests.post(WX_QRCODE_URL + accessToken).body(String.format(PARAM,userId)).send().readToText();
        System.out.println(result);
        QrcodeBean qrcodeBean = JSON.parseObject(result,QrcodeBean.class);
        Integer errorCode = qrcodeBean.getErrcode();
        String url = qrcodeBean.getUrl();
        if(!StringUtils.isEmpty(url)){
            return url;
        }else{
            if(errorCode != null &&  errorCode == WxErrorCode.ACCESS_TOKEN_ERROR){
                this.accessTokenCacheFacade.refresh();
                return get(userId);
            }else{
                throw new RuntimeException("get qrcode error");
            }
        }
    }
}
