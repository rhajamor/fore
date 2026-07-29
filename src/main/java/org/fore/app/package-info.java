/**
 * Quarkus application entry point. Bootstraps the CDI container on a background
 * thread and runs the engine's render loop on the main thread (required by GLFW
 * on macOS).
 */
package org.fore.app;
