package chess.service;

import chess.dao.SpringBoardDao;
import chess.domain.Side;
import chess.domain.board.Board;
import chess.domain.piece.Piece;
import chess.domain.position.Position;
import chess.dto.PositionDTO;
import chess.dto.ResponseDTO;
import chess.dto.RoomValidateDTO;
import chess.dto.ScoreDTO;
import chess.exception.DuplicatedRoomNameException;
import chess.exception.NotExistRoomException;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpringChessService {
    private static final String SUCCEED_CODE = "SUCCEED";
    private static final String GAME_SET_CODE = "GAME_SET";
    private static final String FAIL_CODE = "FAIL";
    private static final String WHITE = "W";
    private static final String BLACK = "B";
    private static final String BLANK = ".";


    private SpringBoardDao springBoardDao;

    public SpringChessService(SpringBoardDao springBoardDao) {
        this.springBoardDao = springBoardDao;
    }

    public Map<String, String> currentBoardByRoomName(String roomName) {
        Map<Position, Piece> board = springBoardDao.initBoard(roomName).getBoard();
        Map<String, String> boardName = new LinkedHashMap<>();
        for (Position position : board.keySet()) {
            String positionName = position.positionName();
            boardName.put(positionName, pieceToName(board.get(position)));
        }
        return boardName;
    }

    public ResponseDTO move(PositionDTO positionDTO, String roomName) {
        Board board = springBoardDao.findBoard(roomName).orElseThrow(NotExistRoomException::new);
        return moveExecute(positionDTO, board, roomName);
    }

    private ResponseDTO moveExecute(PositionDTO positionDTO, Board board, String roomName) {
        board.move(Position.from(positionDTO.getFrom()), Position.from(positionDTO.getTo()), currentTurn(roomName));
        springBoardDao.updateBoard(board, currentTurn(roomName).changeTurn().name(), roomName);
        if (board.isGameSet()) {
            Side side = board.winner();
            return new ResponseDTO(GAME_SET_CODE, side.name(), "게임 종료(" + side.name() + " 승리)");
        }
        return new ResponseDTO(SUCCEED_CODE, "Succeed", currentTurn(roomName).name());
    }

    public void newBoard(String roomName) {
        springBoardDao.updateBoard(Board.getGamingBoard(), "WHITE", roomName);
    }

    public RoomValidateDTO checkDuplicatedRoom(String roomName) {
        if (springBoardDao.checkDuplicateByRoomName(roomName)) {
            throw new DuplicatedRoomNameException();
        }
        return new RoomValidateDTO(SUCCEED_CODE, "방 생성 성공!");
    }

    private String pieceToName(Piece piece) {
        if (piece.side() == Side.WHITE) {
            return WHITE + piece.getInitial().toUpperCase();
        }
        if (piece.side() == Side.BLACK) {
            return BLACK + piece.getInitial().toUpperCase();
        }
        return BLANK;
    }

    public String turnName(String roomName) {
        return currentTurn(roomName).name();
    }

    private Side currentTurn(String roomName) {
        return springBoardDao.findTurn(roomName).orElseThrow(NotExistRoomException::new);
    }

    public List<String> rooms() {
        return springBoardDao.findRooms();
    }

    public void createRoom(String roomName) {
        springBoardDao.newBoard(roomName);
    }

    public void deleteRoom(String roomName) {
        springBoardDao.deleteRoom(roomName);
    }

    public ScoreDTO score(String roomName) {
        Board board = springBoardDao.findBoard(roomName).orElseThrow(NotExistRoomException::new);
        return new ScoreDTO(String.valueOf(board.score(Side.WHITE)), String.valueOf(board.score(Side.BLACK)));
    }
}
