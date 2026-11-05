import java.util.List;

public class Character {
    public static final int DEFAULT_WIDTH = 30;
    public static final int MOVE_SPEED = 5;
    public static final int JUMP_VELOCITY = -15;
    public static final int GRAVITY = 1;

    public static int maximumJumpHeight() {
        int velocity = JUMP_VELOCITY;
        int displacement = 0;
        int height = 0;
        while (velocity < 0) {
            displacement += velocity;
            height = Math.max(height, -displacement);
            velocity += GRAVITY;
        }
        return height;
    }

    int x, y;
    int width, height;
    int xVelocity, yVelocity;
    boolean onGround;

    private boolean movingLeft = false;
    private boolean movingRight = false;
    private boolean jumping = false;

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
            xVelocity = -MOVE_SPEED;
        } else if (movingRight) {
            xVelocity = MOVE_SPEED;
        } else {
            xVelocity = 0;
        }

        if (jumping && onGround) {
            yVelocity = JUMP_VELOCITY;
            onGround = false;
        }

        int previousBottom = y + height;
        x += xVelocity;
        y += yVelocity;

        boolean landed = false;
        if (yVelocity >= 0) {
            for (Platform platform : platforms) {
                if (!platform.isLethal()
                        && previousBottom <= platform.y
                        && y + height >= platform.y
                        && x < platform.x + platform.width
                        && x + width > platform.x) {
                    y = platform.y - height;
                    yVelocity = 0;
                    landed = true;
                    break;
                }
            }
        }

        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
            yVelocity = 0;
        }

        if (!landed) {
            yVelocity += GRAVITY;
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

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        this.xVelocity = 0;
        this.yVelocity = 0;
        this.onGround = false;
    }

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

    public void stopJump() {
        jumping = false;
    }
}
