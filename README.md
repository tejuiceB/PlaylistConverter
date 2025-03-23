# Playlist Transfer

**Playlist Transfer** is a web application that allows users to seamlessly transfer their playlists between Spotify and YouTube. With just a few clicks, you can move your favorite tracks from one platform to the other, ensuring you have access to your music wherever you go.

## Features

- **OAuth Authentication**: Securely log in to your Spotify and YouTube accounts using OAuth.
- **Playlist Transfer**: Easily transfer playlists from Spotify to YouTube or from YouTube to Spotify.
- **Seamless Integration**: Uses the Spotify Web API and YouTube Data API v3 to fetch and manage playlists.
- **User-Friendly Interface**: A simple and intuitive interface built with Thymeleaf.

## Technologies Used

- **Java**: The core programming language used for backend logic.
- **Spring Boot**: Framework for building the backend services.
- **Thymeleaf**: Used for server-side rendering of the frontend.
- **Spotify Web API**: Fetches and manages Spotify playlists.
- **YouTube Data API v3**: Fetches and manages YouTube playlists.
- **OAuth**: Secure authentication for accessing user accounts.

## YouTube API Quota Information
- Create playlist: 50 units
- Search for video: 100 units per search
- Add video to playlist: 50 units per addition
- Daily total limit: 10,000 units

For a 10-track playlist conversion:
- Playlist creation: 50 units
- 10 searches: 1000 units
- 10 additions: 500 units
- Total: ~1550 units per conversion

This allows approximately 6-7 playlist conversions per day while staying within quota limits.

## Setup Instructions

1. Clone the repository:
```bash
git clone https://github.com/yourusername/PlaylistConverter.git
cd PlaylistConverter
```

2. Configure API Credentials:
   - Create a Spotify Developer account and get API credentials
   - Create a Google Cloud Project and enable YouTube Data API v3
   - Update `src/main/resources/application.properties` with your credentials

3. Set environment variables (for Codespaces):
```bash
export REDIRECT_URI=https://your-codespace-name-3000.app.github.dev
```

4. Build and run the project:
```bash
chmod +x mvnw
./mvnw clean install
./mvnw spring-boot:run
```

5. Access the application:
   - Local: http://localhost:3000
   - Codespaces: https://your-codespace-name-3000.app.github.dev

## Usage Tips
1. Start with smaller playlists (up to 10 tracks) to manage quota limits
2. Allow delays between conversions (built into the application)
3. Monitor your YouTube API quota usage in Google Cloud Console
4. For larger playlists, consider breaking them into smaller chunks

## Troubleshooting
- If conversion fails, check the YouTube API quota in Google Cloud Console
- Ensure all redirect URIs match exactly in both API dashboards
- Clear browser cache if authentication issues occur
- Check application logs for detailed error messages

## Project Flow

```mermaid
graph TD
    A[Landing Page] --> B{Choose Conversion}
    B -->|Spotify to YouTube| C[Login to Spotify]
    B -->|YouTube to Spotify| D[Login to YouTube]
    
    C --> E[OAuth Authentication]
    E --> F[View Spotify Playlists]
    F --> G[Select Playlist]
    G --> H[Select Tracks]
    H --> I[Convert to YouTube]
    I --> J[View Result]

    D --> K[OAuth Authentication]
    K --> L[View YouTube Playlists]
    L --> M[Select Playlist]
    M --> N[Select Tracks]
    N --> O[Convert to Spotify]
    O --> P[View Result]
```

### Application Flow Explanation

1. **User Authentication Flow**:
   - User starts at landing page
   - Chooses conversion direction (Spotify → YouTube or YouTube → Spotify)
   - Authenticates with source platform using OAuth
   - Authenticates with destination platform

2. **Spotify to YouTube Conversion**:
   - User logs in to Spotify (OAuth)
   - Views list of Spotify playlists
   - Selects playlist to convert
   - Chooses specific tracks
   - System creates YouTube playlist
   - For each track:
     * Searches YouTube for best match
     * Adds video to playlist
   - Shows result with YouTube playlist link

3. **YouTube to Spotify Conversion**:
   - User logs in to YouTube (OAuth)
   - Views list of YouTube playlists
   - Selects playlist to convert
   - Chooses specific tracks
   - System creates Spotify playlist
   - For each track:
     * Searches Spotify for best match
     * Adds track to playlist
   - Shows result with Spotify playlist link

4. **API Quota Management**:
   - YouTube API daily limits:
     * Create playlist: 50 units
     * Search: 100 units/search
     * Add track: 50 units/addition
   - Batch processing for efficiency
   - Delays between API calls
   - Maximum 10 tracks per conversion

5. **Error Handling**:
   - OAuth authentication errors
   - API quota exceeded
   - Track not found
   - Network issues
   - Rate limiting

## Screenshots
![Screenshot (21)](https://github.com/user-attachments/assets/69dae491-f543-41c0-b097-c8b162fceb0e)
![Screenshot (22)](https://github.com/user-attachments/assets/6591e114-c50c-4c59-bfe3-4cbb9c7f9b80)
![Screenshot (23)](https://github.com/user-attachments/assets/d836fb28-fec7-4d8f-8081-e0f73283a638)
![Screenshot (24)](https://github.com/user-attachments/assets/938584c8-5b88-4894-a666-0217a4d1d5bd)
![Screenshot (27)](https://github.com/user-attachments/assets/52633621-afdf-436f-8f89-8474f0d4679f)
![Screenshot (28)](https://github.com/user-attachments/assets/fa56e06c-7b4e-45cd-a428-4f7c1c65dec9)
![Screenshot (29)](https://github.com/user-attachments/assets/9da6729b-3eb6-47ca-ba5d-9afee1d4e9a2)
![Screenshot (30)](https://github.com/user-attachments/assets/bd7cad4c-168b-43c0-9b21-1ff34731e371)
![Screenshot (38)](https://github.com/user-attachments/assets/6e50adb8-a9ac-419b-b8f5-089a0eee997a)
![Screenshot (35)](https://github.com/user-attachments/assets/97cc2a95-81ae-44c4-bfec-4b4a7da9afc0)
![Screenshot (37)](https://github.com/user-attachments/assets/fad963d9-ed86-462a-a222-23075aaf240c)




