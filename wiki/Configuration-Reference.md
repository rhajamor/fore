# Configuration Reference

FORE uses Quarkus configuration via `src/main/resources/application.properties`. All engine properties use the `fore.*` prefix.

## Engine Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `fore.window.width` | `int` | `1600` | Window width in pixels |
| `fore.window.height` | `int` | `900` | Window height in pixels |
| `fore.window.title` | `String` | `FORE Engine — Free OpenGL Rendering Engine` | Window title bar text |
| `fore.render.exposure` | `float` | `1.0` | HDR exposure multiplier for tone mapping |
| `fore.render.vsync` | `boolean` | `true` | Enable vertical sync (swap interval 1) |
| `fore.scene` | `String` | `pbr` | Default scene to load on startup |

### Scene Names

The `fore.scene` property accepts these values:

| Value | Scene | Description |
|-------|-------|-------------|
| `basic` | `BasicScene` | Sphere, cube, torus, cylinder with two lights |
| `pbr` | `PBRShowcase` | 7×7 metallic/roughness grid (default) |
| `lighting` | `LightingDemo` | Interior with colored point lights and spotlight |
| `shapes` | `ShapesShowcase` | All geometry types with metallic materials |

## Quarkus Properties

These Quarkus properties are set in the default `application.properties`:

| Property | Value | Description |
|----------|-------|-------------|
| `quarkus.banner.enabled` | `false` | Suppress Quarkus startup banner |
| `quarkus.log.level` | `INFO` | Root log level |
| `quarkus.log.category."org.fore".level` | `INFO` | Engine-specific log level |
| `quarkus.package.jar.type` | `uber-jar` | Package as single executable jar |
| `quarkus.package.main-class` | `org.fore.app.ForeApplication` | Custom main class for GLFW thread control |

## Default Configuration File

```properties
# FORE Engine Configuration

# Window
fore.window.width=1600
fore.window.height=900
fore.window.title=FORE Engine — Free OpenGL Rendering Engine

# Rendering
fore.render.exposure=1.0
fore.render.vsync=true

# Scene (basic, pbr, lighting, shapes)
fore.scene=pbr

# Quarkus
quarkus.banner.enabled=false
quarkus.log.level=INFO
quarkus.log.category."org.fore".level=INFO

# Use uber-jar so we can control the main class (GLFW needs Thread 0 on macOS)
quarkus.package.jar.type=uber-jar
quarkus.package.main-class=org.fore.app.ForeApplication
```

## Overriding at Runtime

Quarkus supports property overrides via:

1. **System properties:** `-Dfore.window.width=1920`
2. **Environment variables:** `FORE_WINDOW_WIDTH=1920`
3. **`.env` file** in the working directory

Example:
```bash
java -XstartOnFirstThread -Dfore.scene=lighting -Dfore.render.exposure=1.5 \
     -jar build/fore-engine-0.1.0-runner.jar
```
