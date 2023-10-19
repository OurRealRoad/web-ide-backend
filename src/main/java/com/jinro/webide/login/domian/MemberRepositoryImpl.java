package com.jinro.webide.login.domian;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.nio.ByteBuffer;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository{

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Member save(Member member){
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(member.getId().getMostSignificantBits());
        byteBuffer.putLong(member.getId().getLeastSignificantBits());
        byte[] uuidBytes = byteBuffer.array();
        jdbcTemplate.update("INSERT INTO member (" +
            "member_uuid, " +
            "member_email," +
            "member_password, " +
            "member_name, " +
            "member_picture," +
            "role,  " +
            "reg_date) VALUES (?,?,?,?,?,?,?)",
            uuidBytes,
            member.getEmail(),
            member.getPassword(),
            member.getName(),
            member.getPicture(),
            "USER",
            member.getCreatedDate());
        return member;
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<Member> findMemberByName(String name) {
        return Optional.empty();
    }
}
