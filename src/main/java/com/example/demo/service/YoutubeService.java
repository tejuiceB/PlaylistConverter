package com.example.demo.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.net.http.HttpRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

@Service
public class YoutubeService {
    @Value("${youtube.client.id}")
    private String client_id;

    @Value("${youtube.redirect.uri}")
    private String redirect_uri;

    @Value("${youtube.response.type}")
    private String response_type;

    @Value("${youtube.scope}")
    private String scope;

    @Value("${youtube.client.secret}")
    private String client_secret;

    @Value("${youtube.grant.type}")
    private String grant_type;

    private String code;
    private String access_token;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLoginUrl() {
        return "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=" + URLEncoder.encode(client_id, StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(redirect_uri, StandardCharsets.UTF_8) +
                "&response_type=" + URLEncoder.encode(response_type, StandardCharsets.UTF_8) +
                "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8);
    }

    public String getTokenUrl() {
        return "https://oauth2.googleapis.com/token";
    }

    public void exchangeCodeForToken() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", client_id);
        params.add("client_secret", client_secret);
        params.add("code", code);
        params.add("grant_type", grant_type);
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

    public String createPlaylist(String name) {
        String playlistId = null;
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Add status object for public visibility
            Map<String, Object> snippet = new HashMap<>();
            snippet.put("title", name);
            snippet.put("privacy", "public");

            Map<String, Object> status = new HashMap<>();
            status.put("privacyStatus", "public");

            Map<String, Object> jsonInput = new HashMap<>();
            jsonInput.put("snippet", snippet);
            jsonInput.put("status", status);

            String requestBody = objectMapper.writeValueAsString(jsonInput);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://www.googleapis.com/youtube/v3/playlists?access_token=" + access_token
                            + "&part=id,snippet,status"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 403) {
                throw new RuntimeException("YouTube API quota exceeded. Please try again later.");
            }

            String responseBody = response.body();
            Map<String, Object> jsonResponse = objectMapper.readValue(responseBody, Map.class);
            playlistId = (String) jsonResponse.get("id");

            // Add delay to avoid quota issues
            Thread.sleep(1000);

        } catch (Exception e) {
            System.out.println("Error creating playlist: " + e.getMessage());
            throw new RuntimeException("Failed to create playlist: " + e.getMessage());
        }

        return playlistId;
    }

    public String getVideoID(String query) {
        try {
            Thread.sleep(2000); // Respect quota limits

            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = String.format("https://www.googleapis.com/youtube/v3/search" +
                    "?access_token=%s" +
                    "&part=snippet" +
                    "&maxResults=1" +
                    "&q=%s" +
                    "&type=video" +
                    "&videoCategoryId=10", // Music category
                    access_token, encodedQuery);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null && responseBody.containsKey("items")) {
                    List<Map<String, Object>> items = (List<Map<String, Object>>) responseBody.get("items");
                    if (!items.isEmpty()) {
                        Map<String, Object> firstItem = items.get(0);
                        Map<String, Object> id = (Map<String, Object>) firstItem.get("id");
                        String videoId = (String) id.get("videoId");
                        System.out.println("Found video ID: " + videoId + " for query: " + query);
                        return videoId;
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error searching video for: " + query + " - " + e.getMessage());
        }
        return null;
    }

    public void addTrack(String trackId, String playListId) {
        if (trackId == null || playListId == null) {
            return;
        }

        try {
            // Add delay between additions
            Thread.sleep(2000);

            String url = "https://www.googleapis.com/youtube/v3/playlistItems" +
                    "?part=snippet" +
                    "&access_token=" + access_token;

            Map<String, Object> snippet = new HashMap<>();
            snippet.put("playlistId", playListId);
            snippet.put("resourceId", Map.of(
                    "kind", "youtube#video",
                    "videoId", trackId));

            Map<String, Object> body = new HashMap<>();
            body.put("snippet", snippet);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            restTemplate.postForEntity(url, request, Map.class);

        } catch (Exception e) {
            System.out.println("Error adding track: " + e.getMessage());
        }
    }

    public Map<String, Object> getPlayLists() {
        try {
            Thread.sleep(1000);
            String url = "https://www.googleapis.com/youtube/v3/playlists?access_token=" + access_token
                    + "&mine=true&part=snippet&maxResults=50";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(access_token);
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

    public Map<String, Object> getVideoLists(String playlistId) {
        String url = "https://www.googleapis.com/youtube/v3/playlistItems?access_token=" + access_token + "&playlistId="
                + playlistId + "&part=snippet&maxResults=50";
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, null, Map.class);

        return response.getBody();
    }

    public void clearSession() {
        this.access_token = null;
        this.code = null;
    }
}
