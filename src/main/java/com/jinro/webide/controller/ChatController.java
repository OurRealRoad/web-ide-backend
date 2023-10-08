package com.jinro.webide.controller;

import com.jinro.webide.dto.ChatDTO;
import com.jinro.webide.dto.RoomRequestDTO;
import com.jinro.webide.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

    private final SimpMessageSendingOperations template;

    @Autowired ChatRepository repository;

    // MessageMapping 을 통해 webSocket 로 들어오는 메시지를 발신 처리한다.
    // 이때 클라이언트에서는 /pub/chat/message 로 요청하게 되고 이것을 controller 가 받아서 처리한다.
    // 처리가 완료되면 /sub/chat/room/roomId 로 메시지가 전송된다.
    @MessageMapping("/chat/enterUser")
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

    // 해당 유저
    @MessageMapping("/chat/sendMessage")
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

    // 유저 퇴장 시에는 EventListener 을 통해서 유저 퇴장을 확인
    @EventListener
    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
        log.info("DisConnEvent {}", event);

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // stomp 세션에 있던 uuid 와 roomId 를 확인해서 채팅방 유저 리스트와 room 에서 해당 유저를 삭제
        String userUUID = (String) headerAccessor.getSessionAttributes().get("userUUID");
        String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");
        String projectId = (String) headerAccessor.getSessionAttributes().get("projectId");

        log.info("headAccessor {}", headerAccessor);

        // 채팅방 유저 리스트에서 UUID 유저 닉네임 조회 및 리스트에서 유저 삭제
        RoomRequestDTO roomRequestDTO = RoomRequestDTO.builder()
                .projectId(projectId)
                .roomId(roomId)
                .build();

        String username = repository.getUserName(roomRequestDTO, userUUID);
        repository.delUser(roomRequestDTO, userUUID);

        if (username != null) {
            log.info("User Disconnected : " + username);

            // builder 어노테이션 활용
            ChatDTO chat = ChatDTO.builder()
                    .type(ChatDTO.MessageType.LEAVE)
                    .sender(username)
                    .message(username + " 님 퇴장!!")
                    .build();

            //메세지 저장 추가
            repository.saveMsg(roomRequestDTO, chat);
            template.convertAndSend("/sub/chat/room/" + roomId, chat);
        }
    }
}
