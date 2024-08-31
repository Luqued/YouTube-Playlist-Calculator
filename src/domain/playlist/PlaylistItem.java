package domain.playlist;

public class PlaylistItem {
    private ContentDetails contentDetails;

    @Override
    public String toString() {
        return "Items{" +
                "contentDetails = " + "VideoId: " + contentDetails.getVideoId() + ", VideoPublishedAt: " +contentDetails.getVideoPublishedAt() +
                '}'+ '\n';
    }

    public String getVideoId(){
        return contentDetails.getVideoId();
    }
}