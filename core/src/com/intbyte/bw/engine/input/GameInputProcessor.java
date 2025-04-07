package com.intbyte.bw.engine.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.intbyte.bw.engine.callbacks.CallBack;

import com.intbyte.bw.engine.entity.Player;


public class GameInputProcessor implements InputProcessor {
    private static boolean isReadyCallBack;
    private final PerspectiveCamera camera;
    private Vector3 position;
    private Player player = Player.getPlayer();
    private float x, z;

    public GameInputProcessor(PerspectiveCamera camera) {
        this.camera = camera;
        position = new Vector3();
    }

    public static boolean isReadyCallBack() {
        return isReadyCallBack;
    }

    public static Vector3 getFastBlock(PerspectiveCamera camera, Vector3 position, int screenX, int screenY) {
        Ray ray = camera.getPickRay(screenX, screenY);
        return position.set(ray.direction).
                scl(-ray.origin.y / ray.direction.y).
                add(ray.origin).
                add(0, 0, 0.1f);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Ray ray = camera.getPickRay(screenX, screenY);
        final float distance = -ray.origin.y / ray.direction.y;

        position.set(ray.direction).scl(distance).add(ray.origin).add((float) player.getPixelX(), 0, (float) player.getPixelZ()).scl(0.1f).add(0, 0, 1);
        x = (float) (player.getPixelX() / 10 - Math.floor(player.getPixelX() / 10));
        z = (float) (player.getPixelZ() / 10 - Math.floor(player.getPixelZ() / 10));

        isReadyCallBack = true;
        CallBack.executeTouchedCallBacks(position);
        CallBack.executeTouchOnBlockCallBack(position.x - x, position.z - z);
        isReadyCallBack = false;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Ray ray = camera.getPickRay(screenX, screenY);
        final float distance = -ray.origin.y / ray.direction.y;
        position.set(ray.direction).scl(distance).add(ray.origin).add((float) player.getPixelX(), 0, (float) player.getPixelZ()).scl(0.1f).add(0, 0, 1);


        isReadyCallBack = true;
        CallBack.executeDraggedCallBacks(position);
        isReadyCallBack = false;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    /**
     * Called when a touch event is cancelled. This can happen if the platform determines the touch sequence should
     * end prematurely (e.g., the application is interrupted).
     * @param screenX The x coordinate, origin is in the upper left corner
     * @param screenY The y coordinate, origin is in the upper left corner
     * @param pointer the pointer for the event.
     * @param button the button for the event.
     * @return whether the input was processed
     */
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        // Default implementation, can be expanded if needed
        return false;
    }
}
