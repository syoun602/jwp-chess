package wooteco.chess.domain.game;

import wooteco.chess.domain.MoveParameter;
import wooteco.chess.domain.board.Board;
import wooteco.chess.domain.piece.PieceState;
import wooteco.chess.domain.piece.implementation.piece.King;
import wooteco.chess.domain.player.Team;
import wooteco.chess.domain.position.Position;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChessGame {

    private Board board;
    private Turn turn;

    private ChessGame(final Board board, final Turn turn) {
        this.board = board;
        this.turn = turn;
    }

    public static ChessGame of(final Board board, final Turn turn) {
        return new ChessGame(board, turn);
    }

    public boolean isEnd() {
        return board.isEnd();
    }

    public void move(MoveParameter moveParameter) {
        if (!isEnd()) {
            board.move(moveParameter.getSource(), moveParameter.getTarget(), turn);
            turn.switchTurn();
            return;
        }
        throw new UnsupportedOperationException("게임이 종료 되었습니다.");
    }

    public List<Position> getMovablePositions(Position source) {
        return board.getMovablePositions(source, turn);
    }

    public Map<Position, PieceState> getBoard() {
        return board.getBoard();
    }

    public double getScore() {
        return board.getScores(turn.getTurn());
    }

    public Map<Team, Double> getStatus() {
        return Arrays.stream(Team.values())
                .collect(Collectors.toMap(
                        value -> value,
                        value -> board.getScores(value)
                ));
    }

    public Team getTurn() {
        return turn.getTurn();
    }

    public Team getWinner() {
        if (isEnd()) {
            return getBoard().values().stream()
                    .filter(piece -> piece instanceof King)
                    .map(PieceState::getTeam)
                    .findFirst().get();
        }
        throw new UnsupportedOperationException("게임이 아직 종료되지 않았습니다.");
    }
}