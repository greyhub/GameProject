package game.player;

import Screen.GameOverScreen;
import Screen.SceneManager;
import bases.*;
import game.Platform;
import game.playerBullet.PlayerBullet;
import tklibs.Mathx;

import java.awt.*;

public class Player01 extends GameObject {
    public int hp;
    public boolean immune;
    private final float GRAVITY = 0.4f;
    private final float JUMPSPEED = 10f;
    public String name;

    public Player01() {
        this(100, 100);
    }

    public Player01(int a, int b) {
        renderer = new Renderer("assets/images/players/straight");
        position.set(a, b);
        hitBox = new BoxCollider(this, Settings.PLAYER_WIDTH - 12, Settings.PLAYER_HEIGHT - 8);
        hp = 3;
    }

    public void takeDamage(int damage) {
        hp -= damage;
        if (hp <= 0) {
            hp = 0;
            this.deactive();
        }
    }

    @Override
    public void run() {
        super.run();
        this.move();
        this.limitPosition();
        this.fire();
        this.checkImmune();
    }

    @Override
    public void render(Graphics g) {
        if (immune) {
            if (immuneCount % 2 == 0) {
                super.render(g);
            }
        } else {
            super.render(g);
        }
    }

    int immuneCount = 0;
    private void checkImmune() {
        if (immune) {
            immuneCount++;
            if(immuneCount > 120) {
                immune = false;
                immuneCount = 0;
            }
        }
    }

    private void limitPosition() {
        // player limit
        int halfWidthPlayer = Settings.PLAYER_WIDTH / 2;
        int halfHeightPlayer = Settings.PLAYER_HEIGHT / 2;
        position.x = Mathx.clamp(position.x, halfWidthPlayer, Settings.BACKGROUND_WIDTH - halfWidthPlayer);
        position.y = Mathx.clamp(position.y, halfHeightPlayer, Settings.GAME_HEIGHT - halfHeightPlayer + 50);
    }

    private void move() {
        // player move
        isTouchingGround = false;
        keyMove();
        freeFall();
        moveVertical();
        moveLeft();
        moveRight();
        moveDown();
        jump();
    }

    private boolean isJumping ;
    private boolean isTouchingGround;
    private boolean isPressing;
    private boolean isTurnLeft;

    private void freeFall() {
        this.velocity.y += GRAVITY;
    }

    private void moveLeft() {
        if (isLeft) {
            position.x -= 3;
            isTurnLeft = true;
        }
    }

    private void moveRight() {
        if (isRight) {
            position.x += 3;
            isTurnLeft = false;
        }
    }

    private void moveDown() {
        if (isDown && isTouchingGround) {
            freeFall();
        }
    }

    private void jump() {
        if (isUp && isTouchingGround) {
            isJumping = true;
            velocity.y -= JUMPSPEED;
        }
        else isJumping = false;
    }

    private void moveVertical() {
        Vector2D nextPosition = this.screenPosition.clone();
        nextPosition.add(0, velocity.y);
        BoxCollider nextHitBox = new BoxCollider(
                nextPosition.x,
                nextPosition.y,
                this.hitBox.width,
                this.hitBox.height
        );

        Platform platform = GameObject.findHitBoxIntersects(nextHitBox);
        if (platform != null) {
            while(!this.hitBox.intersects(platform.hitBox)) {
                this.position.add(0, Math.signum(velocity.y));
            }
            Platform currentIntersects = GameObject.findIntersects(Platform.class, this);
//            if(!(currentIntersects != null && new Double(this.hitBox.bot()).intValue() > new Double(currentIntersects.hitBox.top()).intValue())) {
//                velocity.y = 0;
//                isTouchingGround = true;
//            }
            if(!(currentIntersects != null && (int) this.hitBox.bot() > (int) currentIntersects.hitBox.top() )) {
                velocity.y = 0;
                isTouchingGround = true;
            }
        }
    }

    int frameCount = 0;

    public void fire() {
        // player fire
        frameCount++;
        int velocityChange;
        if (isTurnLeft) {
            velocityChange = -10;
        }
        else {
            velocityChange = 10;
        }

        if (isFire && frameCount > 10) {
            PlayerBullet bullet = new PlayerBullet();
            bullet.position.set(position.x, position.y);
            bullet.velocity.set(velocityChange, 0);
            frameCount = 0;
        }
    }

    public boolean isUp;
    public boolean isDown;
    public boolean isLeft;
    public boolean isRight;
    public boolean isFire;
    public void keyMove() {
        isUp = KeyEventPress.isUpPress;
        isDown = KeyEventPress.isDownPress;
        isLeft = KeyEventPress.isLeftPress;
        isRight = KeyEventPress.isRightPress;
        isFire = KeyEventPress.isFirePress;
    }
    @Override
    public void deactive() {
        super.deactive();
        SceneManager.signNewScreen(new GameOverScreen());
    }
}
