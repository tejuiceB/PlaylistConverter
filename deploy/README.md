# AWS Deployment Guide for PlaylistConverter

This guide explains how to deploy the PlaylistConverter web application on AWS EC2 with Nginx, SSL, and environment configuration.

---

## 1. Prerequisites

- AWS account with EC2 access
- SSH key pair for EC2
- Java 11+ and Maven installed locally
- Domain name (e.g., spoyou.site) pointing to your EC2 public IP

---

## 2. EC2 Instance Setup

1. **Launch EC2 Instance**
   - Choose Amazon Linux 2 or Ubuntu
   - Select t2.micro (free tier) or higher
   - Open ports: 22 (SSH), 80 (HTTP), 443 (HTTPS)

2. **Connect to EC2**
   ```bash
   ssh -i your-key.pem ec2-user@your-ec2-ip
   ```

3. **Install Java and Nginx**
   ```bash
   # For Amazon Linux 2
   sudo yum update -y
   sudo amazon-linux-extras install java-openjdk11 nginx1 -y
   sudo systemctl enable nginx
   sudo systemctl start nginx
   ```

---

## 3. Application Deployment

1. **Build the JAR Locally**
   ```bash
   ./mvnw clean package
   ```

2. **Copy Files to EC2**
   ```bash
   scp -i your-key.pem target/playlist-converter-1.0.0.jar ec2-user@your-ec2-ip:/opt/playlistconverter/
   scp -i your-key.pem src/main/resources/application.properties ec2-user@your-ec2-ip:/opt/playlistconverter/
   ```

3. **Create Systemd Service**
   - Create `/etc/systemd/system/playlistconverter.service`:
     ```
     [Unit]
     Description=Playlist Converter Web Application
     After=network.target

     [Service]
     Type=simple
     User=root
     WorkingDirectory=/opt/playlistconverter
     ExecStart=/usr/bin/java -jar /opt/playlistconverter/playlist-converter-1.0.0.jar
     Restart=always
     RestartSec=10

     [Install]
     WantedBy=multi-user.target
     ```
   - Reload and start:
     ```bash
     sudo systemctl daemon-reload
     sudo systemctl start playlistconverter
     sudo systemctl enable playlistconverter
     ```

4. **Check Logs**
   ```bash
   sudo journalctl -u playlistconverter -f
   ```

---

## 4. Nginx Reverse Proxy

1. **Configure Nginx**
   - Edit `/etc/nginx/conf.d/playlistconverter.conf`:
     ```
     server {
         listen 80;
         listen [::]:80;
         server_name spoyou.site www.spoyou.site;

         location / {
             proxy_pass http://127.0.0.1:3000;
             proxy_set_header Host $host;
             proxy_set_header X-Real-IP $remote_addr;
             proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
             proxy_set_header X-Forwarded-Proto $scheme;
         }
     }
     ```
   - Test and reload:
     ```bash
     sudo nginx -t
     sudo systemctl restart nginx
     ```

---

## 5. SSL Certificate (HTTPS)

1. **Install Certbot**
   ```bash
   sudo yum install -y certbot python2-certbot-nginx
   ```

2. **Obtain SSL Certificate**
   ```bash
   sudo certbot --nginx -d spoyou.site -d www.spoyou.site
   ```

3. **Auto-Renewal**
   ```bash
   sudo crontab -e
   # Add:
   0 0,12 * * * /usr/bin/certbot renew --quiet
   ```

---

## 6. Environment Variables & Configuration

- Edit `/opt/playlistconverter/application.properties` with your API credentials and redirect URIs.
- Example:
  ```
  spotify.client.id=YOUR_SPOTIFY_CLIENT_ID
  spotify.client.secret=YOUR_SPOTIFY_CLIENT_SECRET
  spotify.redirect.uri=https://spoyou.site/spotify_callback
  spotify.scope=playlist-read-private playlist-modify-private user-read-private

  youtube.client.id=YOUR_YOUTUBE_CLIENT_ID
  youtube.client.secret=YOUR_YOUTUBE_CLIENT_SECRET
  youtube.redirect.uri=https://spoyou.site/youtube_callback
  youtube.scope=https://www.googleapis.com/auth/youtube https://www.googleapis.com/auth/userinfo.email
  ```

---

## 7. Monitoring & Logs

- **Nginx logs:** `/var/log/nginx/access.log` and `/var/log/nginx/error.log`
- **App logs:** `sudo journalctl -u playlistconverter -f`
- **User login CSV:** `/opt/playlistconverter/user_logins.csv`

---

## 8. Viewing and Downloading User Login Details

### Watch New Logins in Real Time

```bash
cd /opt/playlistconverter
tail -f user_logins.csv
```

### Download the CSV File to Your Local Machine

On your local machine, run:

```bash
scp -i your-key.pem ec2-user@your-ec2-ip:/opt/playlistconverter/user_logins.csv .
```

This will download `user_logins.csv` to your current local directory.

### Open and View the CSV

- Open with Excel, Google Sheets, or any text editor.
- Each row contains: `timestamp,service,username,email,ip`

---

## 9. Useful Commands

- Restart app: `sudo systemctl restart playlistconverter`
- Restart Nginx: `sudo systemctl restart nginx`
- Check app status: `sudo systemctl status playlistconverter`
- Check Nginx status: `sudo systemctl status nginx`
- View logs: `sudo tail -f /var/log/nginx/access.log`

---

## 10. Troubleshooting

- **504 Gateway Timeout:** Check if the backend is running and accessible on port 3000.
- **SSL Issues:** Ensure DNS is propagated and rerun Certbot if needed.
- **API Errors:** Check quota and credentials in Google Cloud and Spotify Developer dashboards.

---

## 11. References

- [AWS EC2 Documentation](https://docs.aws.amazon.com/ec2/)
- [Nginx Documentation](https://nginx.org/en/docs/)
- [Certbot Documentation](https://certbot.eff.org/)
- [Spring Boot Docs](https://spring.io/projects/spring-boot)

---

**For questions or support, see the main project [SUPPORT.md](../docs/SUPPORT.md).**
