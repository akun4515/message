package cn.dface.app.message.service.impl;

import cn.dface.app.message.bean.*;
import cn.dface.app.message.bo.UserBo;
import cn.dface.app.message.cache.AccessTokenCacheFacade;
import cn.dface.app.message.constant.BaseConstant;
import cn.dface.app.message.constant.WxErrorCode;
import cn.dface.app.message.init.UserInit;
import cn.dface.app.message.service.KfService;
import cn.dface.app.message.service.SyncUserDataService;
import cn.dface.app.message.service.TemplateMessageService;
import cn.dface.app.message.service.UserService;
import cn.dface.app.message.utils.Dateutils;
import com.alibaba.fastjson.JSON;
import net.dongliu.requests.Requests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author akun
 * @create 2018-10-20 下午5:29
 **/
@Service
public class UserServiceImpl implements UserService {

    private static final String WX_USER_LIST_URL = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=";

    private static final String BOOST_CONTENT = "又有朋友为您助力成功了！\n\n再邀请【%d】位朋友，就可以免费获得价值1980元半年街舞畅跳卡了！时间不多了，把您的海报分享给更多朋友吧～";

    private static final String CANCLE_BOOST_CONTENT = "您的好友%s放弃为您助力啦！扣除1人气值。";
    @Value("${user.data.path}")
    private String userDataPath;
    @Autowired
    private TemplateMessageService templateMessageService;
    @Autowired
    private SyncUserDataService    syncUserDataService;
    @Autowired
    private KfService              kfService;

    @Override
    public synchronized Integer addUser(String openId) {

        UserBo userBo = UserInit.userOpenIdMap.get(openId);
        if (userBo == null) {
            Integer userId;
            Set<Integer> userIdSet = UserInit.userMap.keySet();
            if(CollectionUtils.isEmpty(userIdSet)){
                userId = 1;
            }else{
                userId =  userIdSet.stream().max(Integer::compareTo).get() + 1;
            }
            UserInfoVo userInfoVo = getUserByOpenId(openId);
            userBo = buildUserBo(userInfoVo);
            userBo.setUserId(userId);
            if(!StringUtils.isEmpty(userBo.getName())){
                UserInit.userMap.put(userId,userBo);
                UserInit.userOpenIdMap.put(userBo.getOpenId(),userBo);
            }
            syncUserDataService.syncUserData();
            return userId;
        }else{
            return userBo.getUserId();
        }
    }

    private UserBo buildUserBo(UserInfoVo userInfoVo){
        if (userInfoVo == null) {
            return null;
        }
        UserBo userBo = new UserBo();
        userBo.setName(userInfoVo.getNickname());
        userBo.setAvatar(userInfoVo.getHeadimgurl());
        userBo.setOpenId(userInfoVo.getOpenid());
        return userBo;
    }

    @Override
    public void boost(Integer userId, Integer boostUserId) {

        UserBo userBo = UserInit.userMap.get(boostUserId);
        UserBo userBo1 = UserInit.userMap.get(userId);
        if (userBo == null) {
            throw new RuntimeException("boost fail ,boostUserId is not exist");
        }
        List<Integer> boostUserIds = userBo.getBoostUsers();
        boolean success = false;
        if(CollectionUtils.isEmpty(boostUserIds)){
            boostUserIds = new ArrayList<>();
            boostUserIds.add(userId);
            success = true;
        }else{
            if(!boostUserIds.contains(userId)){
                boostUserIds.add(userId);
                success = true;
            }
        }
        userBo.setBoostUsers(boostUserIds);
        if(success){
            // 模版消息通知助力
            Integer count = BaseConstant.BOOST_COUNT - boostUserIds.size();
            templateMessageService.send(buildTemplateMessageBean(userBo.getOpenId(),count<=0?0:count,
                    userBo1.getName()));
            syncUserDataService.syncUserData();
        }
    }

    private TemplateMessageBean buildTemplateMessageBean(String openId,Integer count,String name){

        TemplateMessageBean templateMessageBean = new TemplateMessageBean();
        templateMessageBean.setTouser(openId);
        templateMessageBean.setTemplate_id("jaC8SSleFCeKHZLPXv5y8EN8gmxUW_1RVthkDqGCg10");
        Map<String,TemplateDataBean> data = new HashMap<>();
        data.put("first",TemplateDataBean.builder().value(String.format(BOOST_CONTENT,count)).color("#3996FB").build());
        data.put("keyword1",TemplateDataBean.builder().value(name).color("#ACACAC").build());
        data.put("keyword2",TemplateDataBean.builder().value(Dateutils.format(new Date())).color("#ACACAC").build());
        templateMessageBean.setData(data);
        return templateMessageBean;
    }

    @Override
    public void cancelBoost(String openId) {
        UserBo user = UserInit.userOpenIdMap.get(openId);
        if (user == null) {
            return;
        }
        Map<Integer,UserBo> userMap = UserInit.userMap;
        if(CollectionUtils.isEmpty(userMap)){
            return;
        }
        Set<Integer> userIdSet = userMap.keySet();
        for (Integer id : userIdSet) {
            UserBo userBo = userMap.get(id);
            List<Integer> boostUserList = userBo.getBoostUsers();
            if(!CollectionUtils.isEmpty(boostUserList) && boostUserList.contains(user.getUserId())){
                boostUserList.remove(user.getUserId());
                String content = String.format(CANCLE_BOOST_CONTENT,user.getName());
                this.kfService.sendText(userBo.getOpenId(),content);
            }
        }
        syncUserDataService.syncUserData();
    }

    @Autowired
    private AccessTokenCacheFacade  accessTokenCacheFacade;
    @Override
    public List<UserInfoVo> getUserList() {

        return get(null);
    }

    @Override
    public UserInfoVo getUserByOpenId(String openId) {
        return getUserInfo(openId);
    }

    private List<UserInfoVo> get(String nextOpenId){
        String accessToken = accessTokenCacheFacade.getAccessToken();
        String getUsersUrl = WX_USER_LIST_URL + accessToken;
        if (!StringUtils.isEmpty(nextOpenId)) {
            getUsersUrl = WX_USER_LIST_URL + accessToken + "&next_openid=" + nextOpenId;
        }
        List<UserInfoVo> userInfoVoList = new ArrayList<>();
        String getUserResult = Requests.get(getUsersUrl).send().readToText();
        UserVo userVo = JSON.parseObject(getUserResult,UserVo.class);
        OpenIdVo openIdVo = userVo.getData();
        if(openIdVo == null){
            return userInfoVoList;
        }
        List<String> openIdList = openIdVo.getOpenid();

        for (String openId : openIdList) {
            String getUserInfoUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";
            String userInfoResult = Requests.get(String.format(getUserInfoUrl,accessToken,openId)).send().readToText();
            UserInfoVo userInfoVo = JSON.parseObject(userInfoResult,UserInfoVo.class);
            userInfoVoList.add(userInfoVo);
            System.out.println(userInfoVo.toString());
        }
        String openId = userVo.getNext_openid();
        if(!StringUtils.isEmpty(openId)){
            get(openId);
        }
        return userInfoVoList;
    }

    private UserInfoVo getUserInfo(String openId){

        String accessToken = accessTokenCacheFacade.getAccessToken();
        String getUserInfoUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";
        String userInfoResult = Requests.get(String.format(getUserInfoUrl,accessToken,openId)).send().readToText();
        UserInfoVo userInfoVo = JSON.parseObject(userInfoResult,UserInfoVo.class);
        if(userInfoVo.getErrcode() != null && userInfoVo.getErrcode() == WxErrorCode.ACCESS_TOKEN_ERROR){
            this.accessTokenCacheFacade.refresh();
            getUserInfo(openId);
        }
        return userInfoVo;
    }

}
