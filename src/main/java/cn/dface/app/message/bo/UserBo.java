package cn.dface.app.message.bo;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import javax.annotation.sql.DataSourceDefinition;
import java.io.Serializable;
import java.util.List;

/**
 * @author akun
 * @create 2018-10-20 上午11:57
 **/
@Data
public class UserBo implements Comparable<UserBo> {

    private Integer userId;
    private String  openId;
    private String  name;
    private String  avatar;
    private String  mediaId;
    private Long    time;
    private List<Integer> boostUsers;

    @Override
    public int compareTo(UserBo o) {
        return getBoostUsersCount().compareTo(o.getBoostUsersCount());
    }

    private Integer getBoostUsersCount(){
        if(CollectionUtils.isEmpty(boostUsers)){
            return 0;
        }else{
            return boostUsers.size();
        }
    }
}
