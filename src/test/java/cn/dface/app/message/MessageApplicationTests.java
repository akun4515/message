package cn.dface.app.message;

import cn.dface.app.message.bean.ButtonBean;
import cn.dface.app.message.bean.MenuBean;
import cn.dface.app.message.bean.WxAddMaterialBean;
import cn.dface.app.message.bo.UserBo;
import cn.dface.app.message.cache.AccessTokenCacheFacade;
import cn.dface.app.message.constant.BaseConstant;
import cn.dface.app.message.init.UserInit;
import cn.dface.app.message.service.MenuService;
import cn.dface.app.message.service.MessageSendService;
import cn.dface.app.message.service.QrcodeService;
import cn.dface.app.message.service.UserService;
import cn.dface.app.message.utils.ImageUtils;
import cn.dface.app.message.utils.ZxingUtils;
import com.alibaba.fastjson.JSON;
import net.dongliu.requests.Requests;
import net.dongliu.requests.body.Part;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore
public class MessageApplicationTests {

    @Autowired
    private QrcodeService qrcodeService;
    @Autowired
    private UserService   userService;
    @Autowired
    private AccessTokenCacheFacade accessTokenCacheFacade;
    @Autowired
    private MenuService   menuService;
    @Autowired
    private MessageSendService  messageSendService;

    @Test
    public void contextLoads() {
    }

    @Test
    @Ignore
    public void send(){
        messageSendService.send(1);
    }

    @Test
    @Ignore
    public void addMenu(){
        MenuBean menuBean = new MenuBean();
        List<ButtonBean> button = Arrays.asList(ButtonBean.builder().name("家长登录").type("view").url("https://prod.xiaomai5.com/bind/933977623430692928/wx51c8be83fa82cf65").build());
        menuBean.setButton(button);
        menuService.addMenu(menuBean);
    }

    @Test
    @Ignore
    public void getQrcode(){

        System.out.println(qrcodeService.getQrcode(1));
    }

    @Test
    @Ignore
    public void userList(){
        userService.getUserList();
    }

    @Test
    @Ignore
    public void upload(){
        long beginTime = System.currentTimeMillis();
        File file = new File("/root/images/userAvatar2.png");
        if(!file.exists()){
            return;
        }
        List<Part<?>> parts = new ArrayList<>();
        Part<?> part = Part.file("media",file);
        part.contentType("application/octet-stream");
        parts.add(part);
        String accessToken = accessTokenCacheFacade.getAccessToken();
        String result = Requests.post("https://api.weixin.qq.com/cgi-bin/material/add_material?type=image&access_token=" + accessToken)
                .multiPartBody(parts).send().readToText();
        WxAddMaterialBean wxAddMaterial = JSON
                .parseObject(result, WxAddMaterialBean.class);
        System.out.println(wxAddMaterial);
        long endTime = System.currentTimeMillis();
        System.out.println((endTime - beginTime));

    }

    @Test
    @Ignore
    public void generateImage(){
        UserBo userBo = UserInit.userMap.get(2);
        String qrcode = this.qrcodeService.getQrcode(2);
        BufferedImage qrcodeUrlBufferedImage = ZxingUtils.generateQrcode(qrcode, BaseConstant.width,BaseConstant.height);
        BufferedImage bufferedImage = ImageUtils.generateImage(new File("/Users/akun/Downloads/WechatIMG558.jpeg"),qrcodeUrlBufferedImage,userBo.getAvatar(),userBo.getName());
        BufferedImage compressImage = ImageUtils.compress(bufferedImage);

        File file = new File("/Users/akun/images/user.png");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            ImageIO.write(compressImage,"png",file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void out(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        try {
//            ImageIO.write(bufferedImage, "png", out);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try {
            byte[] destImageData = ImageUtils.compress(out.toByteArray(),1.0);
            OutputStream fileOut = new FileOutputStream("/Users/akun/images/user.png");
            InputStream is = new ByteArrayInputStream(destImageData);
            byte[] buff = new byte[1024];
            int len = 0;
            while((len=is.read(buff))!=-1){
                fileOut.write(buff, 0, len);
            }
            fileOut.flush();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
