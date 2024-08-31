package test;

import service.PlaylistRequestService;

import java.io.IOException;

public class PlaylistRequestTest01 {
    public static void main(String[] args) throws IOException, InterruptedException {
        // 1619 videos = "PLAQ7nLSEnhWTEihjeM1I-ToPDJEKfZHZu"
        // 778 videos = "PLW-S5oymMexXTgRyT3BWVt_y608nt85Uj"
        // 380 videos = "PLplXQ2cg9B_pGKqHU8ixK1aSQMoNJkS3K"
        // 160 videos = PL62G310vn6nHAeLcycI39g3vHwbZ3fCKl
        // 35 videos = "PLkqz3S84Tw-SWvRC3wbtrtIoczzX3TpdK"
        // 16 videos = "PL0ACAD8FCBB343FE3"

        long inicio = System.currentTimeMillis();

        String playlistId = "PLkqz3S84Tw-SWvRC3wbtrtIoczzX3TpdK";

        PlaylistRequestService.playlistCalculatorTest(playlistId);

        long fim = System.currentTimeMillis();

        System.out.println("Tempo de execução: " + (fim - inicio) + " ms");
    }
}