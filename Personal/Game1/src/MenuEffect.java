import java.awt.Graphics;
import java.awt.Color;
import java.lang.Math;
import java.awt.Rectangle;
import java.util.Random;

public class MenuEffect extends GameObject {
    protected int enemyWidth = 15, enemyHeight = 15;
    private Handler handler;

    Random r = new Random();
    private int red = r.nextInt(255);
    private int green = r.nextInt(255);
    private int blue = r.nextInt(255);
    private Color colour;
    private int timer = 0;

    public MenuEffect(int x, int y, ID id, Handler handler) {
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

        if (timer % 60 == 0) {
            red = r.nextInt(255);
            green = r.nextInt(255);
            blue = r.nextInt(255);
            colour = new Color(red, green, blue);
        }
        timer++;

        handler.addObject(new Trail((int)x, (int)y, ID.Trail, colour, enemyWidth, enemyHeight, 0.08f, handler));

    }

    public void render(Graphics g) {
        g.setColor(colour);
        g.fillRect((int)x, (int)y, enemyWidth, enemyHeight);

    }
}