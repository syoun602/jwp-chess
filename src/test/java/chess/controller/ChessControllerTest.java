package chess.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import chess.dao.GameDao;
import chess.dao.MemberDao;
import chess.domain.Board;
import chess.domain.BoardInitializer;
import chess.domain.ChessGame;
import chess.domain.Member;
import chess.domain.Participant;
import chess.domain.Room;
import chess.domain.piece.detail.Team;
import chess.dto.GamePasswordDto;
import chess.dto.MoveCommandDto;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
@Sql("classpath:init.sql")
class ChessControllerTest {

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private GameDao gameDao;

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("페이지가 반환되는지 확인한다.")
    void indexPage() {
        RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("명령을 입력해 말을 움직일 수 있는지 확인한다.")
    void movePiece() {
        final Member one = new Member(1L, "one");
        final Member two = new Member(2L, "two");
        final Board board = new Board(BoardInitializer.create());
        final Participant participant = new Participant(one, two);
        final Room room = new Room("some", "123", participant);
        final ChessGame game = new ChessGame(1L, board, Team.WHITE, room);

        memberDao.save(one);
        memberDao.save(two);
        gameDao.save(game);

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new MoveCommandDto("a2", "a4"))
                .when().put("/game/" + 1L + "/move")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body(is("1"));
    }

    @Test
    @DisplayName("말이 이동할 수 없을 경우 오류코드를 반환한다.")
    void cannotMovePiece() {
        final Member one = new Member(1L, "one");
        final Member two = new Member(2L, "two");
        final Board board = new Board(BoardInitializer.create());
        final Participant participant = new Participant(one, two);
        final Room room = new Room("some", "123", participant);
        final ChessGame game = new ChessGame(1L, board, Team.WHITE, room);

        memberDao.save(one);
        memberDao.save(two);
        gameDao.save(game);

        final MoveCommandDto moveCommandDto = new MoveCommandDto("a2", "a5");

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(moveCommandDto)
                .when().put("/game/" + 1L + "/move")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("해당 위치로 이동할 수 없습니다."));
    }

    @Test
    @DisplayName("결과를 반환한다.")
    void result() {
        final Member one = new Member(1L, "one");
        final Member two = new Member(2L, "two");
        final Board board = new Board(BoardInitializer.create());
        final Participant participant = new Participant(one, two);
        final Room room = new Room("some", "123", participant);
        final ChessGame game = new ChessGame(2L, board, Team.WHITE, room);

        memberDao.save(one);
        memberDao.save(two);
        gameDao.save(game);

        RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/game/" + 1L + "/result")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("비밀번호가 일치할 경우 체스 게임을 반환한다.")
    void correctPassword() {
        final Member one = new Member(1L, "one");
        final Member two = new Member(2L, "two");
        final Board board = new Board(BoardInitializer.create());
        final Participant participant = new Participant(one, two);
        final Room room = new Room("some", "123", participant);
        final ChessGame game = new ChessGame(2L, board, Team.WHITE, room);

        memberDao.save(one);
        memberDao.save(two);
        gameDao.save(game);

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new GamePasswordDto("123"))
                .when().post("/game/" + 1L + "/password")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("끝난 체스 게임을 삭제한다.")
    void deleteGame() {
        final Member one = new Member(1L, "one");
        final Member two = new Member(2L, "two");
        final Board board = new Board(BoardInitializer.create());
        final Participant participant = new Participant(one, two);
        final Room room = new Room("some", "123", participant);
        final ChessGame game = new ChessGame(1L, board, Team.WHITE, room);
        game.terminate();

        memberDao.save(one);
        memberDao.save(two);
        gameDao.save(game);

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new GamePasswordDto("123"))
                .when().delete("/game/" + 1L)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("1"));
    }
}
