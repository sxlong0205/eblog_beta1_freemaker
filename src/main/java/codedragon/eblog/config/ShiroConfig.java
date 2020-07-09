package codedragon.eblog.config;

import cn.hutool.core.map.MapUtil;
import codedragon.eblog.shiro.AccountRealm;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author : Code Dragon
 * create at:  2020/7/8  22:18
 */
@Slf4j
@Configuration
public class ShiroConfig {
    //配置安全中心
    @Bean
    public SecurityManager securityManager(AccountRealm accountRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(accountRealm);
        log.info("---------------->securityManager注入成功");
        return securityManager;
    }

    //配置拦截器
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();
        filterFactoryBean.setSecurityManager(securityManager);
        // 配置登录的url和登录成功的url
        filterFactoryBean.setLoginUrl("/login");
        filterFactoryBean.setSuccessUrl("/user/center");
        // 配置未授权跳转页面
        filterFactoryBean.setUnauthorizedUrl("/error/403");

        Map<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put("/user/home", "authc");
        hashMap.put("/user/upload", "authc");
        hashMap.put("/user/message", "authc");
        hashMap.put("/user/set", "authc");
        hashMap.put("/login", "anon");
        filterFactoryBean.setFilterChainDefinitionMap(hashMap);

        return filterFactoryBean;
    }
}













