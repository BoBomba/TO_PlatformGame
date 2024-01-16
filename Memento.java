public class Memento {
    private final int initialStateX;
    private final int initialStateY;

    public Memento(int x, int y) {
        this.initialStateX = x;
        this.initialStateY = y;
    }

    public int getInitialStateX() {
        return initialStateX;
    }

    public int getInitialStateY() {
        return initialStateY;
    }
}
