package codedragon.eblog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @author : Code Dragon
 * create at:  2020/7/13  17:31
 */
@EnableAsync
@Configuration
@EnableWebSocketMessageBroker //摆事开启使用STOMP协议传输基于代理的消息
public class WsConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket")  //注册一个端点，websocket的访问地址
                .withSockJS(); //浏览器降级操作
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/user/", "/topic/"); //推送消息前缀
        registry.setApplicationDestinationPrefixes("/app");
    }
}
