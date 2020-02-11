import java.lang.Math;

public class Vector2D {
    private double x;
    private double y;

    public void storeVector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    double distance(Vector2D v) {
        double yDist = Math.abs(v.y - this.y);
        double xDist = Math.abs(v.x - this.x);

        return Math.hypot(yDist, xDist);
    }

    Vector2D add(Vector2D v) {
        this.x = this.x + v.x;
        this.y = this.y + v.y;

        return this;

    }

    Vector2D scale(double f) {
        this.x = this.x * f;
        this.y = this.y * f;

        return this;

    }

    public static void main(String[] args) {
        Vector2D vector1 = new Vector2D();
        Vector2D vector2 = new Vector2D();

        vector1.storeVector(3, 3);
        vector2.storeVector(5, 6);
        double scaleFactor = 3;

        System.out.println("Distance = " + vector1.distance(vector2));
        vector1 = vector1.add(vector2);
        System.out.println("Sum = " + vector1.x + ", " + vector1.y);
        System.out.println("Vector 1 scaled by " + scaleFactor +
                            " = " + vector1.scale(scaleFactor));


    }

}