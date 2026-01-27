package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.util.Constants;

/**
 * Abstract base class for all game entities.
 * Provides common functionality for position, bounds, and rendering.
 */
public abstract class AbstractEntity implements GameObject {
    
    protected Vector2 position;
    protected Vector2 velocity;
    protected float width;
    protected float height;
    protected Rectangle bounds;
    protected boolean active;
    protected TextureRegion textureRegion;
    
    protected int tileX;
    protected int tileY;
    
    /**
     * Creates an entity at the specified tile position.
     * @param tileX Grid X coordinate
     * @param tileY Grid Y coordinate
     * @param width Entity width in pixels
     * @param height Entity height in pixels
     */
    public AbstractEntity(int tileX, int tileY, float width, float height) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.width = width;
        this.height = height;
        this.active = true;
        
        float worldX = tileX * Constants.TILE_SIZE + (Constants.TILE_SIZE - width) / 2f;
        float worldY = tileY * Constants.TILE_SIZE + (Constants.TILE_SIZE - height) / 2f;
        
        this.position = new Vector2(worldX, worldY);
        this.velocity = new Vector2(0, 0);
        this.bounds = new Rectangle(worldX, worldY, width, height);
    }
    
    /**
     * Creates an entity at the specified world position.
     * @param worldX World X coordinate
     * @param worldY World Y coordinate
     * @param width Entity width in pixels
     * @param height Entity height in pixels
     */
    public AbstractEntity(float worldX, float worldY, float width, float height) {
        this.width = width;
        this.height = height;
        this.active = true;
        
        this.position = new Vector2(worldX, worldY);
        this.velocity = new Vector2(0, 0);
        this.bounds = new Rectangle(worldX, worldY, width, height);
        
        this.tileX = (int) ((worldX + width / 2f) / Constants.TILE_SIZE);
        this.tileY = (int) ((worldY + height / 2f) / Constants.TILE_SIZE);
    }
    
    @Override
    public void update(float deltaTime) {
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        
        bounds.setPosition(position);
        
        updateTilePosition();
    }
    
    @Override
    public void render(SpriteBatch batch) {
        if (textureRegion != null && active) {
            batch.draw(textureRegion, position.x, position.y, width, height);
        }
    }
    
    /**
     * Updates tile coordinates based on current world position.
     */
    protected void updateTilePosition() {
        float centerX = position.x + width / 2f;
        float centerY = position.y + height / 2f;
        
        tileX = (int) (centerX / Constants.TILE_SIZE);
        tileY = (int) (centerY / Constants.TILE_SIZE);
    }
    
    /**
     * Moves the entity to a specific tile.
     * @param newTileX Target tile X
     * @param newTileY Target tile Y
     */
    public void setTilePosition(int newTileX, int newTileY) {
        this.tileX = newTileX;
        this.tileY = newTileY;
        
        position.x = newTileX * Constants.TILE_SIZE + (Constants.TILE_SIZE - width) / 2f;
        position.y = newTileY * Constants.TILE_SIZE + (Constants.TILE_SIZE - height) / 2f;
        
        bounds.setPosition(position);
    }
    
    /**
     * Checks if this entity overlaps with another.
     * @param other The other entity
     * @return true if overlapping
     */
    public boolean overlaps(GameObject other) {
        return bounds.overlaps(other.getBounds());
    }
    
    /**
     * Gets the center position of this entity.
     * @return Center point
     */
    public Vector2 getCenter() {
        return new Vector2(position.x + width / 2f, position.y + height / 2f);
    }
    
    /**
     * Gets the distance to another entity.
     * @param other The other entity
     * @return Distance in pixels
     */
    public float distanceTo(GameObject other) {
        return getCenter().dst(other.getPosition().cpy().add(
            other.getBounds().width / 2f, 
            other.getBounds().height / 2f
        ));
    }
    
    @Override
    public Vector2 getPosition() {
        return position.cpy();
    }
    
    @Override
    public void setPosition(float x, float y) {
        position.set(x, y);
        bounds.setPosition(position);
        updateTilePosition();
    }
    
    @Override
    public Rectangle getBounds() {
        return bounds;
    }
    
    @Override
    public boolean isActive() {
        return active;
    }
    
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public int getTileX() {
        return tileX;
    }
    
    @Override
    public int getTileY() {
        return tileY;
    }
    
    public Vector2 getVelocity() {
        return velocity;
    }
    
    public void setVelocity(float vx, float vy) {
        velocity.set(vx, vy);
    }
    
    public float getWidth() {
        return width;
    }
    
    public float getHeight() {
        return height;
    }
    
    public void setTextureRegion(TextureRegion region) {
        this.textureRegion = region;
    }
    
    public TextureRegion getTextureRegion() {
        return textureRegion;
    }
}
