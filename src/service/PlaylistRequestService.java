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

public class PlaylistRequestService {
    private static final String PLAYLIST_API_URL = "https://youtube.googleapis.com/youtube/v3/playlistItems?part=contentDetails&maxResults=50";
    private static final String PLAYLIST_ID_PARAM = "&playlistId=";
    private static final String VIDEO_API_URL = "https://youtube.googleapis.com/youtube/v3/videos?part=contentDetails&";
    private static final String VIDEO_FIELDS_PARAM = "maxResults=300&fields=items(contentDetails(duration))";
    private static final String API_KEY = "&key=AIzaSyBpIRmo7OQtGUkb5K6FNZ2mmkOcco5JrLc";
    private static String url = "";

    public static String playlistCalculatorTest(String playlistId) {
        url = PLAYLIST_API_URL + PLAYLIST_ID_PARAM + playlistId + API_KEY;
        PlaylistContent playlistContent = getPlaylistContent(request(url));
        List<String> videosIds = getVideosIds(getFilledPlaylistItems(playlistContent, playlistId));
        List<String> idsFormatted = idsFormat(videosIds);
        VideoContent filledVideoContent = getFilledVideoContent(idsFormatted);
        List<Duration> durations = getDurationList(filledVideoContent);
        List<String> strings = durationParse(durationSum(durations));
        return getRelatorio(strings);
    }

    private static HttpResponse<String> request(String url) {
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

    private static PlaylistContent getPlaylistContent(HttpResponse<String> response) {
        Gson gson = new Gson();
        return gson.fromJson(response.body(), PlaylistContent.class);
    }

    private static VideoContent getVideoContent(HttpResponse<String> response) {
        Gson gson = new Gson();
        return gson.fromJson(response.body(), VideoContent.class);
    }

    private static PlaylistContent getFilledPlaylistItems(PlaylistContent playlistContent, String playlistId) {
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

    private static List<String> getVideosIds(PlaylistContent playlistContent) {
        List<String> videoIds = new ArrayList<>();
        List<PlaylistItem> items = playlistContent.getItems();

        for (PlaylistItem item : items) {
            videoIds.add(item.getVideoId());
        }
        return videoIds;
    }

    private static List<String> idsFormat(List<String> idsList) {
        List<String> list = new ArrayList<>();
        for (String id : idsList) {
            id = "id=" + id + "&";
            list.add(id);
        }
        return list;
    }

    private static VideoContent getFilledVideoContent(List<String> ids) {
        url = VIDEO_API_URL + ids.getFirst() + VIDEO_FIELDS_PARAM + API_KEY;
        VideoContent videoContent = getVideoContent(request(url));
        return addVideoItems(videoContent, ids);
    }

    private static VideoContent addVideoItems(VideoContent videoContent, List<String> ids) {
        int size = ids.size();

        if (size < 50) {
            appendIdsIfSizeMinorThan50(videoContent, ids, size);
        } else {
            appendIdsIfSizeGreaterThan50(videoContent, ids, size);
        }
        return videoContent;
    }

    private static void appendIdsIfSizeMinorThan50(VideoContent videoContent, List<String> ids, int size) {
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

    private static void appendIdsIfSizeGreaterThan50(VideoContent videoContent, List<String> ids, int size) {
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

    private static List<Duration> getDurationList(VideoContent videoContent) {
        List<String> durationStrings = videoContent.getDurationStrings();
        List<Duration> durationList = new ArrayList<>();

        for (String s : durationStrings) {
            Duration duration = Duration.parse(s);
            durationList.add(duration);
        }
        return durationList;
    }

    private static Duration durationSum(List<Duration> durations) {
        return durations.stream().reduce(Duration.ZERO, Duration::plus);
    }

    private static List<String> durationParse(Duration duration) {
        long seconds = duration.getSeconds();

        long d = seconds / 86400;
        seconds = seconds % 86400;

        long h = seconds / 3600;
        seconds = seconds % 3600;

        long m = seconds / 60;
        seconds = seconds % 60;

        long s = seconds;

        List<String> list = new ArrayList<>();
        String dias;
        String horas;
        String minutos;
        String segundos;

        if (d >= 1) {
            if (d == 1) {
                dias = d + " dia";
                list.add(dias);
            } else {
                dias = d + " dias";
                list.add(dias);
            }
        }

        if (h >= 1) {
            if (h == 1) {
                horas = h + " hora";
                list.add(horas);
            } else {
                horas = h + " horas";
                list.add(horas);
            }
        }

        if (m >= 1) {
            if (m == 1) {
                minutos = m + " minuto";
                list.add(minutos);
            } else {
                minutos = m + " minutos";
                list.add(minutos);
            }
        }

        if (s >= 1) {
            if (s == 1) {
                segundos = s + " segundo";
                list.add(segundos);
            } else {
                segundos = s + " segundos";
                list.add(segundos);
            }
        }

        return list;
    }

    private static String getRelatorio(List<String> list) {
        if (list.isEmpty()){
            return "ERRO!";
        }
        return String.join(", ", list);
    }
}