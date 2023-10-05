package com.jinro.webide.repository;

import com.jinro.webide.domain.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Project> projectRowMapper = (rs, rowNum) -> {
        Project project = new Project();

        project.setProjectId(toUUIDString(rs.getBytes("project_uuid")));
        project.setMemberId(toUUIDString(rs.getBytes("member_uuid")));
        project.setProjectName(rs.getString("project_name"));
        project.setProjectLang(rs.getString("project_lang"));
        project.setRegDate(rs.getTimestamp("reg_date").toLocalDateTime());
        project.setChgDate(rs.getTimestamp("chg_date").toLocalDateTime());

        return project;
    };

    private String toUUIDString(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong).toString();
    }

    private byte[] toBytes(String uuidString) {
        UUID uuid = UUID.fromString(uuidString);
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    @Override
    public Project save(Project project) {
        String projectId = UUID.randomUUID().toString();

        LocalDateTime regDate = LocalDateTime.now();
        LocalDateTime chgDate = LocalDateTime.now();

        jdbcTemplate.update("INSERT INTO tb_project (project_uuid, member_uuid, project_name, project_lang, reg_date, chg_date) VALUES (?, ?, ?, ?, ?, ?)",
                toBytes(projectId), toBytes(project.getMemberId()), project.getProjectName(), project.getProjectLang(), regDate, chgDate);

        project.setProjectId(projectId);
        project.setRegDate(regDate);
        project.setChgDate(chgDate);

        return project;
    }

    @Override
    public void lastUsingTimeUpdate(String projectId) {
        jdbcTemplate.update("UPDATE tb_project SET chg_date = ? WHERE project_uuid = ?",
                LocalDateTime.now(), toBytes(projectId));
    }

    @Override
    public Optional<Project> findById(String projectId) {
        List<Project> projects = jdbcTemplate.query("SELECT * FROM projects WHERE id = ?", new Object[]{projectId}, projectRowMapper);

        return projects.isEmpty() ? Optional.empty() : Optional.of(projects.get(0));
    }

    @Override
    public List<Project> findAll(String memberId) {
        return jdbcTemplate.query("SELECT * FROM tb_project WHERE member_uuid = ?", new Object[]{toBytes(memberId)}, projectRowMapper);
    }

    @Override
    public void deleteById(String projectId) {
        jdbcTemplate.update("DELETE FROM tb_project WHERE project_uuid = ?", toBytes(projectId));
    }

    @Override
    public void delete(String memberId) {
        jdbcTemplate.update("DELETE FROM tb_project WHERE member_uuid = ?", toBytes(memberId));
    }
}