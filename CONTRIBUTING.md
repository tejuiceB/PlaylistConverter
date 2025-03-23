# Contributing to PlaylistConverter

Thank you for your interest in contributing to PlaylistConverter! This document provides guidelines and steps for contributing.

## Getting Started

1. Fork the repository
2. Clone your fork: `git clone https://github.com/yourusername/PlaylistConverter.git`
3. Create a new branch: `git checkout -b feature/your-feature-name`

## Setting Up Development Environment

1. Install JDK 11 or later
2. Set up API credentials:
   - Create a Spotify Developer account
   - Create a Google Cloud Project and enable YouTube Data API
3. Copy `src/main/resources/application.properties.template` to `application.properties`
4. Add your API credentials to `application.properties`

## Making Changes

1. Make your changes
2. Add tests if applicable
3. Run tests: `./mvnw test`
4. Ensure code coverage remains high
5. Format your code
6. Commit with clear message

## Pull Request Process

1. Update documentation if needed
2. Run all tests locally
3. Push to your fork
4. Create a Pull Request with clear description
5. Wait for review

## Code Style

- Follow existing code style
- Use meaningful variable names
- Add comments for complex logic
- Keep methods focused and small

## Reporting Issues

- Use the GitHub issue tracker
- Include steps to reproduce
- Provide system information
- Add screenshots if applicable

## Community

- Be respectful and inclusive
- Help others in discussions
- Share knowledge and experience

## Questions?

Feel free to open an issue for questions or contact the maintainers.

Thank you for contributing!
