package domain.video;

public class VideoItem {
    private ContentDetails contentDetails;

    @Override
    public String toString() {
        return "VideoItem{" +
                "contentDetails=" + contentDetails +
                '}' + '\n';
    }

    public String getDuration(){
        return this.contentDetails.duration;
    }
}
