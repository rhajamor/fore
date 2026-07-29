/**
 * OpenGL render pipeline. Manages the three-pass rendering flow: shadow map
 * generation, PBR geometry pass to an HDR framebuffer, and post-process
 * tone mapping to screen.
 */
package org.fore.render;
