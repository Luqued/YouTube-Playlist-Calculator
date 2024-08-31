package domain.video;

import java.util.ArrayList;
import java.util.List;

public class VideoContent {
    private List<VideoItem> items;

    @Override
    public String toString() {
        return "VideoContent{" +
                "items=" + items +
                '}';
    }
    public  List<String> getDurationStrings(){
        List<String> durationStrings = new ArrayList<>();

        for (VideoItem  v : items){
            durationStrings.add(v.getDuration());
        }

        return durationStrings;
    }

    public void addItem(VideoItem item){
        this.items.add(item);
    }

    public List<VideoItem> getItems(){
        return items;
    }
}