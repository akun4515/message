package cn.dface.app.message.service.impl;

import cn.dface.app.message.bean.WxAddMaterialBean;
import cn.dface.app.message.bo.UserBo;
import cn.dface.app.message.constant.BaseConstant;
import cn.dface.app.message.init.UserInit;
import cn.dface.app.message.service.*;
import cn.dface.app.message.utils.ImageUtils;
import cn.dface.app.message.utils.ZxingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * @author akun
 * @create 2018-10-18 下午6:06
 **/
@Service
public class MessageSendServiceImpl implements MessageSendService {

    @Autowired
    private KfService  kfService;
    @Autowired
    private QrcodeService qrcodeService;
    @Autowired
    private WxUploadService wxUploadService;
    @Autowired
    private SyncUserDataService syncUserDataService;
    @Value("${image.path}")
    private String imagePath;
    @Override
    @Async
    public void send(Integer userId) {

        UserBo userBo = UserInit.userMap.get(userId);
        if (userBo == null) {
            throw new RuntimeException("can not find userinfo");
        }
        kfService.sendText(userBo.getOpenId(),"您好，"+userBo.getName()+"\n" +
                "\n" +
                "免费半年畅跳卡领取方法：\n" +
                "①保存下方海报，发送到【朋友圈】\n" +
                "②邀请【66位】朋友帮您扫码关注，即可免费领取价值1980元的半年畅跳卡\n" +
                "③街舞学习卡仅限【前30位】完成任务的家长领取\n" +
                "④好友取消关注，助力将失效喔\n" +
                "⑤活动仅限11月11日之前参与！\n" +
                "⑥爱可米宁波方圆校区仅限常规班老生参与\n" +
                "\n" +
                "--------------------\n" +
                "↓ 下面是您的专属海报，将这张海报转发给身边的朋友，邀请大家一起来参加爱可米的锦鲤跳龙门活动吧，让你的宝贝，改变从这里开始。");
        if(StringUtils.isEmpty(userBo.getMediaId())){
            uploadImg(userBo);
        }
        if(!StringUtils.isEmpty(userBo.getMediaId()) ){
            if(userBo.getTime() == null){
                kfService.send(userBo.getOpenId(),userBo.getMediaId());
            }else{
                if(System.currentTimeMillis() / 1000 - userBo.getTime()  < 3 * 24 * 60 * 60){
                    kfService.send(userBo.getOpenId(),userBo.getMediaId());
                }else{
                    uploadImg(userBo);
                    kfService.send(userBo.getOpenId(),userBo.getMediaId());
                }
            }
        }
    }
    private void uploadImg(UserBo userBo){
        String qrcode = this.qrcodeService.getQrcode(userBo.getUserId());
        BufferedImage qrcodeUrlBufferedImage = ZxingUtils.generateQrcode(qrcode, BaseConstant.width,BaseConstant.height);
        BufferedImage bufferedImage = ImageUtils.generateImage(new File(imagePath + "WechatIMG558.jpeg"),qrcodeUrlBufferedImage,userBo.getAvatar(),userBo.getName());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", out);
            WxAddMaterialBean wxAddMaterialBean = wxUploadService.uploadImg(out.toByteArray());
            if(!StringUtils.isEmpty(wxAddMaterialBean.getMedia_id())){
                String mediaId = wxAddMaterialBean.getMedia_id();
                userBo.setMediaId(mediaId);
                userBo.setTime(wxAddMaterialBean.getCreated_at());
                syncUserDataService.syncUserData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
