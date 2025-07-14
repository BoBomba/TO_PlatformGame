import org.junit.Test;
import static org.junit.Assert.*;

public class CharacterMementoTest {
    @Test
    public void testSaveAndRestoreState() {
        Character character = new Character(10, 20, 30, 40);
        Memento saved = character.saveStateToMemento();
        character.x = 50;
        character.y = 60;
        character.getStateFromMemento(saved);
        assertEquals(10, character.x);
        assertEquals(20, character.y);
    }
}
