package com.example.demo.controller;

import com.example.demo.service.SpotifyService;
import com.example.demo.service.YoutubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private YoutubeService youtubeService;

    @Autowired
    private SpotifyService spotifyService;

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        boolean youtubeAuth = session.getAttribute("youtube_authenticated") != null;
        boolean spotifyAuth = session.getAttribute("spotify_authenticated") != null;

        model.addAttribute("youtubeAuthenticated", youtubeAuth);
        model.addAttribute("spotifyAuthenticated", spotifyAuth);

        if (spotifyAuth) {
            model.addAttribute("spotifyProfileImage", spotifyService.getImage_url());
            model.addAttribute("spotifyUsername", spotifyService.getDisplay_name());
        }

        return "landing";
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
    public String youtube_callback(@RequestParam(required = false) String code, HttpSession session) {
        if (code != null) {
            youtubeService.setCode(code);
            youtubeService.exchangeCodeForToken();
            // Get YouTube username from the service
            Map<String, Object> userInfo = youtubeService.getUserInfo();
            session.setAttribute("youtube_username", userInfo.get("name"));
            session.setAttribute("youtube_authenticated", true);
            return "redirect:/"; // Redirect back to landing
        }
        return "redirect:/";
    }

    @GetMapping("/spotify_callback")
    public String spotify_callback(@RequestParam(required = false) String code, HttpSession session) {
        if (code != null) {
            spotifyService.setCode(code);
            spotifyService.exchangeCodeForToken();
            spotifyService.setCurrentUser();
            session.setAttribute("spotify_authenticated", true);
            return "redirect:/"; // Redirect back to landing
        }
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(@RequestParam String service, HttpSession session) {
        if ("spotify".equals(service)) {
            session.removeAttribute("spotify_authenticated");
        } else if ("youtube".equals(service)) {
            session.removeAttribute("youtube_authenticated");
        } else if ("all".equals(service)) {
            session.invalidate();
        }
        return "redirect:/";
    }

    @GetMapping("/check-auth")
    @ResponseBody
    public Map<String, Boolean> checkAuth(HttpSession session) {
        Map<String, Boolean> auth = new HashMap<>();
        auth.put("spotify", session.getAttribute("spotify_authenticated") != null);
        auth.put("youtube", session.getAttribute("youtube_authenticated") != null);
        return auth;
    }
}
