package org.lwjglb.game;

import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjglb.engine.GameItem;
import org.lwjglb.engine.IGameLogic;
import org.lwjglb.engine.MouseInput;
import org.lwjglb.engine.Window;
import org.lwjglb.engine.graph.*;

public class DummyGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private final Light light;

    private GameItem[] gameItems;

    private static final float CAMERA_POS_STEP = 0.05f;

    /**
     * Inicializálja a DummyGame dependenciáit
     */
    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
        light = new Light(new Vector3f(0, 0, 0),new Vector3f(1, 1, 1));
    }

    /**
     * Az init állítja be a game itemeket
     * @param window
     * @throws Exception
     */
    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        Mesh stallMesh = ObjLoader.loadObjModel("dragon","textures/dragon.png");
        GameItem gameItem0 = new GameItem(stallMesh);
        gameItem0.setScale(0.1f);
        gameItem0.increaseRotation(200);
        gameItems = new GameItem[]{gameItem0};
    }

    /**
     * Az input kezeli a usertől bejövő elemeket
     * @param window
     * @param mouseInput
     */
    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            cameraInc.y = 1;
        }
    }

    /**
     * A kamerát updateli
     * @param interval
     * @param mouseInput
     */
    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        // Update camera based on mouse            
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }

        for (GameItem gameItem : gameItems) {
            float rotation = gameItem.getRotation().y + 1;
            if (rotation > 360) {
                rotation = 0;
            }
            gameItem.setRotation(0,rotation,0);
        }
    }

    /**
     * Ez adja át a renderernek az updatelt game itemeket windowal és camerával
     * @param window
     */
    @Override
    public void render(Window window) {
        renderer.render(window, camera,light, gameItems);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
    }

}
