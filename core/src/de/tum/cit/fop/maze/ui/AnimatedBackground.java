package de.tum.cit.fop.maze.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

/**
 * Renders an animated background from a sprite sheet.
 */
public class AnimatedBackground implements Disposable {
    private final Texture spriteSheet;
    private final Animation<TextureRegion> animation;
    private float stateTime;

    public AnimatedBackground(String texturePath, int cols, int rows, float frameDuration) {
        spriteSheet = new Texture(Gdx.files.internal(texturePath));

        int frameWidth = spriteSheet.getWidth() / cols;
        int frameHeight = spriteSheet.getHeight() / rows;

        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, frameWidth, frameHeight);
        TextureRegion[] frames = new TextureRegion[cols * rows];

        int index = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                frames[index++] = tmp[row][col];
            }
        }

        animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        stateTime = 0f;
    }

    /**
     * Updates the animation state.
     */
    public void update(float delta) {
        stateTime += delta;
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime);

        com.badlogic.gdx.math.Matrix4 oldProjection = batch.getProjectionMatrix().cpy();

        batch.setProjectionMatrix(new com.badlogic.gdx.math.Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        batch.begin();
        batch.draw(currentFrame, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        batch.setProjectionMatrix(oldProjection);
    }

    @Override
    public void dispose() {
        spriteSheet.dispose();
    }
}
