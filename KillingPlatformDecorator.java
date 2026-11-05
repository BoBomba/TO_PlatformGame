import java.awt.Color;
import java.awt.Graphics;

public class KillingPlatformDecorator extends Platform {
    public KillingPlatformDecorator(Platform platform) {
        super(platform.x, platform.y, platform.width, platform.height);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
    }

    @Override
    public boolean isLethal() {
        return true;
    }

    public static void resetCharacter(Character character, Memento savedState) {
        character.getStateFromMemento(savedState);
    }
}
