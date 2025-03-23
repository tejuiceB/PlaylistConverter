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
            String playListId = null;
            int retries = 3;
            while (playListId == null && retries > 0) {
                playListId = youtubeService.createPlaylist(playlistName);
                retries--;
                Thread.sleep(2000);
            }

            if (playListId == null) {
                throw new RuntimeException("Failed to create YouTube playlist after multiple attempts");
            }

            // Increase max tracks to 10, with intelligent quota management
            int maxTracks = Math.min(selectedTracks.size(), 10);
            int successCount = 0;
            int quotaUsed = 50; // Initial quota for playlist creation

            for (int i = 0; i < maxTracks; i++) {
                try {
                    // Check remaining quota
                    if (quotaUsed >= 9000) { // YouTube daily limit is 10,000
                        model.addAttribute("warning",
                                "Daily quota limit approaching. Processed " + successCount + " tracks.");
                        break;
                    }

                    String track = selectedTracks.get(i);
                    String artist = selectedArtists.get(i);
                    String searchQuery = String.format("%s - %s official audio", artist, track);

                    // Search costs 100 units
                    String trackId = youtubeService.getVideoID(searchQuery);
                    quotaUsed += 100;

                    if (trackId != null) {
                        // Add to playlist costs 50 units
                        youtubeService.addTrack(trackId, playListId);
                        quotaUsed += 50;
                        successCount++;
                        Thread.sleep(1500); // Reduced delay between tracks
                    }
                } catch (Exception e) {
                    System.out.println("Error processing track " + (i + 1) + ": " + e.getMessage());
                }
            }

            if (successCount == 0) {
                throw new RuntimeException("No tracks were successfully added to the playlist");
            }

            String url = "https://www.youtube.com/playlist?list=" + playListId;
            model.addAttribute("url", url);
            model.addAttribute("successCount", successCount);
            model.addAttribute("totalTracks", maxTracks);
            return "spotifyConvertResult";

        } catch (Exception e) {
            model.addAttribute("error", "Conversion failed: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/youtube_convert")
    public String convertYoutubeTracks(@RequestParam List<String> selectedTracks, @RequestParam String playlistName,
            Model model) {

        String playListId = spotifyService.createPlaylist(playlistName);

        List<String> trackIdUrls = new ArrayList<>();

        for (String track : selectedTracks) {
            String trackIdUrl = spotifyService.getTrackUrl(track);
            trackIdUrls.add(trackIdUrl);
        }

        spotifyService.addTracks(trackIdUrls, playListId);

        String url = "https://open.spotify.com/playlist/" + playListId;
        model.addAttribute("url", url);
        return "youtubeConvertResult";
    }
}
