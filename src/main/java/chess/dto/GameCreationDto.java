package chess.dto;

public class GameCreationDto {

    private String title;
    private String password;
    private Long whiteId;
    private Long blackId;

    public GameCreationDto() {
    }

    public GameCreationDto(final String title, final String password, final Long whiteId, final Long blackId) {
        this.title = title;
        this.password = password;
        this.whiteId = whiteId;
        this.blackId = blackId;
    }

    public String getTitle() {
        return title;
    }

    public String getPassword() {
        return password;
    }

    public Long getWhiteId() {
        return whiteId;
    }

    public Long getBlackId() {
        return blackId;
    }

    @Override
    public String toString() {
        return "GameCreationDto{" +
                "title='" + title + '\'' +
                ", password='" + password + '\'' +
                ", whiteId=" + whiteId +
                ", blackId=" + blackId +
                '}';
    }
}
