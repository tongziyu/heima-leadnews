package com.heima.app.gateway.filter;


import cn.hutool.core.util.StrUtil;
import com.heima.app.gateway.util.AppJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @Author: Tong Ziyu
 * @Date: 2024/5/17 02:13
 */
@Component
@Slf4j
public class AuthorizedFilter implements GlobalFilter, Ordered {


    /**
     * 全局过滤器
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        /**
         * 登录校验
         * 1. 判断该请求是否是登录请求,如果是则放行
         * 2. 通过请求头拿到token,判断token
         * 3. 如果token 为空,则401
         * 4. 解析token,如果token解析失败,则401
         * 5. 放行
         */
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 判断是否是登录请求
        if (request.getURI().getPath().contains("/login")) {
            return chain.filter(exchange);
        }

        // 拿到用户token
        String token = request.getHeaders().getFirst("token");

        if (StrUtil.isEmpty(token)){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        // 解析
        try {
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);

            int i = AppJwtUtil.verifyToken(claimsBody);
            if (i == 1 || i == 2){
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        }catch (Exception e){
            e.printStackTrace();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }


        return chain.filter(exchange);

    }

    /**
     * 值越小 优先级越高
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
