package folk.sisby.antique_atlas.client.gui.core;

import folk.sisby.antique_atlas.client.texture.Drawable;
import net.minecraft.client.util.math.MatrixStack;


/**
 * A GUI element that follows the mouse cursor and is meant to replace it.
 *
 * @author Hunternif
 */
public class CursorComponent extends Component {

    private Drawable texture;
    private int textureWidth, textureHeight;
    /**
     * Coordinates of the cursor point on the texture.
     */
    private int pointX, pointY;

    /**
     * @param texture texture image file
     * @param width   cursor width
     * @param height  cursor height
     * @param pointX  X of the cursor point on the image
     * @param pointY  Y of the cursor point on the image
     */
    public void setTexture(Drawable texture, int width, int height, int pointX, int pointY) {
        this.texture = texture;
        this.textureWidth = width;
        this.textureHeight = height;
        this.pointX = pointX;
        this.pointY = pointY;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTick) {
        texture.draw(matrices, mouseX - pointX, mouseY - pointY, textureWidth, textureHeight);
    }
}
