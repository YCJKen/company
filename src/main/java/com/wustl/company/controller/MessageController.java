package com.wustl.company.controller;

import com.wustl.company.common.Result;
import com.wustl.company.common.Results;
import com.wustl.company.dto.MessageDTO;
import com.wustl.company.entity.Message;
import com.wustl.company.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    
    @PostMapping
    public Result<Message> sendMessage(@Valid @RequestBody MessageDTO messageDTO,
                                     HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        Message message = messageService.sendMessage(userId, messageDTO);
        return Results.success(message);
    }
    
    @GetMapping("/conversation/{friendId}")
    public Result<List<Message>> getConversation(@PathVariable Integer friendId,
                                               HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        List<Message> messages = messageService.getConversation(userId, friendId);
        messageService.markAsRead(userId, friendId);  // 标记为已读
        return Results.success(messages);
    }
    
    @GetMapping("/unread/count")
    public Result<Integer> getUnreadCount(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        int count = messageService.getUnreadCount(userId);
        return Results.success(count);
    }
} 