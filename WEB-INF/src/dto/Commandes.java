package dto;
import java.util.List;
public class Commandes {
    protected int id;
    protected int iduser;
    protected String date;
    protected List<Pizzas> pizzas;

    public Commandes(int id,int iduser, String date,List<Pizzas> pizzas){
        this.id = id;
        this.iduser=iduser;
        this.date = date;
        this.pizzas = pizzas;
    }
    public Commandes(){
        new Commandes(0,0,null,null);
    }

    public int getId(){
        return id;
    }
    public int getIduser(){
        return iduser;
    }
    public List<Pizzas> getPizzas(){
        return pizzas;
    }
    public String getDate(){
        return date;
    }

    public double prixFinal(){
        double prix=0;
        for(int i=0;i<pizzas.size();i=i+1){
            prix= prix +pizzas.get(i).prixFinal();
        }
        return prix;
    }
}
