package chess.dao;

import chess.domain.Member;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class ChessMemberDao implements MemberDao {

    private final JdbcTemplate jdbcTemplate;

    public ChessMemberDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Member save(Member member) {
        final String sql = "insert into Member (name) values (?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, member.getName());
            return ps;
        }, keyHolder);

        final long memberId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return new Member(memberId, member.getName());
    }

    @Override
    public Optional<Member> findById(Long id) {
        final String sql = "select id, name from Member where id = ?";
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(sql,
                        (resultSet, rowNum) -> new Member(
                                resultSet.getLong("id"),
                                resultSet.getString("name")
                        ), id)
        );
    }

    @Override
    public List<Member> findAll() {
        final String sql = "select id, name from Member";
        return jdbcTemplate.query(
                sql, (resultSet, rowNum) -> new Member(
                        resultSet.getLong("id"),
                        resultSet.getString("name")
                ));
    }

    @Override
    public void deleteById(Long id) {
        final String sql = "delete from Member where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
