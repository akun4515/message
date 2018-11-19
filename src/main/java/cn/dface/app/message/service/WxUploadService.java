package cn.dface.app.message.service;

import cn.dface.app.message.bean.WxAddMaterialBean;

import java.io.File;

/**
 * @author akun
 * @create 2018-10-23 下午1:14
 **/
public interface WxUploadService  {

    WxAddMaterialBean uploadImg(byte[] bytes);
}
