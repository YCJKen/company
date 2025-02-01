package com.wustl.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wustl.company.dto.MessageDTO;
import com.wustl.company.entity.Message;
import com.wustl.company.entity.User;
import com.wustl.company.mapper.MessageMapper;
import com.wustl.company.mapper.UserMapper;
import com.wustl.company.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    @Override
    public Message sendMessage(Integer senderId, MessageDTO messageDTO) {
        Message message = Message.builder()
                .senderId(senderId)
                .receiverId(messageDTO.getReceiverId())
                .content(messageDTO.getContent())
                .status(0)  // 未读
                .createdAt(LocalDateTime.now())
                .build();
        
        messageMapper.insert(message);
        
        // 重新查询以获取完整的消息信息（包括创建时间）
        message = messageMapper.selectById(message.getMessageId());
        
        // 设置发送者信息
        User sender = userMapper.selectById(senderId);
        if (sender != null) {
            sender.setPassword(null);
            message.setSender(sender);
        }
        
        return message;
    }

    @Override
    public List<Message> getConversation(Integer userId, Integer friendId) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.nested(w -> w
                .eq(Message::getSenderId, userId)
                .eq(Message::getReceiverId, friendId)
                .or()
                .eq(Message::getSenderId, friendId)
                .eq(Message::getReceiverId, userId)
        ).orderByAsc(Message::getCreatedAt);
        
        List<Message> messages = messageMapper.selectList(queryWrapper);
        
        // 填充发送者信息
        messages.forEach(message -> {
            User sender = userMapper.selectById(message.getSenderId());
            if (sender != null) {
                sender.setPassword(null);
                message.setSender(sender);
            }
        });
        
        return messages;
    }

    @Override
    public void markAsRead(Integer userId, Integer friendId) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getSenderId, friendId)
                   .eq(Message::getReceiverId, userId)
                   .eq(Message::getStatus, 0);
        
        Message update = new Message();
        update.setStatus(1);  // 已读
        
        messageMapper.update(update, queryWrapper);
    }

    @Override
    public int getUnreadCount(Integer userId) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getReceiverId, userId)
                   .eq(Message::getStatus, 0);
        
        return Math.toIntExact(messageMapper.selectCount(queryWrapper));
    }
} 