package codedragon.eblog.config;

import codedragon.eblog.common.lang.Consts;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author : Code Dragon
 * create at:  2020/7/9  15:18
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Autowired
    Consts consts;

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/avatar/**")
                .addResourceLocations("file:///" + consts.getUploadDir() + "/avatar/");
    }
}
