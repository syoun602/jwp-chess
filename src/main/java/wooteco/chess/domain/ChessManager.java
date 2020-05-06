package wooteco.chess.domain;

import wooteco.chess.domain.board.Board;
import wooteco.chess.domain.piece.Team;

import java.util.Optional;

public class ChessManager {
    private Long id;
    private ChessRunner chessRunner;
    private boolean playing;

    public ChessManager(Long id, ChessRunner chessRunner) {
        this.id = id;
        this.chessRunner = chessRunner;
    }

    public ChessManager(Long id) {
        this.id = id;
    }

    public ChessManager() {
    }

    public void start() {
        this.chessRunner = new ChessRunner();
        playing = true;
    }

    public void end() {
        playing = false;
    }

    public void move(String source, String target) {
        chessRunner.updateBoard(source, target);
        playing = stopGameIfWinnerExists();
    }

    public void clearBoard() {
        chessRunner.clearBoard();
    }

    private boolean stopGameIfWinnerExists() {
        return !chessRunner.findWinner().isPresent();
    }

    public boolean isPlaying() {
        return playing;
    }

    public double calculateScore() {
        return chessRunner.calculateScore();
    }

    public Board getBoard() {
        return chessRunner.getBoard();
    }

    public Team getCurrentTeam() {
        return chessRunner.getCurrentTeam();
    }

    public Optional<Team> getWinner() {
        return chessRunner.findWinner();
    }

    public Long getId() {
        return id;
    }
}