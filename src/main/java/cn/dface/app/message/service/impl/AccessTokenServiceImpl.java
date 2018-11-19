package cn.dface.app.message.service.impl;

import cn.dface.app.message.bean.AccessTokenBean;
import cn.dface.app.message.constant.BaseConstant;
import cn.dface.app.message.service.AccessTokenService;
import com.alibaba.fastjson.JSON;
import net.dongliu.requests.Requests;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author akun
 * @create 2018-10-18 下午6:04
 **/
@Service
public class AccessTokenServiceImpl implements AccessTokenService {

    private static final String WX_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";

    @Override
    public String getAccessToken() {
        StringBuilder url = new StringBuilder(WX_ACCESS_TOKEN_URL);
        url.append("&appid=");
        url.append(BaseConstant.APP_ID);
        url.append("&secret=");
        url.append(BaseConstant.APP_SECRET);
        String result = Requests.get(url.toString()).send().readToText();
        System.out.println(result);
        AccessTokenBean accessTokenBean = JSON.parseObject(result,AccessTokenBean.class);
        if(accessTokenBean != null && !StringUtils.isEmpty(accessTokenBean.getAccess_token())){
            return accessTokenBean.getAccess_token();
        }else{
            throw new RuntimeException("get accessToken error");
        }
    }
}
