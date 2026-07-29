package org.fore.examples;

import org.fore.scene.Scene;

/** Interface for built-in demo scenes. Implement {@link #setup(Scene)} to populate a scene with geometry, materials, and lights. */
public interface ExampleScene {
    void setup(Scene scene);
}
