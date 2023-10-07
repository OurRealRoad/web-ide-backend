package com.jinro.webide.controller;

import com.jinro.webide.dto.ChatDTO;
import com.jinro.webide.dto.ChatRoom;
import com.jinro.webide.dto.RoomRequest;
import com.jinro.webide.repository.ChatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
public class ChatRoomController {

    @Autowired
    private ChatRepository chatRepository;

    // 해당 채팅방 채팅내역 표시
    @PostMapping("/api/v1/chat/chatlist")
    public ModelAndView goChatRoom(@RequestBody RoomRequest roomRequest, ModelAndView mav){
        //1. 조회
        List<ChatDTO> list = chatRepository.findRoomChatting(roomRequest.getRoomId());

        //return
        mav.addObject("list", list);
        mav.setViewName("jsonView");
        return mav;
    }

    // 채팅방 생성
    @PostMapping("/api/v1/chat/createroom")
    public ModelAndView createRoom(@RequestBody RoomRequest roomRequest, ModelAndView mav) {
// 추후 ide와 결합시 사용
//  public ModelAndView createRoom(@RequestBody String name, ModelAndView mav, WebSocketSession session) {

//      //1. 세션에서 projectId 가져오기
//      String projectId = session.getAttributes().get("projectId").toString();

        //2. 채팅방 생성
        ChatRoom room = chatRepository.createChatRoom(roomRequest.getRoomName());
        log.info("CREATE Chat Room {}", room);

        //return
        mav.addObject("roomId", room.getRoomId());
        mav.setViewName("jsonView");
        return mav;
    }

    // 채팅에 참여한 유저 리스트 반환
    @PostMapping("/api/v1/chat/userlist")
    public ModelAndView userList(@RequestBody RoomRequest roomRequest, ModelAndView mav) {
        //1. 조회
        ArrayList<String> userList = chatRepository.getUserList(roomRequest.getRoomId());

        //return
        mav.addObject("userList", userList);
        mav.setViewName("jsonView");
        return mav;
    }
}
