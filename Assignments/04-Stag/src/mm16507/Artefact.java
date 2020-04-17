public class Artefact extends Entity {
    private String name;
    private String description;

    public Artefact(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }

}
