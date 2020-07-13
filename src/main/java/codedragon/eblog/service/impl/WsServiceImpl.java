package codedragon.eblog.service.impl;

import codedragon.eblog.entity.UserMessage;
import codedragon.eblog.service.UserMessageService;
import codedragon.eblog.service.WsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author : Code Dragon
 * create at:  2020/7/13  21:10
 */
@Service
public class WsServiceImpl implements WsService {
    @Autowired
    UserMessageService userMessageService;
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Async
    @Override
    public void sendMessCountToUser(Long toUserId) {
        //未读消息
        int count = userMessageService.count(new QueryWrapper<UserMessage>()
                .eq("to_user_id", toUserId)
                .eq("status", "0")
        );

        //websocket 通知
        simpMessagingTemplate.convertAndSendToUser(toUserId.toString(), "/messCount", count);
    }
}
