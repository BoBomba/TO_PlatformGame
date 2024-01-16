import java.awt.Color;
import java.awt.Graphics;

public class Point {
    int x, y;
    int radius = 10;
    Color color = Color.GREEN;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
    }

    public boolean checkCollision(int x, int y) {
        int distanceX = Math.abs(x - this.x); // Odległość w osi X
        int distanceY = Math.abs(y - this.y); // Odległość w osi Y

        // Prosty test kolizji: czy odległość między środkami jest mniejsza niż suma promieni
        int radiusSum = this.radius;
        return distanceX <= radiusSum && distanceY <= radiusSum;
    }
}
