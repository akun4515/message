package cn.dface.app.message.cache;

import cn.dface.app.message.service.AccessTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * @author akun
 * @create 2018-10-20 下午1:56
 **/
@Service
public class AccessTokenCacheFacadeImpl implements AccessTokenCacheFacade {

    private  String accessToken;
    @Autowired
    private AccessTokenService accessTokenService;

    @PostConstruct
    public void init(){
       get();
    }

    private void get(){
        String accessToken = accessTokenService.getAccessToken();
        if(StringUtils.isEmpty(accessToken)){
            throw new RuntimeException("get accessToken error ,start fail");
        }
        this.accessToken = accessToken;
    }

    @Override
    public String getAccessToken() {
        if(!StringUtils.isEmpty(accessToken)){
            return this.accessToken;
        }else{
            get();
            return this.accessToken;
        }
    }

    @Override
    public String refresh() {
        get();
        return this.accessToken;
    }
}
