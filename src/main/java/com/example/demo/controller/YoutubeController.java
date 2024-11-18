package com.example.demo.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.service.YoutubeService;

@Controller
public class YoutubeController {
    
    @Autowired
    YoutubeService youtubeService;

    @GetMapping("youtube_playlists")
    public String getYoutubePlaylists(Model model) {
        Map<String, Object> playlists = youtubeService.getPlayLists();
        model.addAttribute("playlists", playlists);
        return "youtube_playlists";
    }

    @PostMapping("/viewYoutubePlaylist")
    public String viewPlaylist(@RequestParam String playlistId, @RequestParam String playlistName, Model model) {
        Map<String, Object> tracks = youtubeService.getVideoLists(playlistId);
        model.addAttribute("tracks", tracks);
        model.addAttribute("playlistName", playlistName);
        return "YoutubePlaylistDetails";
    }
}
