package cn.dface.app.message.service.impl;

import cn.dface.app.message.bo.UserBo;
import cn.dface.app.message.init.UserInit;
import cn.dface.app.message.service.SyncUserDataService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author akun
 * @create 2018-10-24 上午11:29
 **/
@Service
public class SyncUserDataServiceImpl implements SyncUserDataService {

    @Value("${user.data.path}")
    private String userDataPath;
    @Override
    @Async
    public synchronized void syncUserData() {
        Map<Integer,UserBo> userMap = UserInit.userMap;
        if(userMap != null && !CollectionUtils.isEmpty(userMap)){
            FileOutputStream fos = null;
            try {
                File file = new File(userDataPath + "user.json");
                if(!file.exists()){
                    file.createNewFile();
                }
                fos = new FileOutputStream(file);
                List<UserBo> userBos = new ArrayList<>();
                Iterator<Integer> it = userMap.keySet().iterator();
                while(it.hasNext()){
                    Integer key = it.next();
                    userBos.add(userMap.get(key));
                }
                byte[] bytesArray = JSON.toJSONString(userBos).getBytes();
                fos.write(bytesArray);
                fos.flush();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                if(fos != null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
