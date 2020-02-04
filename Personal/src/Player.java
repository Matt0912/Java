import java.awt.Graphics;
import java.awt.Color;
import java.lang.Math;
import java.awt.Rectangle;

public class Player extends GameObject {
    protected int playerWidth = 50, playerHeight = 50;
    Handler handler;

    public Player(int x, int y, ID id, Handler handler) {
        super(x, y, id);
        this.handler = handler;

    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, playerWidth, playerHeight);
    }

    public void tick() {
        x += velX;
        y += velY;

        x = Game.clamp(x, 0, Game.WIDTH - (int)Math.round(playerWidth*1.32));
        y = Game.clamp(y, 0, Game.HEIGHT - (int)Math.round(playerHeight*1.8));

        collision();

    }

    private void collision() {
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject tempObject = handler.object.get(i);

            if (tempObject.getId() == ID.BasicEnemy) {
                if (getBounds().intersects(tempObject.getBounds())) {
                    //Collision code
                    HUD.HEALTH -= 2;
                }
            }
        }
    }



    public void render(Graphics g) {
        if (id == ID.Player) {
            g.setColor(Color.white);
        }
        g.fillRect(x, y, playerWidth, playerHeight);
    }

}