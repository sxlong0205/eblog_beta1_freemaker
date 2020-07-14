package codedragon.eblog.search.mq;

import codedragon.eblog.config.RabbitConfig;
import codedragon.eblog.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : Code Dragon
 * create at:  2020/7/14  10:26
 */
@Slf4j
@Component
@RabbitListener(queues = RabbitConfig.ES_QUEUE)
public class MqMessageHandler {
    @Autowired
    SearchService searchService;

    @RabbitHandler
    public void handler(PostMqIndexMessage message) {
        switch (message.getType()) {
            case PostMqIndexMessage.CREATE_OR_UPDATE:
                searchService.createOrUpdateIndex(message);
                break;
            case PostMqIndexMessage.REMOVE:
                searchService.removeIndex(message);
                break;
            default:
                log.error("没找到对应的消息类型，请注意！！！ ---> {}", message.toString());
                break;
        }
    }
}
