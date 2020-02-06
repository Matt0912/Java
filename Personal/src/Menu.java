import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class Menu extends MouseAdapter{

    private Game game;
    private Handler handler;
    private Random r = new Random();

    public Menu(Game game, Handler handler) {
        this.game = game;
        this.handler = handler;
    }

    public void mousePressed(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        if (game.gameState == Game.STATE.StartMenu)  {
            // Play Button
            if (mouseOver(mx, my, Game.WIDTH/2-120, 150, 200, 64)) {
                game.gameState = Game.STATE.Game;
                handler.addObject(new Player(Game.WIDTH/2-50, Game. HEIGHT/2-50, ID.Player, handler));
                handler.addObject(new BasicEnemy(r.nextInt(Game.WIDTH - 60),r.nextInt(Game.HEIGHT/2), ID.BasicEnemy, handler));
            }
            // Quit Button
            if (mouseOver(mx, my, Game.WIDTH/2-120, 250, 200, 64)) {
                System.exit(0);
            }
        }
        if (game.gameState == Game.STATE.PauseMenu) {
            // Resume Button
            if (mouseOver(mx, my, Game.WIDTH/2-120, 150, 200, 64)) {
                game.gameState = Game.STATE.Game;
            }
            // Quit Button
            if (mouseOver(mx, my, Game.WIDTH/2-120, 250, 200, 64)) {
                System.exit(0);
            }
        }
    }

    public void mouseReleased(MouseEvent e) {

    }

    private boolean mouseOver(int mx, int my, int x, int y, int width, int height) {
        if (mx > x && mx < x + width) {
            if (my > y && my < y + height) {
                return true;
            }
        }
        return false;

    }

    public void tick() {

    }

    public void renderStart(Graphics g) {
        Font fnt1 = new Font("arial", 1, 50);
        Font fnt2 = new Font("arial", 1, 30);

        g.setColor(Color.gray);
        g.fillRect(Game.WIDTH/2-120, 150, 200, 64);
        g.fillRect(Game.WIDTH/2-120, 250, 200, 64);

        g.setFont(fnt1);
        g.setColor(Color.white);
        g.drawString("MENU", Game.WIDTH/2-90, 100);

        g.setFont(fnt2);
        g.drawString("PLAY", Game.WIDTH/2-60, 195);
        g.drawString("QUIT", Game.WIDTH/2-60, 295);

    }

    public void renderPause(Graphics g) {
        Font fnt1 = new Font("arial", 1, 50);
        Font fnt2 = new Font("arial", 1, 30);

        g.setColor(Color.gray);
        g.fillRect(Game.WIDTH/2-120, 150, 200, 64);
        g.fillRect(Game.WIDTH/2-120, 250, 200, 64);

        g.setFont(fnt1);
        g.setColor(Color.white);
        g.drawString("MENU", Game.WIDTH/2-90, 100);

        g.setFont(fnt2);
        g.drawString("RESUME", Game.WIDTH/2-85, 195);
        g.drawString("QUIT", Game.WIDTH/2-60, 295);

    }

}