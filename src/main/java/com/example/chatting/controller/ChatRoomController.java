package com.example.chatting.controller;

import com.example.chatting.dto.ChatRoom;
import com.example.chatting.repository.ChatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Slf4j
@RestController
public class ChatRoomController {

    @Autowired
    private ChatRepository chatRepository;

    // 채팅 리스트 화면
    // 요청이 들어오면 전체 채팅룸 리스트를 담아서 return
    @GetMapping("/chat/chatlist")
    public ModelAndView goChatRoom(ModelAndView mav){
        mav.addObject("list", chatRepository.findAllRoom());
        mav.setViewName("jsonView");
        return mav;
    }

    // 채팅방 생성
    // 채팅방 생성 후 다시 / 로 return
    @PostMapping("/chat/createroom")
    public String createRoom(@RequestParam String name, RedirectAttributes rttr) {
        ChatRoom room = chatRepository.createChatRoom(name);
        log.info("CREATE Chat Room {}", room);
        rttr.addFlashAttribute("roomId", room);
        return name;
    }

    // 채팅방 입장 화면
    // 파라미터로 넘어오는 roomId 를 확인후 해당 roomId 를 기준으로
    // 채팅방을 찾아서 클라이언트를 chatroom 으로 보낸다.
    @GetMapping("/chat/room")
    public ModelAndView roomDetail(@RequestParam String roomId, ModelAndView mav){
        log.info("roomId {}", roomId);
        mav.addObject("room", chatRepository.findRoomById(roomId));
        mav.setViewName("jsonView");
        return mav;
    }
}
