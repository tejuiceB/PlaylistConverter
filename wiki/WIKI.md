# PlayList Converter Wiki

Welcome to the PlayList Converter wiki! Here you'll find detailed information about using and configuring the application.

## Table of Contents
1. [Quick Start](#quick-start)
2. [Authentication Setup](#authentication-setup)
3. [Using the Application](#using-the-application)
4. [Troubleshooting](#troubleshooting)
5. [Technical Details](#technical-details)
6. [Support Resources](#support-resources)

## Quick Start

1. **Landing Page**: Start by choosing which platforms to connect
   ![Landing Page](https://github.com/user-attachments/assets/69dae491-f543-41c0-b097-c8b162fceb0e)

2. **Connect Services**: Click to connect your Spotify and YouTube accounts
   ![Connect Services](https://github.com/user-attachments/assets/6591e114-c50c-4c59-bfe3-4cbb9c7f9b80)

## Authentication Setup

### Spotify Setup
1. Click "Connect Spotify" to authorize access
   ![Spotify Login](https://github.com/user-attachments/assets/d836fb28-fec7-4d8f-8081-e0f73283a638)

2. View your Spotify playlists
   ![Spotify Playlists](https://github.com/user-attachments/assets/938584c8-5b88-4894-a666-0217a4d1d5bd)

### YouTube Setup
1. Click "Connect YouTube" to authorize access
   ![YouTube Login](https://github.com/user-attachments/assets/52633621-afdf-436f-8f89-8474f0d4679f)

2. View your YouTube playlists
   ![YouTube Playlists](https://github.com/user-attachments/assets/fa56e06c-7b4e-45cd-a428-4f7c1c65dec9)

## Using the Application

### Converting Playlists
1. **Select Tracks**: Choose which tracks to convert
   ![Select Tracks](https://github.com/user-attachments/assets/9da6729b-3eb6-47ca-ba5d-9afee1d4e9a2)

2. **Review Selection**: Verify your selected tracks
   ![Review Selection](https://github.com/user-attachments/assets/bd7cad4c-168b-43c0-9b21-1ff34731e371)

3. **Conversion Result**: View the converted playlist
   ![Conversion Result](https://github.com/user-attachments/assets/6e50adb8-a9ac-419b-b8f5-089a0eee997a)

### Managing API Quotas
- YouTube API has daily limits
- For a 10-track playlist:
  - Creating playlist: 50 units
  - Searching tracks: 1000 units
  - Adding tracks: 500 units
  ![Quota Usage](https://github.com/user-attachments/assets/97cc2a95-81ae-44c4-bfec-4b4a7da9afc0)

## Technical Details

### System Architecture
- Built with Spring Boot 2.7.18
- Uses OAuth 2.0 for secure authentication
- RESTful API integration with both platforms
- Thymeleaf templating engine
- Bootstrap 4.5.2 for responsive design

### API Integration
1. **Spotify API**
   - Authentication: OAuth 2.0
   - Scope: playlist-read-private, playlist-modify-private
   - Rate Limits: 1000 requests/day
   - Batch Processing: Up to 50 tracks per request

2. **YouTube API**
   - Authentication: OAuth 2.0
   - Scope: youtube.force-ssl
   - Daily Quota: 10,000 units
   - Search Cost: 100 units/query
   - Write Operations: 50 units each

## Troubleshooting

### Common Issues
- If conversion fails, check YouTube API quota
- Clear browser cache for auth issues
- Ensure all redirect URIs match
![Error Handling](https://github.com/user-attachments/assets/fad963d9-ed86-462a-a222-23075aaf240c)

### Advanced Troubleshooting

#### OAuth Issues
- Token expired: Re-authenticate through landing page
- Invalid scope: Check API console settings
- Redirect URI mismatch: Verify URIs in both platforms

#### Quota Management
- Monitor usage in Google Cloud Console
- Implement batch processing for large playlists
- Use caching for frequent conversions
- Schedule conversions during off-peak hours

#### Performance Optimization
- Convert during non-peak hours
- Use batch processing when available
- Cache frequently accessed playlists
- Monitor API response times

### Best Practices
1. Start with smaller playlists (≤10 tracks)
2. Allow delays between conversions
3. Monitor your quota usage
4. Break large playlists into chunks

## Support Resources

### Getting Help
- Technical Support: tech.support@playlistconverter.com
- Feature Requests: features@playlistconverter.com
- Bug Reports: bugs@playlistconverter.com

### Community
- GitHub Discussions: [Join the conversation](https://github.com/yourusername/PlaylistConverter/discussions)
- Contributing: See our [Contributing Guide](../CONTRIBUTING.md)
- Feature Requests: Submit via GitHub Issues

Need more help? [Create an issue](https://github.com/yourusername/PlaylistConverter/issues) on our GitHub repository.
