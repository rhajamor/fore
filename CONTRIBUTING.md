# Contributing to FORE

Thank you for your interest in contributing to FORE! This guide will help you get started.

## Prerequisites

- Java 21+ ([Adoptium Temurin](https://adoptium.net/) recommended)
- Git
- A GPU supporting OpenGL 4.1+

## Building

```bash
git clone https://github.com/rhajamor/fore.git
cd fore
./gradlew build
```

## Running

```bash
# macOS (Gradle handles -XstartOnFirstThread):
./gradlew runEngine

# Or directly:
# macOS:
java -XstartOnFirstThread -jar build/fore-engine-0.1.0-runner.jar
# Linux/Windows:
java -jar build/fore-engine-0.1.0-runner.jar
```

## Code Style

- Standard Java conventions (4-space indent, braces on same line)
- No IDE-specific formatting plugins required
- Keep classes focused — one responsibility per file
- Minimal Javadoc: every public class gets at least a one-line doc comment

## Making Changes

1. Fork the repository
2. Create a feature branch from `master`: `git checkout -b feature/my-feature`
3. Make your changes with clear, focused commits
4. Ensure `./gradlew build` passes
5. Submit a pull request against `master`

## Pull Request Guidelines

- Keep PRs focused — one feature or fix per PR
- Write a clear description of what changed and why
- Include steps to test the change (e.g., "run scene 2, orbit camera, check shadows")
- Screenshots or GIFs are welcome for visual changes

## Reporting Bugs

Use the [bug report template](https://github.com/rhajamor/fore/issues/new?template=bug_report.md). Include:
- Steps to reproduce
- Expected vs actual behavior
- OS, GPU, and Java version

## Questions?

Open a [discussion](https://github.com/rhajamor/fore/discussions) or file an issue.
