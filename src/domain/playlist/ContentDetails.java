package domain.playlist;

public class ContentDetails {
    private String videoId;
    private String videoPublishedAt;

    @Override
    public String toString() {
        return "ContentDetails{" +
                "videoId='" + videoId + '\'' +
                ", videoPublishedAt='" + videoPublishedAt + '\'' +
                '}';
    }

    public String getVideoId() {
        return videoId;
    }

    public String getVideoPublishedAt() {
        return videoPublishedAt;
    }
}
