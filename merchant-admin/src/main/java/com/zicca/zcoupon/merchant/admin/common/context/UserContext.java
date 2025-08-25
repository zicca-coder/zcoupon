package com.zicca.zcoupon.merchant.admin.common.context;



import java.util.Optional;

/**
 * 登录用户信息存储上下文
 *
 * @author zicca
 */
public class UserContext {

    private static final ThreadLocal<UserInfoDTO> USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 设置用户至上下文
     *
     * @param user 用户详情信息
     */
    public static void setUser(UserInfoDTO user) {
        USER_THREAD_LOCAL.set(user);
    }

    /**
     * 获取上下文中用户 ID
     *
     * @return 用户 ID
     */
    public static String getUserId() {
        UserInfoDTO userInfoDTO = USER_THREAD_LOCAL.get();
        return Optional.ofNullable(userInfoDTO).map(UserInfoDTO::getUserId).orElse(null);
    }

    /**
     * 获取上下文中用户名称
     *
     * @return 用户名称
     */
    public static String getUsername() {
        UserInfoDTO userInfoDTO = USER_THREAD_LOCAL.get();
        return Optional.ofNullable(userInfoDTO).map(UserInfoDTO::getUsername).orElse(null);
    }

    /**
     * 获取上下文中用户店铺编号
     *
     * @return 用户店铺编号
     */
    public static Long getShopId() {
        UserInfoDTO userInfoDTO = USER_THREAD_LOCAL.get();
        return Optional.ofNullable(userInfoDTO).map(UserInfoDTO::getShopId).orElse(null);
    }

    /**
     * 清理用户上下文
     */
    public static void removeUser() {
        USER_THREAD_LOCAL.remove();
    }


}
