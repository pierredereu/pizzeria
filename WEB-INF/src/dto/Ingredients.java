package dto;

public class Ingredients {
    protected int id;
    protected String name;
    protected double prix;

    public Ingredients(int id, String name,double prix){
        this.id = id;
        this.name = name;
        this.prix = prix;
    }
    public Ingredients(){
        new Ingredients(0,null,0);
    }

    
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public double getPrix(){
        return prix;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPrix(double prix) {
        this.prix = prix;
    }

}
