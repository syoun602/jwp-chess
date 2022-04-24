package chess.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import chess.domain.piece.Piece;

@Repository
public class PieceDao {

    private final JdbcTemplate jdbcTemplate;

    public PieceDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Long gameId, Piece piece) {
        final String sql = "insert into "
                + "Piece(square_file, square_rank, team, piece_type, game_id) "
                + "values (?, ?, ?, ?, ?);";
        jdbcTemplate.update(sql,
                String.valueOf(piece.getSquare().getFile().getValue()),
                String.valueOf(piece.getSquare().getRank().getValue()),
                piece.getTeam().name(),
                piece.getPieceType().name(),
                gameId
        );
    }
}
