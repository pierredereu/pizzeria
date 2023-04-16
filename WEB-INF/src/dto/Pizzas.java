package dto;
import java.util.List;

public class Pizzas {
    protected int id;
    protected String name;
    protected Pates type;
    protected List<Ingredients> ingredients;
    protected double prix;

    public Pizzas(int id, String name,double prix,Pates type,List<Ingredients> ingredients){
        this.id = id;
        this.name = name;
        this.prix = prix;
        this.type=type;
        this.ingredients = ingredients;
    }
    public Pizzas(){
        new Pizzas(0,null,0,null,null);
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
    public Pates getType(){
        return type;
    }
    public List<Ingredients> getIngredients(){
        return ingredients;
    }
    public double prixFinal(){
        double prixFinal=0;
        for(int i=0;i<ingredients.size();i=i+1){
            prixFinal=prixFinal+ingredients.get(i).getPrix();
        }
        return prixFinal+this.prix;
    }
}
