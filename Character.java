import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class Character {
    int x, y;
    int width, height;
    int xVelocity, yVelocity; //strategia ??
    boolean onGround;

    private boolean movingLeft = false;
    private boolean movingRight = false;
    private boolean jumping = false;

    Color color = Color.pink;



    public Character(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.xVelocity = 0;
        this.yVelocity = 0;
        this.onGround = false;
    }

    public void update(List<Platform> platforms) {

        if (movingLeft) {
            xVelocity = -5;
        } else if (movingRight) {
            xVelocity = 5;
        } else {
            xVelocity = 0;
        }

        if (jumping && onGround) {
            yVelocity = -15; // Wartość skoku
            onGround = false;
        }

        x += xVelocity;
        y += yVelocity;

        int potentialX = x + xVelocity;
        int potentialY = y + yVelocity;
        boolean collided = false;

        for (Platform platform : platforms) {
            if (checkCollision(potentialX, potentialY, platform)) {
                if (!(platform instanceof KillingPlatformDecorator)) {
                    collided = true;
                    yVelocity = 0;
                    if (potentialY > y) { // Postać spada na platformę
                        y = platform.y - height;
                    }
                    break;
                }

            }
        }

        // Ograniczenia ekranu
        if (x < 0) x = 0;
        if (y < 0) y = 0;

        if (!collided) {
            yVelocity += 1; // Grawitacja
            onGround = false;
        } else {
            onGround = true;
        }
    }

    public boolean checkCollision(int potentialX, int potentialY, Platform platform) {
        boolean collisionX = potentialX < platform.x + platform.width && potentialX + width > platform.x;
        boolean collisionY = potentialY < platform.y + platform.height && potentialY + height > platform.y;

        return collisionX && collisionY;
    }


    public Memento saveStateToMemento() {
        return new Memento(this.x, this.y);
    }

    public void getStateFromMemento(Memento memento) {
        x = memento.getInitialStateX();
        y = memento.getInitialStateY();
        this.xVelocity = 0;
        this.yVelocity = 0;
        this.onGround = false;
    }

//    public void moveLeft() {
//        xVelocity = -5;
//    }
//
//    public void moveRight() {
//        xVelocity = 5;
//    }
//
//    public void stop() {
//        xVelocity = 0;
//    }
//
//    public void jump() {
//        if (onGround) {
//            yVelocity = -15; // Wartość skoku
//            onGround = false;
//        }
//    }

    public void moveLeft() {
        movingLeft = true;
    }

    public void moveRight() {
        movingRight = true;
    }

    public void stop() {
        movingLeft = false;
        movingRight = false;
    }

    public void jump() {
        jumping = true;
    }

    // Wywoływane, gdy klawisz skoku zostanie zwolniony
    public void stopJump() {
        jumping = false;
    }

}

