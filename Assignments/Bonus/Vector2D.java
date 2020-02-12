import java.lang.Math;
import java.util.Scanner;
import java.util.*;


public class Vector2D {
    private double x;
    private double y;
    protected static int numVectors = 0;
    static Scanner userInput = new Scanner(System.in);

    // Input = true if taking user input, Input = false if taking other input
    public Vector2D(boolean input) {
        numVectors++;
        if (input == true) {
            System.out.println("Enter x,y co-ordinates for vector " + numVectors + ": ");
            try {
                storeVector(userInput.nextDouble(), userInput.nextDouble());
            } catch (Exception e) {
                System.out.println("This doesn't appear to be a number?");
                System.exit(0);
            }
            System.out.println("x = " + this.x + ", y = " + this.y);
        }
        else {
            storeVector(0,0);
        }
    }

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
        Vector2D vector1 = new Vector2D(false);
        vector1.x = this.x + v.x;
        vector1.y = this.y + v.y;

        return vector1;
    }

    Vector2D scale(double f) {
        Vector2D vector1 = new Vector2D(false);
        vector1.x = this.x * f;
        vector1.y = this.y * f;

        return vector1;
    }

    public String toString() {
        return "Vector2D: (" + this.x + ", " + this.y + ")";
    }

    public static void main(String[] args) {
        Vector2D vector1 = new Vector2D(true);
        Vector2D vector2 = new Vector2D(true);
        double scaleFactor = 3;

        System.out.println("Distance = " + vector1.distance(vector2));
        System.out.println("Sum = " + vector1.add(vector2).toString());
        System.out.println("Vector 1 scaled by " + scaleFactor +
                            " = " + vector1.scale(scaleFactor).toString());
        System.out.println("Vector 1 = " + vector1.toString());


    }

}