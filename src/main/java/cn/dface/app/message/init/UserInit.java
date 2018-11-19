package cn.dface.app.message.init;

import cn.dface.app.message.bo.UserBo;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author akun
 * @create 2018-10-20 上午11:58
 **/
@Component
public class UserInit {

    public static final ConcurrentHashMap<Integer,UserBo> userMap = new ConcurrentHashMap<>();

    public static final ConcurrentHashMap<String,UserBo> userOpenIdMap = new ConcurrentHashMap<>();

    @Value("${user.data.path}")
    private String userDataPath;
    @PostConstruct
    public void init(){
        InputStreamReader read = null;
        BufferedReader reader = null;
        try {
            String fileContent ="";
            File file = new File(userDataPath + "user.json");
            if(!file.exists()) {
                file.createNewFile();
            }
            read = new InputStreamReader(
                    new FileInputStream(file), "UTF-8");
            reader = new BufferedReader(read);
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent += line;
            }
            if(!StringUtils.isEmpty(fileContent)){
                List<UserBo> userBoList = JSON.parseArray(fileContent,UserBo.class);
                if(!CollectionUtils.isEmpty(userBoList)){
                    userMap.putAll(userBoList.stream().collect(Collectors.toMap(UserBo::getUserId,Function.identity())));
                    userOpenIdMap.putAll(userBoList.stream().collect(Collectors.toMap(UserBo::getOpenId,Function.identity())));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if(read != null){
                    read.close();
                }
                if(reader != null){
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
