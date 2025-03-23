# PlayList Converter Documentation

## Overview
PlayList Converter is a robust web application that enables seamless playlist conversion between Spotify and YouTube platforms. This documentation provides comprehensive information about the project setup, configuration, and usage.

## Table of Contents
1. [Architecture](#architecture)
2. [API Documentation](#api-documentation)
3. [Development Guide](#development-guide)
4. [Deployment Guide](#deployment-guide)
5. [Support](#support)

## Architecture
The application follows a Spring Boot MVC architecture:
- Frontend: Thymeleaf templates with Bootstrap
- Backend: Java Spring Boot REST APIs
- Authentication: OAuth 2.0
- API Integration: Spotify Web API and YouTube Data API v3

## API Documentation

### Spotify Integration
- **Auth Endpoint**: `/spotify_login`
- **Callback**: `/spotify_callback`
- **Playlists**: `/spotify_playlists`
- **Rate Limits**: 
  - 1000 requests/day
  - Batch processing available

### YouTube Integration
- **Auth Endpoint**: `/youtube_login`
- **Callback**: `/youtube_callback`
- **Playlists**: `/youtube_playlists`
- **Quota Limits**:
  - Daily: 10,000 units
  - Search: 100 units/query
  - Playlist creation: 50 units
  - Video addition: 50 units/video

## Development Guide

### Prerequisites
- JDK 11 or later
- Maven 3.6+
- IDE (recommended: IntelliJ IDEA or VS Code)
- Git

### Setup Instructions
1. Clone repository
2. Configure API credentials
3. Run application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Testing
Run tests with coverage:
```bash
./mvnw test jacoco:report
```

## Deployment Guide

### Production Deployment
1. Build JAR:
   ```bash
   ./mvnw clean package
   ```
2. Configure environment variables
3. Deploy using:
   ```bash
   java -jar target/playlist-converter-1.0.0.jar
   ```

### Environment Variables
- `SPOTIFY_CLIENT_ID`
- `SPOTIFY_CLIENT_SECRET`
- `YOUTUBE_CLIENT_ID`
- `YOUTUBE_CLIENT_SECRET`
- `REDIRECT_URI`

## Support
- Email: support@playlistconverter.com
- GitHub Issues: [Create Issue](https://github.com/yourusername/PlaylistConverter/issues)
- Documentation: [Wiki](https://github.com/yourusername/PlaylistConverter/wiki)
