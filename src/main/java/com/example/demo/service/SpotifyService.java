package com.example.demo.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SpotifyService {

    @Value("${spotify.client.id}")
    private String client_id;

    @Value("${spotify.client.secret}")
    private String client_secret;

    @Value("${spotify.redirect.uri}")
    private String redirect_uri;

    @Value("${spotify.scope}")
    private String scope;

    private String response_type = "code";
    private String code;
    private String access_token;
    private String display_name;
    private String id;
    private String image_url;
    private String email; // Add this field

    public String getImage_url() {
        return image_url;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getLoginUrl() {
        return "https://accounts.spotify.com/authorize?" +
                "client_id=" + URLEncoder.encode(client_id, StandardCharsets.UTF_8) +
                "&response_type=" + URLEncoder.encode(response_type, StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(redirect_uri, StandardCharsets.UTF_8) +
                "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8);
    }

    String getTokenUrl() {
        return "https://accounts.spotify.com/api/token";
    }

    public void exchangeCodeForToken() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String auth = client_id + ":" + client_secret;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
        headers.set("Authorization", authHeader);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("redirect_uri", redirect_uri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(getTokenUrl(), request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                setAccess_token((String) responseBody.get("access_token"));
            }
        } else {
            System.out.println("Error: " + response.getStatusCode());
        }
    }

    public Map<String, Object> getPlaylists(int limit, int offset) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(access_token);

            String url = "https://api.spotify.com/v1/me/playlists?limit=" + limit + "&offset=" + offset;
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            return response.getBody();
        } catch (HttpClientErrorException.Unauthorized e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Session expired. Please login again.");
            return errorResponse;
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get playlists: " + e.getMessage());
            return errorResponse;
        }
    }

    public Map<String, Object> getTrackList(String playlistId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(access_token);

        String url = "https://api.spotify.com/v1/playlists/" + playlistId
                + "?fields=tracks(items(track(name,artists)))";

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            System.out.println("Error: " + response.getStatusCode());
            return null;
        }
    }

    public void setCurrentUser() {
        String url = "https://api.spotify.com/v1/me";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(access_token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        Map<String, Object> user = response.getBody();

        display_name = (String) user.get("display_name");
        id = (String) user.get("id");
        email = (String) user.get("email"); // Add this line

        List images = (List) user.get("images");
        Map<String, Object> image = (Map<String, Object>) images.get(0);
        image_url = (String) image.get("url");
    }

    public String getDisplay_name() {
        return display_name;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String createPlaylist(String playlistName) {
        String playlistId = null;

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> body = new HashMap<>();
        body.put("name", playlistName);
        body.put("public", false);

        try {
            String requestBody = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.spotify.com/v1/users/" + id + "/playlists"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + access_token)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            Map<String, Object> jsonResponse = objectMapper.readValue(responseBody, Map.class);
            playlistId = (String) jsonResponse.get("id");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return playlistId;
    }

    public String getTrackUrl(String query) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(access_token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = "https://api.spotify.com/v1/search?q=" + encodedQuery + "&type=track&limit=1";

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> result = response.getBody();

            if (result != null && result.containsKey("tracks")) {
                Map<String, Object> tracks = (Map<String, Object>) result.get("tracks");
                List<Map<String, Object>> items = (List<Map<String, Object>>) tracks.get("items");

                if (items != null && !items.isEmpty()) {
                    return (String) items.get(0).get("uri");
                }
            }

            // Add delay to avoid rate limiting
            Thread.sleep(1000);

        } catch (Exception e) {
            System.out.println("Error searching track: " + e.getMessage());
        }
        return null;
    }

    public void addTracks(List<String> trackIdUrls, String playListId) {
        if (trackIdUrls.isEmpty() || playListId == null) {
            return;
        }

        try {
            // Process tracks in smaller batches to avoid issues
            int batchSize = 50;
            for (int i = 0; i < trackIdUrls.size(); i += batchSize) {
                List<String> batch = trackIdUrls.subList(i, Math.min(i + batchSize, trackIdUrls.size()));

                String url = "https://api.spotify.com/v1/playlists/" + playListId + "/tracks";
                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper objectMapper = new ObjectMapper();

                Map<String, Object> body = new HashMap<>();
                body.put("uris", batch);

                try {
                    String requestBody = objectMapper.writeValueAsString(body);

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(new URI(url))
                            .header("Content-Type", "application/json")
                            .header("Authorization", "Bearer " + access_token)
                            .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                // Add delay between batches
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.out.println("Error adding tracks: " + e.getMessage());
        }
    }

    public void clearSession() {
        this.access_token = null;
        this.display_name = null;
        this.image_url = null;
        this.id = null;
    }
}
