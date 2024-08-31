package service;

import com.google.gson.Gson;
import domain.playlist.PlaylistContent;
import domain.playlist.PlaylistItem;
import domain.video.VideoContent;
import domain.video.VideoItem;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

// 1 - Solicitar o objeto PlaylistContent
// 2 - Preencher o PlaylistContent com todos os videos da playlist
// 3 - Obter o id de todos os videos
// 4 - Para cada id, fazer um request
// 5 - Adicionar em um objeto VideoContent o 'duration' de todos os videos
// 6 - Pegar cada 'duration' e formatar para calcular sua duração
// 7 - Retornar a duração formatada da playlist

public class PlaylistRequestService {
    private static final String PLAYLIST_API_URL = "https://youtube.googleapis.com/youtube/v3/playlistItems?part=contentDetails&maxResults=50";
    private static final String PLAYLIST_ID_PARAM = "&playlistId=";
    private static final String VIDEO_API_URL = "https://youtube.googleapis.com/youtube/v3/videos?part=contentDetails&";
    private static final String VIDEO_FIELDS_PARAM = "maxResults=300&fields=items(contentDetails(duration))";
    private static final String API_KEY = "&key=YOUR_KEY_HERE";
    private static String pageToken = "";
    private static String url = "";

    public static HttpResponse<String> request(String url) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PlaylistContent getPlaylistContent(HttpResponse<String> response) {
        Gson gson = new Gson();
        return gson.fromJson(response.body(), PlaylistContent.class);
    }

    public static VideoContent getVideoContent(HttpResponse<String> response) {
        Gson gson = new Gson();
        return gson.fromJson(response.body(), VideoContent.class);
    }

    public static VideoContent playlistCalculator(String playlistId) {
        url = PLAYLIST_API_URL + pageToken + PLAYLIST_ID_PARAM + playlistId + API_KEY;
        PlaylistContent playlistContent = getPlaylistContent(request(url));
        List<String> videosIds = getVideosIds(getFilledPlaylistItems(playlistContent, playlistId));
        List<String> idsFormatted = idsFormat(videosIds);
        return getFilledVideoContent(idsFormatted);
    }

    public static void playlistCalculatorTest(String playlistId) {
        url = PLAYLIST_API_URL + pageToken + PLAYLIST_ID_PARAM + playlistId + API_KEY;
        PlaylistContent playlistContent = getPlaylistContent(request(url));
        List<String> videosIds = getVideosIds(getFilledPlaylistItems(playlistContent, playlistId));
        List<String> idsFormatted = idsFormat(videosIds);
        VideoContent filledVideoContent = getFilledVideoContent(idsFormatted);
        List<Duration> durations = durationParse(filledVideoContent);
        for (Duration d : durations){
            System.out.println(d);
        }
        System.out.println(durations.size());

        System.out.println(durations.stream().reduce(Duration.ZERO, Duration::plus));
    }

    public static PlaylistContent getFilledPlaylistItems(PlaylistContent playlistContent, String playlistId) {
        PlaylistContent plTemp = playlistContent;
        while (plTemp.getNextPageToken() != null) {
            url = PLAYLIST_API_URL + "&pageToken=" + plTemp.getNextPageToken() + "&" + PLAYLIST_ID_PARAM + playlistId + API_KEY;
            plTemp = getPlaylistContent(request(url));
            for (PlaylistItem item : plTemp.getItems()) {
                playlistContent.addItem(item);
            }
        }
        return playlistContent;
    }

    public static List<String> getVideosIds(PlaylistContent playlistContent) {
        List<String> videoIds = new ArrayList<>();
        List<PlaylistItem> items = playlistContent.getItems();

        for (PlaylistItem item : items) {
            videoIds.add(item.getVideoId());
        }
        return videoIds;
    }

    public static List<String> idsFormat(List<String> idsList) {
        List<String> list = new ArrayList<>();
        for (String id : idsList) {
            id = "id=" + id + "&";
            list.add(String.valueOf(id));
        }
        return list;
    }

    public static VideoContent getFilledVideoContent(List<String> ids) {
        url = VIDEO_API_URL + ids.getFirst() + VIDEO_FIELDS_PARAM + API_KEY;
        VideoContent videoContent = getVideoContent(request(url));
        return addVideoItems(videoContent, ids);
    }

    public static VideoContent addVideoItems(VideoContent videoContent, List<String> ids) {
        int size = ids.size();

        if (size < 50) {
            appendIdsIfSizeMinorThan50(videoContent, ids, size);
        } else {
            appendIdsIfSizeGreaterThan50(videoContent, ids, size);
        }
        return videoContent;
    }

    public static void appendIdsIfSizeMinorThan50(VideoContent videoContent, List<String> ids, int size) {
        VideoContent videoTemp;
        StringBuilder s = new StringBuilder();

        for (int i = 0; i < size; i++) {
            s.append(ids.get(i));
        }

        url = VIDEO_API_URL + s + VIDEO_FIELDS_PARAM + API_KEY;
        videoTemp = getVideoContent(request(url));

        for (VideoItem item : videoTemp.getItems()) {
            videoContent.addItem(item);
        }

        videoContent.getItems().removeFirst();
    }

    public static void appendIdsIfSizeGreaterThan50(VideoContent videoContent, List<String> ids, int size) {
        VideoContent videoTemp;
        List<String> idsParam = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        int c = 0;

        for (String s : ids) {
            sb.append(s);
            c++;
            if (c == 50) {
                idsParam.add(String.valueOf(sb));
                sb = new StringBuilder();
                c = 0;
            }
        }
        if (size % 50 > 0) {
            idsParam.add(String.valueOf(sb));
        }

        for (String id : idsParam) {
            url = VIDEO_API_URL + id + VIDEO_FIELDS_PARAM + API_KEY;
            videoTemp = getVideoContent(request(url));
            for (VideoItem item : videoTemp.getItems()) {
                videoContent.addItem(item);
            }
        }
        videoContent.getItems().removeFirst();
    }

    public static List<Duration> durationParse(VideoContent videoContent){
        List<String> durationStrings = videoContent.getDurationStrings();
        List<Duration> durationList = new ArrayList<>();

        for (String s : durationStrings){
            Duration duration = Duration.parse(s);
            durationList.add(duration);
        }
        return durationList;
    }
}