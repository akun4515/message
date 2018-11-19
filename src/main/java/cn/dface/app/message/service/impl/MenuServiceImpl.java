package cn.dface.app.message.service.impl;

import cn.dface.app.message.bean.MenuBean;
import cn.dface.app.message.bean.WxResponseBean;
import cn.dface.app.message.cache.AccessTokenCacheFacade;
import cn.dface.app.message.constant.WxErrorCode;
import cn.dface.app.message.service.MenuService;
import com.alibaba.fastjson.JSON;
import net.dongliu.requests.Requests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author akun
 * @create 2018-10-26 下午1:49
 **/
@Service
public class MenuServiceImpl implements MenuService {

    private static final String MENU_ADD_URL = " https://api.weixin.qq.com/cgi-bin/menu/create?access_token=";
    @Autowired
    private AccessTokenCacheFacade  accessTokenCacheFacade;

    @Override
    public void addMenu(MenuBean menuBean) {

        String token = this.accessTokenCacheFacade.getAccessToken();
        String result = Requests.post(MENU_ADD_URL + token).body(JSON.toJSONString(menuBean)).send().readToText();
        WxResponseBean wxResponseBean = JSON.parseObject(result,WxResponseBean.class);
        if(wxResponseBean != null && wxResponseBean.getErrcode() == WxErrorCode.ACCESS_TOKEN_ERROR){
            this.accessTokenCacheFacade.refresh();
            this.addMenu(menuBean);
        }
    }
}
