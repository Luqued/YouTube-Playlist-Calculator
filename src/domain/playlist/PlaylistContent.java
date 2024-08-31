package domain.playlist;

import java.util.List;

public class PlaylistContent {
    private String nextPageToken;
    private List<PlaylistItem> items;
    private PageInfo pageInfo;

    public void addItem(PlaylistItem playlistItem){
        this.items.add(playlistItem);
    }
    @Override
    public String toString() {
        return "PlayListContent:\n" +
                "NextPageToken='" + nextPageToken + '\'' + '\n' +
                "Items=" + items + '\n' +
                "Page info : \n" +
                "TotalResults: " + pageInfo.getTotalResults() + '\n' +
                "ResultsPerPage: " + pageInfo.getResultsPerPage();
    }

    public List<PlaylistItem> getItems() {
        return this.items;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }
}