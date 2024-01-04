import javax.swing.*;
import java.awt.*;
import java.util.Scanner;
private static class GameWindow extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Rysuj planszę gry
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                int squareSize = 30;
                int xOffset = 50;
                int yOffset = 50;

                if (grid[x][y].equals("@")) {
                    g.setColor(Color.BLUE);
                    g.fillRect(y * squareSize + xOffset, x * squareSize + yOffset, squareSize, squareSize);
                } else if (grid[x][y].equals("x")) {
                    g.setColor(Color.RED);
                    g.fillRect(y * squareSize + xOffset, x * squareSize + yOffset, squareSize, squareSize);
                } else if (grid[x][y].equals("!")) {
                    g.setColor(Color.GREEN);
                    g.fillRect(y * squareSize + xOffset, x * squareSize + yOffset, squareSize, squareSize);
                } else if (grid[x][y].equals("-")) {
                    g.setColor(Color.GRAY);
                    g.fillRect(y * squareSize + xOffset, x * squareSize + yOffset, squareSize, squareSize);
                }
                g.setColor(Color.BLACK);
                g.drawRect(y * squareSize + xOffset, x * squareSize + yOffset, squareSize, squareSize);
            }
        }
    }
}

// Reszta kodu pozostaje bez zmian
}