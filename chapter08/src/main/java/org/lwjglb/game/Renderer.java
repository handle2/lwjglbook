package org.lwjglb.game;

import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL11.*;
import org.lwjglb.engine.GameItem;
import org.lwjglb.engine.Utils;
import org.lwjglb.engine.Window;
import org.lwjglb.engine.graph.Camera;
import org.lwjglb.engine.graph.Light;
import org.lwjglb.engine.graph.ShaderProgram;
import org.lwjglb.engine.graph.Transformation;

public class Renderer {

    /**
     * Field of View in Radians
     */
    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.01f;

    private static final float Z_FAR = 1000.f;

    private final Transformation transformation;

    private ShaderProgram shaderProgram;

    public Renderer() {
        transformation = new Transformation();
    }

    /**
     * Beállítja a shadereket
     * @param window
     * @throws Exception
     */
    public void init(Window window) throws Exception {
        // Create shader
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
        shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
        shaderProgram.link();
        
        // Create uniforms for modelView and projection matrices and texture
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("transformationMatrix");
        shaderProgram.createUniform("texture_sampler");
        shaderProgram.createUniform("lightPosition");
        shaderProgram.createUniform("lightColor");
        shaderProgram.createUniform("shineDamper");
        shaderProgram.createUniform("reflectivity");
        shaderProgram.createUniform("viewMatrix");
    }

    /**
     * Kiüríti a pixeleket render előtt
     */
    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    /**
     *
     * @param window
     * @param camera
     * @param gameItems
     */
    public void render(Window window, Camera camera, Light light, GameItem[] gameItems) {
        clear();
        
        if ( window.isResized() ) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();
        
        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);
        shaderProgram.setUniform("viewMatrix",viewMatrix);
        
        shaderProgram.setUniform("texture_sampler", 0);
        // Render each gameItem
        for(GameItem gameItem : gameItems) {
            // Set model view matrix for this item
            Matrix4f transformationMatrix = transformation.getTransformationMatrix(gameItem, viewMatrix);
            shaderProgram.setUniform("shineDamper", gameItem.getMesh().getTexture().getShineDamper());
            shaderProgram.setUniform("reflectivity", gameItem.getMesh().getTexture().getReflectivity());
            shaderProgram.setUniform("transformationMatrix", transformationMatrix);
            shaderProgram.setUniform("lightPosition", light.getPosition());
            shaderProgram.setUniform("lightColor", light.getColor());
            // Render the mes for this game item
            gameItem.getMesh().render();
        }

        shaderProgram.unbind();
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }
}
