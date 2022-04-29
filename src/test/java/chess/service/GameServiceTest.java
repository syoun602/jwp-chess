package chess.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import chess.domain.ChessGame;
import chess.domain.piece.pawn.WhitePawn;
import chess.domain.square.Square;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@Sql("classpath:gameServiceInit.sql")
class GameServiceTest {

    @Autowired
    private GameService gameService;

    @Test
    @DisplayName("게임이 정상적으로 생성되는지 확인한다.")
    void createGame() {
        assertThat(gameService.createGame("some", "123", 1L, 2L)).isEqualTo(1L);
    }

    @Test
    @DisplayName("진행 중인 게임 수를 반환한다.")
    void findPlayingGames() {
        gameService.createGame("some", "123", 1L, 2L);
        gameService.createGame("some", "123", 1L, 2L);
        gameService.createGame("some", "123", 1L, 2L);

        assertThat(gameService.findPlayingGames().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("게임 id를 통해 게임을 불러온다.")
    void findByGameId() {
        final Long gameId = gameService.createGame("some", "123", 1L, 2L);
        final ChessGame game = gameService.findByGameId(gameId);

        assertThat(game.getId()).isEqualTo(gameId);

    }

    @Test
    @DisplayName("게임이 정상적으로 종료되는지 확인한다.")
    void terminate() {
        final Long gameId = gameService.createGame("some", "123", 1L, 2L);
        gameService.terminate(gameId);
        final ChessGame game = gameService.findByGameId(gameId);

        assertThat(game.isEnd()).isTrue();
    }

    @Test
    @DisplayName("멤버별 게임 기록을 반환한다.")
    void findHistoriesByMemberId() {
        gameService.createGame("some", "123", 1L, 2L);
        gameService.createGame("some", "123", 1L, 2L);
        gameService.terminate(1L);
        gameService.terminate(2L);

        assertThat(gameService.findHistoriesByMemberId(1L).size()).isEqualTo(2);
    }

    @Test
    @DisplayName("말이 이동되는지 확인한다.")
    void move() {
        final Long gameId = gameService.createGame("some", "123", 1L, 2L);
        gameService.move(gameId, "a2", "a4");
        final ChessGame game = gameService.findByGameId(gameId);

        assertThat(game.getBoard().getPieceAt(Square.from("a4"))).isInstanceOf(WhitePawn.class);
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않을 경우 예외를 발생시킨다.")
    void validatePassword() {
        final Long gameId = gameService.createGame("some", "123", 1L, 2L);

        assertThatThrownBy(() -> gameService.validatePassword(gameId, "12"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("끝난 체스 게임을 삭제한다.")
    void deleteGame() {
        final Long gameId = gameService.createGame("some", "123", 1L, 2L);
        final String password = "123";
        gameService.terminate(gameId);

        assertDoesNotThrow(() -> gameService.deleteGameById(gameId, password));
    }

    @Test
    @DisplayName("끝나지 않은 체스 게임을 삭제할 경우 예외를 발생한다.")
    void cannotDeleteGame() {
        final Long gameId = gameService.createGame("some", "123", 1L, 2L);
        final String password = "123";

        assertThatThrownBy(() -> gameService.deleteGameById(gameId, password))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("진행 중인 게임은 삭제할 수 없습니다.");
    }
}
