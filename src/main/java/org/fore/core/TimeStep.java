package org.fore.core;

/** Tracks frame timing: delta time between frames, FPS, and frame count. */
public class TimeStep {

    private long lastTime;
    private float deltaTime;
    private float totalTime;
    private int frameCount;
    private float fpsAccumulator;
    private int fpsFrameCount;
    private float currentFps;

    public TimeStep() {
        lastTime = System.nanoTime();
    }

    public void update() {
        long now = System.nanoTime();
        deltaTime = (now - lastTime) / 1_000_000_000.0f;
        lastTime = now;
        totalTime += deltaTime;
        frameCount++;

        fpsAccumulator += deltaTime;
        fpsFrameCount++;
        if (fpsAccumulator >= 1.0f) {
            currentFps = fpsFrameCount / fpsAccumulator;
            fpsAccumulator = 0;
            fpsFrameCount = 0;
        }
    }

    public float getDeltaTime() {
        return deltaTime;
    }

    public float getTotalTime() {
        return totalTime;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public float getFps() {
        return currentFps;
    }
}
