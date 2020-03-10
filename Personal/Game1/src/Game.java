import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.Random;

public class Game extends Canvas implements Runnable {

    private static final long serialVersionUID = 6221414150021413828L;

    public static final int WIDTH = 900, HEIGHT = WIDTH/16 * 9;

    private Thread thread;
    private boolean running = false;

    private HUD hud;
    private Handler handler;
    private Random r;
    private Spawn spawner;
    private Menu menu;

    public enum STATE {
        StartMenu,
        PauseMenu,
        EndMenu,
        Game
    };

    public STATE gameState = STATE.StartMenu;

    public Game() {
        handler = new Handler();
        menu = new Menu(this, handler, hud);
        this.addKeyListener(new KeyInput(this, handler));
        this.addMouseListener(menu);

        new Window(WIDTH, HEIGHT, "Game Test 1", this);

        hud = new HUD();
        spawner = new Spawn(handler, hud);
        r = new Random();

        if (gameState == STATE.StartMenu) {
            for (int i = 0; i < 10; i++) {
                handler.addObject(new MenuEffect(r.nextInt(WIDTH - 60), r.nextInt(HEIGHT/2), ID.MenuEffect, handler));
            }
        }
    }

    public synchronized void start() {
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    public synchronized void stop() {
        try {
            thread.join();
            running = false;
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        this.requestFocus();
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000/amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while(running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >=1) {
                tick();
                delta--;
            }
            if(running) {
                render();
            }
            frames++;

            if(System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                //System.out.println("FPS: "+ frames);
                frames = 0;
            }
        }
        stop();
    }

    private void tick() {
        if (gameState == STATE.Game) {
            handler.tick();
            hud.tick();
            spawner.tick();
        }
        else if (gameState == STATE.PauseMenu || gameState == STATE.EndMenu) {
            menu.tick();
        }
        else if (gameState == STATE.StartMenu) {
            menu.tick();
            handler.tick();
        }

    }

    private void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.white);
        g.drawRect(0, 0, WIDTH-17, HEIGHT-40);

        handler.render(g);

        if (gameState == STATE.Game) {
            hud.render(g);
        }
        else if (gameState == STATE.StartMenu) {
            menu.renderStart(g);
        }
        else if (gameState == STATE.PauseMenu) {
            menu.renderPause(g);
        }
        else if (gameState == STATE.EndMenu) {
            menu.renderEnd(g);
        }
        g.dispose();
        bs.show();
    }

    public static int clamp(int var, int min, int max) {
        if (var >= max)
            return var = max;
        else if (var <= min)
            return var = min;
        else
            return var;
    }

    public static void main(String[] args) {
        new Game();
    }
}