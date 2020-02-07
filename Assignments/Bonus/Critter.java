
public class Critter {

    public static void main(String[]args){
        String name = System.console().readLine();
        poke(name);
    }

    public void poke(String name) {
        if (name == null) {
            System.out.println("I was poked");
        }
        else {
            System.out.println(name + " was poked");
        }
    }

}

