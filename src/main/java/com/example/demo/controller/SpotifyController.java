package com.example.demo.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.service.SpotifyService;

@Controller
public class SpotifyController {
    
    @Autowired
    private SpotifyService spotifyService;

    @GetMapping("/spotify_playlists")
    public String getPlaylists(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset, Model model) {
        Map<String, Object> playlists = spotifyService.getPlaylists(limit, offset);

        model.addAttribute("playlists", playlists);

        String username = spotifyService.getDisplay_name();
        model.addAttribute("username", username);

        String image_url = spotifyService.getImage_url();
        model.addAttribute("image_url", image_url);

        return "spotify_playlists";
    }
    
    @PostMapping("/viewSpotifyPlaylist")
    public String viewPlaylist(@RequestParam String playlistId, @RequestParam String playlistName, Model model) {
        Map<String, Object> tracks = spotifyService.getTrackList(playlistId);
        model.addAttribute("tracks", tracks);
        model.addAttribute("playlistName", playlistName);
        return "spotifyPlaylistDetails";
    }
}
