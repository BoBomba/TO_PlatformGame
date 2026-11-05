import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CharacterCollisionTest {
    @Test
    public void landsOnSafePlatformEvenWhenLevelContainsHazard() {
        Character character = new Character(10, 60, 20, 20);
        character.yVelocity = 20;
        Platform safePlatform = new Platform(0, 100, 100, 10);
        Platform hazard = new KillingPlatformDecorator(new Platform(200, 80, 20, 20));

        character.update(Arrays.asList(safePlatform, hazard));

        assertEquals(80, character.y);
        assertEquals(0, character.yVelocity);
        assertTrue(character.onGround);
    }
}
