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

        // Prosty test kolizji
        int radiusSum = this.radius;
        return distanceX <= radiusSum && distanceY <= radiusSum;
    }

    public boolean checkCollision(Character character) {

        int centerX = character.x + character.width / 2; // Środek postaci w osi X
        int centerY = character.y + character.height / 2; // Środek postaci w osi Y

        int distanceX = Math.abs(centerX - this.x); // Odległość w osi X
        int distanceY = Math.abs(centerY - this.y); // Odległość w osi Y

        // Prosty test kolizji
        int radiusSum = this.radius + Math.min(character.width, character.height) / 2;
        return distanceX <= radiusSum && distanceY <= radiusSum;
    }

    public static boolean checkCollision(Character character, Point point) {
        int centerX = character.x + character.width / 2; // Środek postaci w osi X
        int centerY = character.y + character.height / 2; // Środek postaci w osi Y

        int distanceX = Math.abs(centerX - point.x); // Odległość w osi X
        int distanceY = Math.abs(centerY - point.y); // Odległość w osi Y

        // Prosty test kolizji: czy odległość między środkami jest mniejsza niż suma promieni
        int radiusSum = point.radius + Math.min(character.width, character.height) / 2;
        return distanceX <= radiusSum && distanceY <= radiusSum;
    }
}
