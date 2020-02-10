import java.awt.Graphics;
import java.awt.Color;
import java.lang.Math;
import java.awt.Rectangle;

public class FastEnemy extends GameObject {
    protected int enemyWidth = 15, enemyHeight = 15;
    private Handler handler;

    public FastEnemy(int x, int y, ID id, Handler handler) {
        super(x, y, id);

        this.handler = handler;

        velX = 8;
        velY = 8;

    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, enemyWidth, enemyHeight);
    }

    public void tick() {
        x += velX;
        y += velY;

        if (x <= 0 || x >= Game.WIDTH -  45) velX *= -1;
        if (y <= 0 || y >= Game.HEIGHT - 70) velY *= -1;

        handler.addObject(new Trail((int)x, (int)y, ID.Trail, Color.pink, enemyWidth, enemyHeight, 0.08f, handler));

    }

    public void render(Graphics g) {
        g.setColor(Color.pink);
        g.fillRect((int)x, (int)y, enemyWidth, enemyHeight);

    }
}