package com.example.demo.controller;

import com.example.demo.service.SpotifyService;
import com.example.demo.service.YoutubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private YoutubeService youtubeService;

    @Autowired
    private SpotifyService spotifyService;

    @GetMapping("/")
    public String index() {
        return "youtubeLogin";
    }

    @GetMapping("/youtube_login")
    public String youtube_login() {
        return "redirect:" + youtubeService.getLoginUrl();
    }

    @GetMapping("/spotify_login")
    public String spotify_login() {
        return "redirect:" + spotifyService.getLoginUrl();
    }

    @GetMapping("/youtube_callback")
    public String youtube_callback(@RequestParam(required = false) String code) {
        if(code!=null) {
            youtubeService.setCode(code);
            youtubeService.exchangeCodeForToken();
        }
        return "spotifyLogin";
    }

    @GetMapping("/spotify_callback")
    public String spotify_callback(@RequestParam(required = false) String code) {
        if(code!=null) {
            spotifyService.setCode(code);
            spotifyService.exchangeCodeForToken();
            spotifyService.setCurrentUser();  
        }
        // return "redirect:http://localhost:8080/spotify_playlists";
        return "landing";
    }
}
