package com.heima.wemedia.interceptor;

import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.thread.WmThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description:
 *  此拦截器作用:
 *    - 在微服务架构里面,一个请求通过网关被转发到各个微服务
 *    - 用户通过网关后,网关的全局过滤器,会将用户的id设置到下游的请求头里面
 *    - 下游的微服务,通过一个全局的拦截器,从header中获取用户id,保存进ThreadLocal,方便使用
 *
 * @Author: Tong Ziyu
 * @Date: 2024/5/19 19:41
 */
@Slf4j
public class WmTokenInterceptor implements HandlerInterceptor {

    /**
     * 前置处理器,将用户的id保存到ThreadLocal中
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 从请求头中获取到用户的信息,存储到ThreadLocal中

        String userId = request.getHeader("userId");

        log.info("用户信息{}",userId);

        // 将用户信息保存到ThreadLocal中
        WmUser wmUser = new WmUser();

        wmUser.setId(Integer.valueOf(userId));

        WmThreadLocalUtil.setUser(wmUser);


        return true;
    }

    /**
     * 在最后执行,用来清理资源
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);

        WmThreadLocalUtil.clear();

    }
}
