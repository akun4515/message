package cn.dface.app.message.bean;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author akun
 * @create 2018-10-23 下午2:12
 **/
public  class XMLTextMessage {

    protected String toUserName;
    protected String fromUserName;
    protected String msgType;
    protected String content;

    public XMLTextMessage(String toUserName, String fromUserName, String msgType,String content) {
        super();
        this.toUserName = toUserName;
        this.fromUserName = fromUserName;
        this.msgType = msgType;
        this.content = content;
    }

    public String toXML(){
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        sb.append("<ToUserName><![CDATA["+toUserName+"]]></ToUserName>");
        sb.append("<FromUserName><![CDATA["+fromUserName+"]]></FromUserName>");
        sb.append("<CreateTime>"+System.currentTimeMillis()/1000+"</CreateTime>");
        sb.append("<MsgType><![CDATA["+msgType+"]]></MsgType>");
        sb.append("<Content>< ![CDATA["+content+"] ]></Content>");
        sb.append("</xml>");
        return sb.toString();
    }

    public boolean outputStreamWrite(OutputStream outputStream){
        try {
            outputStream.write(toXML().getBytes("utf-8"));
            outputStream.flush();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public String getToUserName() {
        return toUserName;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public String getMsgType() {
        return msgType;
    }
}
