import java.util.Random;

public class Spawn {

    private Handler handler;
    private HUD hud;
    private Random r = new Random();

    public Spawn(Handler handler, HUD hud) {
        this.handler = handler;
        this.hud = hud;

    }

    public void tick() {
        if (hud.getScore() % 200 == 0)
            hud.setLevel(hud.getLevel() + 1);{
            handler.addObject(new BasicEnemy(r.nextInt(Game.WIDTH - 60),r.nextInt(Game.HEIGHT/2), ID.BasicEnemy, handler));

            if (hud.getLevel() == 3) {
                handler.addObject(new FastEnemy(r.nextInt(Game.WIDTH - 60),r.nextInt(Game.HEIGHT/2), ID.FastEnemy, handler));

            }

        }
    }
}