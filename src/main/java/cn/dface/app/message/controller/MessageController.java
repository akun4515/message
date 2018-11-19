package cn.dface.app.message.controller;

import cn.dface.app.message.service.MessageSendService;
import cn.dface.app.message.service.UserService;
import com.alibaba.fastjson.JSON;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author akun
 * @create 2018-10-18 下午5:26
 **/
@RestController
@RequestMapping("/api/message")
public class MessageController {

    private final String TOKEN="akun4515";
    @Autowired
    private UserService   userService;
    @Autowired
    private MessageSendService  messageSendService;
    @RequestMapping("/receive")
    public Object receive(HttpServletRequest request, HttpServletResponse response)throws Exception{

        ServletInputStream inputStream = request.getInputStream();
        ServletOutputStream outputStream = response.getOutputStream();
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");

        //首次请求申请验证,返回echostr
        if(echostr!=null){
            outputStreamWrite(outputStream,echostr);
            return null;
        }
        //验证请求签名
//        if(!signature.equals(SignatureUtil.generateEventMessageSignature(TOKEN,timestamp,nonce))){
//            System.out.println("The request signature is invalid");
//            return;
//        }
        System.out.println("接收到了消息");
        Map<String, String> wxdata=parseXml(request);

        if(wxdata.get("MsgType")!=null){
            if("event".equals(wxdata.get("MsgType"))){
                System.out.println("接收到了事件通知" + JSON.toJSONString(wxdata));
                if("subscribe".equals(wxdata.get("Event"))){
                    String scene_id = wxdata.get("EventKey");
                    if(!StringUtils.isEmpty(scene_id)){
                        Integer boostUserId = Integer.parseInt(scene_id.substring(scene_id.indexOf("_") + 1));
                        String fromUserOpenId = wxdata.get("FromUserName");
                        Integer userId = userService.addUser(fromUserOpenId);
                        if(!boostUserId.equals(userId)){
                            //扫他人的海报助力
                            this.userService.boost(userId,boostUserId);
                        }
                        messageSendService.send(userId);
                    }
                }
                if("SCAN".equals(wxdata.get("Event"))){
                    String fromUserOpenId = wxdata.get("FromUserName");
                    Integer userId = userService.addUser(fromUserOpenId);
                    messageSendService.send(userId);
                }
                if("unsubscribe".equals(wxdata.get("Event"))){
                    String fromUserOpenId = wxdata.get("FromUserName");
                    this.userService.cancelBoost(fromUserOpenId);
                }

            }
        }
        return null;
    }

    /**
     * 数据流输出
     * @param outputStream
     * @param text
     * @return
     */
    private boolean outputStreamWrite(OutputStream outputStream, String text){
        try {
            outputStream.write(text.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 回复微信服务器"文本消息"
     * @param response
     * @param returnvaleue
     */
    public void output(HttpServletResponse response, String returnvaleue) {
        try {
            PrintWriter pw = response.getWriter();
            pw.write(returnvaleue);
            System.out.println(returnvaleue);
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * dom4j 解析 xml 转换为 map
     * @param request
     * @return
     * @throws Exception
     */
    public static Map<String, String> parseXml(HttpServletRequest request) throws Exception {
        // 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<String, String>();
        // 从request中取得输入流
        InputStream inputStream = request.getInputStream();
        // 读取输入流
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        // 得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        List<Element> elementList = root.elements();

        // 遍历所有子节点
        for (Element e : elementList) {
            map.put(e.getName(), e.getText());
        }
        // 释放资源
        inputStream.close();
        return map;
    }

}
