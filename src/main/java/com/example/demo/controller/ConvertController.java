package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.service.SpotifyService;
import com.example.demo.service.YoutubeService;

@Controller
public class ConvertController {

    private static final int MAX_BATCH_SIZE = 10;
    private static final int QUOTA_LIMIT = 9000;
    private static final int QUOTA_SEARCH = 100;
    private static final int QUOTA_ADD = 50;
    private static final int QUOTA_CREATE = 50;

    @Autowired
    private YoutubeService youtubeService;

    @Autowired
    private SpotifyService spotifyService;

    @PostMapping("/spotify_convert")
    public String convertSpotifyTracks(@RequestParam List<String> selectedTracks,
            @RequestParam List<String> selectedArtists,
            @RequestParam String playlistName,
            Model model) {
        try {
            // Check initial quota
            int estimatedQuota = QUOTA_CREATE + (selectedTracks.size() * (QUOTA_SEARCH + QUOTA_ADD));
            if (estimatedQuota > QUOTA_LIMIT) {
                model.addAttribute("error",
                        "Daily quota limit would be exceeded. Please try with fewer tracks or wait.");
                return "error";
            }

            // Create playlist with retry
            String playListId = createPlaylistWithRetry(playlistName);
            if (playListId == null) {
                model.addAttribute("error",
                        "Failed to create YouTube playlist. Please check your quota or try again later.");
                return "error";
            }

            // Process in batches
            int totalTracks = selectedTracks.size();
            int processedTracks = 0;
            int successCount = 0;
            int quotaUsed = QUOTA_CREATE;

            while (processedTracks < totalTracks) {
                int batchSize = Math.min(MAX_BATCH_SIZE, totalTracks - processedTracks);
                int batchEnd = processedTracks + batchSize;

                // Process batch
                for (int i = processedTracks; i < batchEnd; i++) {
                    if (quotaUsed >= QUOTA_LIMIT) {
                        model.addAttribute("warning",
                                String.format("Quota limit reached. Processed %d of %d tracks.", successCount,
                                        totalTracks));
                        break;
                    }

                    String track = selectedTracks.get(i);
                    String artist = selectedArtists.get(i);
                    if (processSingleTrack(track, artist, playListId)) {
                        successCount++;
                    }
                    quotaUsed += (QUOTA_SEARCH + QUOTA_ADD);
                }

                processedTracks = batchEnd;
                Thread.sleep(2000); // Delay between batches
            }

            // Return results
            String url = "https://www.youtube.com/playlist?list=" + playListId;
            model.addAttribute("url", url);
            model.addAttribute("successCount", successCount);
            model.addAttribute("totalTracks", totalTracks);
            return "spotifyConvertResult";

        } catch (Exception e) {
            model.addAttribute("error", "Conversion failed: " + e.getMessage());
            return "error";
        }
    }

    private String createPlaylistWithRetry(String name) throws InterruptedException {
        int retries = 3;
        String playlistId = null;
        while (playlistId == null && retries > 0) {
            try {
                playlistId = youtubeService.createPlaylist(name);
            } catch (Exception e) {
                retries--;
                if (retries > 0)
                    Thread.sleep(2000);
            }
        }
        return playlistId;
    }

    private boolean processSingleTrack(String track, String artist, String playlistId) {
        try {
            String searchQuery = String.format("%s - %s official audio", artist, track);
            String trackId = youtubeService.getVideoID(searchQuery);
            if (trackId != null) {
                youtubeService.addTrack(trackId, playlistId);
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error processing track: " + track + " - " + e.getMessage());
        }
        return false;
    }

    @PostMapping("/youtube_convert")
    public String convertYoutubeTracks(@RequestParam List<String> selectedTracks, @RequestParam String playlistName,
            Model model) {
        try {
            String playListId = spotifyService.createPlaylist(playlistName);
            if (playListId == null) {
                model.addAttribute("error",
                        "Failed to create Spotify playlist. Please check your quota or try again later.");
                return "error";
            }
            List<String> trackIdUrls = new ArrayList<>();
            for (String track : selectedTracks) {
                String trackIdUrl = spotifyService.getTrackUrl(track);
                if (trackIdUrl != null) {
                    trackIdUrls.add(trackIdUrl);
                }
            }
            if (trackIdUrls.isEmpty()) {
                model.addAttribute("error", "No matching tracks found on Spotify.");
                return "error";
            }
            spotifyService.addTracks(trackIdUrls, playListId);
            String url = "https://open.spotify.com/playlist/" + playListId;
            model.addAttribute("url", url);
            return "youtubeConvertResult";
        } catch (Exception e) {
            model.addAttribute("error", "Conversion failed: " + e.getMessage());
            return "error";
        }
    }
}
