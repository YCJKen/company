package com.wustl.company.service;

import com.wustl.company.dto.MessageDTO;
import com.wustl.company.entity.Message;

import java.util.List;

public interface MessageService {
    Message sendMessage(Integer senderId, MessageDTO messageDTO);
    List<Message> getConversation(Integer userId, Integer friendId);
    void markAsRead(Integer userId, Integer friendId);
    int getUnreadCount(Integer userId);
} 