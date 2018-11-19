package cn.dface.app.message.controller;

import cn.dface.app.message.bo.UserBo;
import cn.dface.app.message.init.UserInit;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author akun
 * @create 2018-11-05 下午1:56
 **/
@RestController
@RequestMapping("/api/data")
public class DataController {


    @GetMapping("/list")
    public Object list(@RequestParam Integer pageNo, @RequestParam Integer pageRow, HttpServletResponse response){

        response.addHeader("Access-Control-Allow-Origin","*");
        ConcurrentHashMap<Integer,UserBo> userMap = UserInit.userMap;
        if (CollectionUtils.isEmpty(userMap)) {
            return "没有数据";
        }
        List<UserBo> userBos = new ArrayList<>();
        Iterator<Integer> it = userMap.keySet().iterator();
        while(it.hasNext()){
            Integer key = it.next();
            userBos.add(userMap.get(key));
        }
        userBos = userBos.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        Integer beginIndex = (pageNo-1)*pageRow;
        Integer endIndex = pageNo*pageRow;
        if(beginIndex > userBos.size() - 1){
            return "没有更多数据了";
        }
        if(endIndex > userBos.size()){
            endIndex = userBos.size();
        }
        return userBos.subList(beginIndex,endIndex);
    }

    @GetMapping("/total")
    public Object total(){

        return UserInit.userMap.size();
    }
}
