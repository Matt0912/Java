public class IntBox {
    private int x;

    // Constructor function
    public IntBox(int x) {
        this.x = x;
    }

    // Copy constructor
    IntBox(IntBox original) {
        this.x = original.x;
    }

    IntBox add(int v) {
        this.x = this.x + v;
        return this;
    }

    IntBox subtract(int v){
        this.x = this.x - v;
        return this;
    }

    IntBox scale(int v) {
        this.x = this.x * v;
        return this;
    }

    public String toString() {
        return "IntBox(" + this.x + ")";
    }

    public static void main(String[] args) {
        IntBox a = new IntBox(10).add(10);
        System.out.println(a.toString()); // "IntBox(20)"
        IntBox b = new IntBox(a);
        System.out.println(a.scale(10).toString()); // "IntBox(200)"
        System.out.println(a.toString()); // "IntBox(200)"
        System.out.println(b.toString()); // "IntBox(20)"

    }

}