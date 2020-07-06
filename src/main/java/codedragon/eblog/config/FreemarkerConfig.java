package codedragon.eblog.config;


import codedragon.eblog.template.HotsTemplate;
import codedragon.eblog.template.PostTemplate;
import codedragon.eblog.template.TimeAgoMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class FreemarkerConfig {

    @Autowired
    private freemarker.template.Configuration configuration;
    @Autowired
    PostTemplate postTemplate;
    @Autowired
    HotsTemplate hotsTemplate;

    @PostConstruct
    public void setUp() {
        configuration.setSharedVariable("timeAgo", new TimeAgoMethod());
        configuration.setSharedVariable("posts", postTemplate);
        configuration.setSharedVariable("hots", hotsTemplate);
    }
}