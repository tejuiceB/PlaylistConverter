# Contributing to PlaylistConverter

## Table of Contents
1. [Code of Conduct](#code-of-conduct)
2. [Getting Started](#getting-started)
3. [Development Process](#development-process)
4. [Pull Request Guidelines](#pull-request-guidelines)
5. [Coding Standards](#coding-standards)
6. [Testing Guidelines](#testing-guidelines)
7. [Documentation](#documentation)
8. [Community](#community)

## Code of Conduct
- Be respectful and inclusive
- No discrimination or harassment
- Professional communication
- Constructive feedback

## Getting Started

### Prerequisites
- JDK 11 or later
- Maven 3.6+
- Git
- IDE (IntelliJ IDEA or VS Code recommended)

### Environment Setup
1. Fork and clone:
   ```bash
   git clone https://github.com/yourusername/PlaylistConverter.git
   cd PlaylistConverter
   ```

2. Set up API credentials:
   ```bash
   cp src/main/resources/application.properties.template src/main/resources/application.properties
   ```

3. Configure credentials:
   - Create Spotify Developer account
   - Set up Google Cloud Project
   - Enable YouTube Data API
   - Update application.properties

### Building
```bash
./mvnw clean install
```

## Development Process

### Branches
- `main`: Production-ready code
- `develop`: Development branch
- `feature/*`: New features
- `bugfix/*`: Bug fixes
- `hotfix/*`: Critical fixes

### Commit Messages
```
type(scope): Short description

Detailed description if needed
```

Types:
- feat: New feature
- fix: Bug fix
- docs: Documentation
- style: Formatting
- refactor: Code restructuring
- test: Adding tests
- chore: Maintenance

## Pull Request Guidelines

### Before Submitting
1. Update documentation
2. Add tests
3. Run test suite
4. Check code coverage
5. Format code

### PR Template
```markdown
## Description
[Description of changes]

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Checklist
- [ ] Tests added/updated
- [ ] Documentation updated
- [ ] Code formatted
- [ ] No new warnings
```

## Coding Standards

### Java
- Follow Oracle Java Style Guide
- Use meaningful names
- Write self-documenting code
- Maximum line length: 100
- Use proper indentation

### Comments
```java
/**
 * Method documentation
 * @param parameter Description
 * @return Description
 */
```

### Testing
- Unit tests required
- Integration tests for API
- Maintain >80% coverage
- Mock external services

## Testing Guidelines

### Running Tests
```bash
./mvnw test
```

### Coverage Report
```bash
./mvnw jacoco:report
```

## Documentation

### Required Documentation
- API endpoints
- Configuration options
- Setup instructions
- Usage examples

### Documentation Style
- Clear and concise
- Include code examples
- Update diagrams
- Keep README current

## Community

### Getting Help
- [Telegram Group](https://t.me/+TwGNOCbIKrM5M2I1)
- GitHub Discussions
- Stack Overflow tags

### Issue Reporting
1. Search existing issues
2. Use issue templates
3. Provide reproduction steps
4. Include environment details

## License
By contributing, you agree that your contributions will be licensed under the MIT License.
