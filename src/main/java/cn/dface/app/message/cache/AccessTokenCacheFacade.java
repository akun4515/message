package cn.dface.app.message.cache;

/**
 * @author akun
 * @create 2018-10-20 下午2:07
 **/
public interface AccessTokenCacheFacade {

    /**
     * 获取token
     * @return
     */
    String getAccessToken();

    /**
     * 刷新
     * @return
     */
    String refresh();
}
