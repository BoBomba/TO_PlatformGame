import java.awt.Color;
import java.awt.Graphics;

public class KillingPlatformDecorator extends Platform {
    private Platform decoratedPlatform;

    boolean killing = true;

    public KillingPlatformDecorator(Platform decoratedPlatform) {
        super(decoratedPlatform.x, decoratedPlatform.y, decoratedPlatform.width, decoratedPlatform.height);
        this.decoratedPlatform = decoratedPlatform;
    }

    @Override
    public void draw(Graphics g) {
        // Rysowanie platformy w kolorze czerwonym
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
    }

    public static void resetCharacter(Character character, Memento savedState) {
        character.getStateFromMemento(savedState);
    }
}
