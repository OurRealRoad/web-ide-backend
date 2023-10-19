package com.jinro.webide.login.domian;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository{

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Member> memberRowMapper = new RowMapper<>() {
        @Override
        public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
            Member member = new Member();
            String uuidString = rs.getString("member_uuid");
            member.setId(UUID.fromString(uuidString));
            member.setEmail(rs.getString("member_email"));
            member.setName(rs.getString("member_name"));
            member.setPicture(rs.getString("member_picture"));
            member.setRole(rs.getObject("role", Role.class));
            member.setCreatedDate(rs.getString("reg_date"));
            member.setUpdatedDate(rs.getString("chg_date"));

            return member;
        }
    };

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
        List<Member> optionalMember = jdbcTemplate.query("SELECT * from member where member_email = ?",
                new Object[]{email}, memberRowMapper);
        return optionalMember.isEmpty() ? Optional.empty() : Optional.of(optionalMember.get(0));
    }

    @Override
    public Optional<Member> findMemberByName(String name) {
        List<Member> optionalMember = jdbcTemplate.query("SELECT * from member where member_name = ?",
                new Object[]{name}, memberRowMapper);
        return optionalMember.isEmpty() ? Optional.empty() : Optional.of(optionalMember.get(0));
    }
}
