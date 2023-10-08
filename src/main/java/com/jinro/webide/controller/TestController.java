package com.jinro.webide.controller;

import com.jinro.webide.dto.ChatDTO;
import com.jinro.webide.dto.RoomRequestDTO;
import com.jinro.webide.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@RequiredArgsConstructor
@Controller
public class TestController {

    private final SimpMessageSendingOperations template;

    @Autowired ChatRepository repository;
    /**
     * index
     */
    @GetMapping("/testChating")
    public ModelAndView home(ModelAndView mav) {
        mav.setViewName("index");
        return mav;
    }

    // 채팅방 입장
    @MessageMapping("/chat/enterUserTest")
    public void enterUser(@Payload ChatDTO chat, SimpMessageHeaderAccessor headerAccessor) throws Exception {
        log.info("CHAT {}", chat);

        // 채팅방에 유저 추가 및 UserUUID 반환
        RoomRequestDTO roomRequestDTO = RoomRequestDTO.builder()
                .projectId(chat.getProjectId())
                .roomId(chat.getRoomId())
                .build();

        String userUUID = repository.addUser(roomRequestDTO, chat.getSender());
        if(userUUID == null) {
            throw new Exception("해당 채팅방은 없는 채팅방입니다. 다시 입력해 주세요.");
        }

        // 반환 결과를 socket session 에 userUUID 로 저장
        headerAccessor.getSessionAttributes().put("userUUID", userUUID);
        headerAccessor.getSessionAttributes().put("roomId", chat.getRoomId());
        headerAccessor.getSessionAttributes().put("projectId", chat.getProjectId());

        chat.setMessage(chat.getSender() + " 님 입장!!");

        //메세지 저장 추가
        repository.saveMsg(roomRequestDTO ,chat);
        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
    }

    // 메세지 전송
    @MessageMapping("/chat/sendMessageTest")
    public void sendMessage(@Payload ChatDTO chat) {
        log.info("CHAT {}", chat);
        String msg = chat.getSender() + "님 : " + chat.getMessage();
        chat.setMessage(msg);

        //메세지 저장 추가
        // 채팅방에 유저 추가 및 UserUUID 반환
        RoomRequestDTO roomRequestDTO = RoomRequestDTO.builder()
                .projectId(chat.getProjectId())
                .roomId(chat.getRoomId())
                .build();

        repository.saveMsg(roomRequestDTO, chat);
        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
    }
}