import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

public class HUD {

    public static int HEALTH = 100;
    private int greenValue = 200;
    private int redValue = 0;

    private int score = 0;
    private int level = 1;

    public void tick() {
        HEALTH = Game.clamp(HEALTH, 0, 100);
        greenValue = Game.clamp(greenValue, 0, 255);
        redValue = Game.clamp(redValue, 0, 255);
        greenValue = HEALTH*2;
        redValue = 200 - greenValue;
        score++;
    }

    public void render(Graphics g) {
        Font fnt = new Font("arial", 1, 15);

        g.setColor(Color.gray);
        g.fillRect(15, 15, 200, 32);
        g.setColor(new Color(redValue, greenValue, 0));
        g.fillRect(15, 15, HEALTH * 2, 32);
        g.setColor(Color.white);
        g.drawRect(15, 15, 200, 32);

        g.setFont(fnt);
        g.drawString("Score: " + score, 30, 64);
        g.drawString("Level: " + level, 145, 64);

    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public int getHEALTH() {
        return HEALTH;
    }

}