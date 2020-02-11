public class Critter {
    private String name = null;

    public void setName(String name) {
        this.name = name;
    }

    public void poke() {
        if (this.name == null) {
            System.out.println("I was poked");
        }
        else {
            System.out.println(this.name + " was poked");
        }
    }

    public void eat(Critter c) {
        System.out.println(this.name + " has eaten " + c.name + "!");
    }

    public static void main(String[]args){
        Critter critter1 = new Critter();
        critter1.poke();
        System.out.print("Enter a critter name: ");
        critter1.setName(System.console().readLine());
        critter1.poke();

        Critter critter2 = new Critter();
        critter2.setName("Cat");
        critter1.eat(critter2);
    }

}

