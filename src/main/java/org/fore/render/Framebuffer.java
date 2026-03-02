package org.fore.render;

import static org.lwjgl.opengl.GL41.*;

public class Framebuffer implements AutoCloseable {

    private int fbo;
    private int colorTexture;
    private int depthTexture;
    private int depthRenderbuffer;
    private int width;
    private int height;
    private final boolean hasColorAttachment;

    private Framebuffer(int fbo, int colorTexture, int depthTexture, int depthRenderbuffer,
                        int width, int height, boolean hasColorAttachment) {
        this.fbo = fbo;
        this.colorTexture = colorTexture;
        this.depthTexture = depthTexture;
        this.depthRenderbuffer = depthRenderbuffer;
        this.width = width;
        this.height = height;
        this.hasColorAttachment = hasColorAttachment;
    }

    public static Framebuffer createHDR(int width, int height) {
        int fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);

        int colorTex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, colorTex);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorTex, 0);

        int depthRbo = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, depthRbo);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, depthRbo);

        checkStatus();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        return new Framebuffer(fbo, colorTex, 0, depthRbo, width, height, true);
    }

    public static Framebuffer createShadowMap(int width, int height) {
        int fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);

        int depthTex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthTex);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        float[] borderColor = {1.0f, 1.0f, 1.0f, 1.0f};
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTex, 0);

        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        checkStatus();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        return new Framebuffer(fbo, 0, depthTex, 0, width, height, false);
    }

    public void resize(int newWidth, int newHeight) {
        if (newWidth == width && newHeight == height) return;

        if (hasColorAttachment && colorTexture != 0) {
            glBindTexture(GL_TEXTURE_2D, colorTexture);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, newWidth, newHeight, 0, GL_RGBA, GL_FLOAT, 0);
        }

        if (depthTexture != 0) {
            glBindTexture(GL_TEXTURE_2D, depthTexture);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, newWidth, newHeight, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
        }

        if (depthRenderbuffer != 0) {
            glBindRenderbuffer(GL_RENDERBUFFER, depthRenderbuffer);
            glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, newWidth, newHeight);
        }

        width = newWidth;
        height = newHeight;
    }

    private static void checkStatus() {
        int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (status != GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Framebuffer incomplete: 0x" + Integer.toHexString(status));
        }
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        glViewport(0, 0, width, height);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bindColorTexture(int unit) {
        glActiveTexture(GL_TEXTURE0 + unit);
        glBindTexture(GL_TEXTURE_2D, colorTexture);
    }

    public void bindDepthTexture(int unit) {
        glActiveTexture(GL_TEXTURE0 + unit);
        glBindTexture(GL_TEXTURE_2D, depthTexture);
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getColorTexture() { return colorTexture; }
    public int getDepthTexture() { return depthTexture; }

    @Override
    public void close() {
        if (colorTexture != 0) glDeleteTextures(colorTexture);
        if (depthTexture != 0) glDeleteTextures(depthTexture);
        if (depthRenderbuffer != 0) glDeleteRenderbuffers(depthRenderbuffer);
        if (fbo != 0) glDeleteFramebuffers(fbo);
        fbo = 0;
    }
}
