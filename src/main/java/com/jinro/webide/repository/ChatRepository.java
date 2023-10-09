package com.jinro.webide.repository;

import com.jinro.webide.dto.ChatDTO;
import com.jinro.webide.dto.ChatRoomDTO;
import com.jinro.webide.dto.RoomRequestDTO;
import com.jinro.webide.util.RoomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
@Slf4j
public class ChatRepository {
    private static final String CHAT_ROOM_KEY = "chat:";
    private final RedisTemplate<String, Object> redisTemplate;

    // 채팅방 메세지 조회
    public List<ChatDTO> findRoomChatting(RoomRequestDTO rrd){
        //1. 채팅방 가져오기
        ChatRoomDTO chatRoomDTO = findRoomById(rrd);

        //2. 채팅내역 반환
        if(chatRoomDTO != null) {
            log.info("채팅방 정보 : {}", chatRoomDTO);
            return chatRoomDTO.getChatDTOList();
        }
        log.info("채팅방이 없습니다. : " + rrd);
        return null;
    }

    // chat:projectId:roomId 기준으로 채팅방 찾기
    public ChatRoomDTO findRoomById(RoomRequestDTO rrd){
        String key = CHAT_ROOM_KEY + rrd.getProjectId() + ":" + rrd.getRoomId();
        HashOperations<String, String, ChatRoomDTO> hashOperations = redisTemplate.opsForHash();
        return hashOperations.get(key, rrd.getRoomId());
    }

    // roomName 로 채팅방 만들기
    public ChatRoomDTO createChatRoom(RoomRequestDTO rrd){
        //1. roomId 생성
        String roomId = RoomUtil.randomRoomId();
        //2. Redis에 채팅방 정보를 저장하는 코드 추가
        ChatRoomDTO chatRoomDTO = ChatRoomDTO.builder()
                .projectId(rrd.getProjectId())
                .roomName(rrd.getRoomName())
                .roomId(roomId)
                .build();

        String key = CHAT_ROOM_KEY + rrd.getProjectId() + ":" + roomId;
        redisTemplate.opsForHash().put(key, roomId, chatRoomDTO);

        return chatRoomDTO;
    }

    // 채팅방 유저 리스트에 유저 추가
    public String addUser(RoomRequestDTO rrd, String userName){
        ChatRoomDTO room = findRoomById(rrd);
        String userUUID = UUID.randomUUID().toString();

        if(room == null) {
            return null;
        }
        //userList 에 추가
        if(room.getUserlist() == null) {
            room.setUserlist(new HashMap<>());
        }
        room.getUserlist().put(userUUID, userName);
        room.setUserCount(room.getUserCount()+1); //채팅방 유저 +1

        // Redis에 채팅방 정보를 업데이트
        String key = CHAT_ROOM_KEY + rrd.getProjectId() + ":" + rrd.getRoomId();
        redisTemplate.opsForHash().put(key, rrd.getRoomId(), room);

        return userUUID;
    }

    // 채팅방 유저 리스트 삭제
    public void delUser(RoomRequestDTO rrd, String userUUID){
        ChatRoomDTO room = findRoomById(rrd);
        room.getUserlist().remove(userUUID);
        room.setUserCount(room.getUserCount()-1); //채팅방 인원 -1

        // Redis에 채팅방 정보를 업데이트
        String key = CHAT_ROOM_KEY + rrd.getProjectId() + ":" + rrd.getRoomId();
        redisTemplate.opsForHash().put(key, rrd.getRoomId(), room);
    }

    // 채팅방 userName 조회
    public String getUserName(RoomRequestDTO rrd, String userUUID){
        ChatRoomDTO room = findRoomById(rrd);
        return room.getUserlist().get(userUUID);
    }

    // 채팅방 전체 user 조회
    public ArrayList<String> getUserList(RoomRequestDTO rrd){
        ArrayList<String> list = new ArrayList<>();

        ChatRoomDTO room = findRoomById(rrd);
        //유저가 없을 경우
        if (room.getUserCount() == 0L) {
            log.info("해당 채팅방은 유저가 없습니다.");
            return null;
        }

        // hashmap 을 for 문을 돌린 후
        // value 값만 뽑아내서 list 에 저장 후 reutrn
        room.getUserlist().forEach((key, value) -> list.add(value));
        return list;
    }

    //메세지 저장
    public void saveMsg(RoomRequestDTO rrd, ChatDTO chatDTO) {
        ChatRoomDTO room = findRoomById(rrd);

        //처음 채팅일 경우
        if(room.getChatDTOList() == null) {
            room.setChatDTOList(new ArrayList<>());
        }
        room.getChatDTOList().add(chatDTO);

        // Redis에 채팅방 정보를 업데이트
        String key = CHAT_ROOM_KEY + rrd.getProjectId() + ":" + rrd.getRoomId();
        redisTemplate.opsForHash().put(key, rrd.getRoomId(), room);
    }
}
