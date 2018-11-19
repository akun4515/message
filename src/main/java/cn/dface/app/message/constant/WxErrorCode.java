package cn.dface.app.message.constant;

/**
 * 微信返回错误吗
 * @author akun
 * @create 2018-01-24 下午1:29
 **/
public class WxErrorCode {
    /**
     * 成功
     */
    public static final int SUCCESS = 0;
    /**
     * 模版ID错误
     */
    public static final int TEMPLATE_ID_ERROR = 40037;

    /**
     * form_id不正确，或者过期
     */
    public static final int FORM_ID_ERROR_OR_OUT_DATE = 41028;

    /**
     * form_id已经被使用
     */
    public static final int FORM_ID_USED = 41029;

    /**
     * page不正确
     */
    public static final int PAGE_ERROR = 41030;

    /**
     * 接口调用超过限额（目前默认每个帐号日调用限额为100万）
     */
    public static final int COUNT_LIMIT = 45009;

    /**
     * access_token错误或者失效
     */
    public static final int ACCESS_TOKEN_ERROR = 42001;
    /**
     * access_token错误或者失效1
     */
    public static final int ACCESS_TOKEN_ERROR1 = 40001;
}
