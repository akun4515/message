package cn.dface.app.message.service.impl;

import cn.dface.app.message.bean.WxAddMaterialBean;
import cn.dface.app.message.cache.AccessTokenCacheFacade;
import cn.dface.app.message.constant.WxErrorCode;
import cn.dface.app.message.service.WxUploadService;
import com.alibaba.fastjson.JSON;
import net.dongliu.requests.Requests;
import net.dongliu.requests.body.Part;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMapMethodArgumentResolver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author akun
 * @create 2018-10-23 下午1:14
 **/
@Service
public class WxUploadServiceImpl implements WxUploadService {

    public static final String UPLOAD_IMG_FOREVER_URL = "https://api.weixin.qq.com/cgi-bin/material/add_material?type=image&access_token=";

    public static final String UPLOAD_MEDIA_URL = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=%s&type=image";
    @Autowired
    private AccessTokenCacheFacade accessTokenCacheFacade;

    @Override
    public WxAddMaterialBean uploadImg(byte[] bytes) {
        List<Part<?>> parts = new ArrayList<>();
        Part<?> part = Part.file("media","WechatIMG558.jpeg",bytes);
        part.contentType("application/octet-stream");
        parts.add(part);
        String accessToken = accessTokenCacheFacade.getAccessToken();
        String result = Requests.post(String.format(UPLOAD_MEDIA_URL,accessToken))
                .multiPartBody(parts).send().readToText();
        System.out.println(result);
        WxAddMaterialBean wxAddMaterial = JSON
                .parseObject(result, WxAddMaterialBean.class);
        System.out.println(wxAddMaterial);
        if (wxAddMaterial.getErrcode() != null && WxErrorCode.ACCESS_TOKEN_ERROR1 == wxAddMaterial.getErrcode()) {
            accessTokenCacheFacade.refresh();
            uploadImg(bytes);
        }
        return wxAddMaterial;
    }
}
