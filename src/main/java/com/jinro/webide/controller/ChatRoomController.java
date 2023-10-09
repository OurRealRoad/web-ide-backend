package com.jinro.webide.controller;

import com.jinro.webide.dto.ChatDTO;
import com.jinro.webide.dto.ChatRoomDTO;
import com.jinro.webide.dto.RoomRequestDTO;
import com.jinro.webide.repository.ChatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@RequestMapping("/api/v1")
@RestController
public class ChatRoomController {

    @Autowired
    private ChatRepository chatRepository;

    // 해당 채팅방 채팅내역 표시
    @PostMapping("/chat/chatlist")
    public ModelAndView goChatRoom(@RequestBody RoomRequestDTO roomRequest, ModelAndView mav){
        //1. 조회
        List<ChatDTO> list = chatRepository.findRoomChatting(roomRequest);

        //return
        mav.addObject("list", list);
        mav.setViewName("jsonView");
        return mav;
    }

    // 채팅방 생성
    @PostMapping("/chat/createroom")
    public ModelAndView createRoom(@RequestBody RoomRequestDTO roomRequest, ModelAndView mav) {
        //1. 채팅방 생성
        ChatRoomDTO room = chatRepository.createChatRoom(roomRequest);
        log.info("CREATE Chat Room {}", room);

        //return
        mav.addObject("roomInfo", room);
        mav.setViewName("jsonView");
        return mav;
    }

    // 채팅에 참여한 유저 리스트 반환
    @PostMapping("/chat/userlist")
    public ModelAndView userList(@RequestBody RoomRequestDTO roomRequest, ModelAndView mav) {
        //1. 조회
        ArrayList<String> userList = chatRepository.getUserList(roomRequest);

        //return
        mav.addObject("userList", userList);
        mav.setViewName("jsonView");
        return mav;
    }
}
