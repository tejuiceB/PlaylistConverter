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
    public String convertSpotifyTracks(@RequestParam List<String> selectedTracks, @RequestParam List<String> selectedArtists, @RequestParam String playlistName, Model model) {
        
        String playListId = youtubeService.createPlaylist(playlistName);

        for (int i = 0; i < selectedTracks.size(); i++) {
            String track = selectedTracks.get(i);
            String artist = selectedArtists.get(i);
            String trackId = youtubeService.getVideoID(artist + " " +track);
            youtubeService.addTrack(trackId, playListId);
        }

        String url = "https://www.youtube.com/playlist?list=" + playListId;
        model.addAttribute("url", url);
        return "spotifyConvertResult";
    }

    @PostMapping("/youtube_convert")
    public String convertYoutubeTracks(@RequestParam List<String> selectedTracks, @RequestParam String playlistName, Model model) {
        
        String playListId = spotifyService.createPlaylist(playlistName);

        List<String> trackIdUrls = new ArrayList<>(); 

        for(String track : selectedTracks) {
            String trackIdUrl = spotifyService.getTrackUrl(track);
            trackIdUrls.add(trackIdUrl);
        }

        spotifyService.addTracks(trackIdUrls, playListId);

        String url = "https://open.spotify.com/playlist/" + playListId;
        model.addAttribute("url", url);
        return "youtubeConvertResult";
    }
}
