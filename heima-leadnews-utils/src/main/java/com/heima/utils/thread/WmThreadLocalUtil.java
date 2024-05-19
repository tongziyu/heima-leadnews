package com.heima.utils.thread;

import com.heima.model.wemedia.pojos.WmUser;

/**
 * @Description:
 * @Author: Tong Ziyu
 * @Date: 2024/5/19 20:10
 */
public class WmThreadLocalUtil {

    private static final  ThreadLocal<WmUser> WM_USER_THREAD_LOCAL = new ThreadLocal<>();

    public static WmUser getUser(){
        return WM_USER_THREAD_LOCAL.get();
    }


    public static void setUser(WmUser wmUser){
        WM_USER_THREAD_LOCAL.set(wmUser);
    }


    public static void clear(){
        WM_USER_THREAD_LOCAL.remove();
    }
}
