package de.tum.cit.fop.maze.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.entity.GameObject;
import de.tum.cit.fop.maze.util.Constants;

/**
 * Camera controller that follows a target entity.
 * Keeps the target strictly centered in the viewport with optional smoothing,
 * and clamps to maze boundaries. Supports zooming and window resizing.
 */
public class Camera2DController {
    
    private final OrthographicCamera camera;
    private GameObject target;
    private Maze maze;
    
    // Camera settings
    private float zoom;
    private float targetZoom;
    private boolean smoothFollow;
    private float followSpeed;
    private float zoomSpeed;
    
    /**
     * Creates a camera controller.
     * @param viewportWidth Initial viewport width
     * @param viewportHeight Initial viewport height
     */
    public Camera2DController(float viewportWidth, float viewportHeight) {
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, viewportWidth, viewportHeight);
        
        this.zoom = 1f;
        this.targetZoom = 1f;
        this.smoothFollow = true;
        this.followSpeed = Constants.CAMERA_LERP_SPEED;
        this.zoomSpeed = 3f;
    }
    
    /**
     * Updates the camera position and zoom.
     * @param deltaTime Frame delta time
     */
    public void update(float deltaTime) {
        if (target != null && maze != null) {
            updatePosition(deltaTime);
        }
        
        // Smooth zoom
        if (Math.abs(zoom - targetZoom) > 0.001f) {
            zoom = MathUtils.lerp(zoom, targetZoom, zoomSpeed * deltaTime);
            camera.zoom = zoom;
            // After zoom change, ensure player is still within bounds
            if (target != null && maze != null) {
                ensureTargetInBounds();
            }
        }
        
        camera.update();
    }
    
    /**
     * Updates camera position to follow target.
     * Centers the camera directly on the player with optional smoothing.
     */
    private void updatePosition(float deltaTime) {
        Vector2 targetPos = target.getPosition();
        
        float targetCenterX = targetPos.x + target.getBounds().width / 2f;
        float targetCenterY = targetPos.y + target.getBounds().height / 2f;
        
        float newCamX = targetCenterX;
        float newCamY = targetCenterY;
        
        float camX = camera.position.x;
        float camY = camera.position.y;
        
        if (smoothFollow) {
            camX = MathUtils.lerp(camX, newCamX, followSpeed * deltaTime);
            camY = MathUtils.lerp(camY, newCamY, followSpeed * deltaTime);
        } else {
            camX = newCamX;
            camY = newCamY;
        }
        
        camX = clampCameraX(camX);
        camY = clampCameraY(camY);
        
        camera.position.set(camX, camY, 0);
    }
    
    /**
     * Ensures the target is centered after zoom or resize.
     * Called when zoom changes or window is resized.
     */
    private void ensureTargetInBounds() {
        if (target == null) return;
        
        updatePosition(0);
    }
    
    /**
     * Clamps camera X to maze bounds.
     */
    private float clampCameraX(float camX) {
        float halfViewWidth = (camera.viewportWidth * zoom) / 2f;
        float mazeWidth = maze.getPixelWidth();
        
        if (mazeWidth <= camera.viewportWidth * zoom) {
            return mazeWidth / 2f;
        }
        
        return MathUtils.clamp(camX, halfViewWidth, mazeWidth - halfViewWidth);
    }
    
    /**
     * Clamps camera Y to maze bounds.
     */
    private float clampCameraY(float camY) {
        float halfViewHeight = (camera.viewportHeight * zoom) / 2f;
        float mazeHeight = maze.getPixelHeight();
        
        if (mazeHeight <= camera.viewportHeight * zoom) {
            return mazeHeight / 2f;
        }
        
        return MathUtils.clamp(camY, halfViewHeight, mazeHeight - halfViewHeight);
    }
    
    /**
     * Immediately centers camera on target.
     */
    public void snapToTarget() {
        if (target == null || maze == null) return;
        
        Vector2 targetPos = target.getPosition();
        float camX = targetPos.x + target.getBounds().width / 2f;
        float camY = targetPos.y + target.getBounds().height / 2f;
        
        camX = clampCameraX(camX);
        camY = clampCameraY(camY);
        
        camera.position.set(camX, camY, 0);
        camera.update();
    }
    
    /**
     * Zooms in (decreases zoom value).
     */
    public void zoomIn() {
        targetZoom = Math.max(Constants.MIN_ZOOM, targetZoom - Constants.ZOOM_STEP);
    }
    
    /**
     * Zooms out (increases zoom value).
     */
    public void zoomOut() {
        targetZoom = Math.min(Constants.MAX_ZOOM, targetZoom + Constants.ZOOM_STEP);
    }
    
    /**
     * Sets zoom level directly.
     * @param zoom Zoom level (1.0 = normal)
     */
    public void setZoom(float zoom) {
        this.targetZoom = MathUtils.clamp(zoom, Constants.MIN_ZOOM, Constants.MAX_ZOOM);
        this.zoom = this.targetZoom;
        camera.zoom = this.zoom;
    }
    
    /**
     * Handles window resize.
     * Updates the viewport and ensures the player remains visible within the middle 80%.
     * @param width New width
     * @param height New height
     */
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.zoom = zoom;
        
        if (target != null && maze != null) {
            ensureTargetInBounds();
        }
        
        camera.update();
    }
    
    /**
     * Sets the target to follow.
     * @param target The game object to follow
     */
    public void setTarget(GameObject target) {
        this.target = target;
    }
    
    /**
     * Sets the maze for boundary calculations.
     * @param maze The maze
     */
    public void setMaze(Maze maze) {
        this.maze = maze;
    }
    
    /**
     * Gets the underlying camera.
     * @return The OrthographicCamera
     */
    public OrthographicCamera getCamera() {
        return camera;
    }
    
    /**
     * Gets current zoom level.
     * @return Zoom value
     */
    public float getZoom() {
        return zoom;
    }
    
    /**
     * Sets whether camera follows smoothly.
     * @param smooth true for smooth follow
     */
    public void setSmoothFollow(boolean smooth) {
        this.smoothFollow = smooth;
    }
    
    /**
     * Sets the follow speed for smooth camera.
     * @param speed Lerp speed (higher = faster)
     */
    public void setFollowSpeed(float speed) {
        this.followSpeed = speed;
    }
    
    /**
     * Handles scroll wheel input for zooming.
     * Positive amount zooms out, negative amount zooms in.
     * @param amount The scroll amount
     */
    public void handleScrollZoom(float amount) {
        if (amount > 0) {
            zoomOut();
        } else if (amount < 0) {
            zoomIn();
        }
    }
    
    /**
     * Gets the current target zoom level.
     * @return Target zoom value
     */
    public float getTargetZoom() {
        return targetZoom;
    }
}
