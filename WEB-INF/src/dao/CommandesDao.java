package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import dto.Commandes;
import dto.Ingredients;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.time.LocalDate;
import dto.Pizzas;
import java.sql.Date;
import dto.Pates;

public class CommandesDao {

	private Connection con;
    private PizzasDAO dao =new PizzasDAO() ;

	public CommandesDao(Connection con) {
		this.con = con;
	}

	public CommandesDao() {
		try{
			Class.forName("org.postgresql.Driver");
			this.con = DriverManager.getConnection("jdbc:postgresql://psqlserv/but2", "bastienleleuetu", "moi");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public Commandes findById(int id) {
		Commandes commande = null;
		ArrayList<Pizzas> pizzas = new ArrayList<Pizzas>();
		try {
			String queryCommande = "SELECT * FROM commandes WHERE cid = ? ";
			String queryPizzas = "SELECT * FROM commandesfinal JOIN pizzasvide ON commandesfinal.pid = pizzasvide.pid"
					+ " WHERE commandesfinal.cid = ? ";
			PreparedStatement ps = con.prepareStatement(queryCommande);
			PreparedStatement psPizzas = con.prepareStatement(queryPizzas);
			ps.setInt(1,id);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				psPizzas.setInt(1,id);
				ResultSet rsPizzas = psPizzas.executeQuery();
				while (rsPizzas.next()) {
					pizzas.add(dao.findById(rsPizzas.getInt("pid")));
				}
				commande = new Commandes(id,rs.getInt("uid"),rs.getString("date"),pizzas);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return commande;
	}

	public List<Commandes> findAll() {
		ArrayList<Commandes> list = new ArrayList<Commandes>();
        String queryCommande = "SELECT * FROM commandes";
        String queryPizzas = "SELECT * FROM commandesfinal JOIN pizzasvide ON commandesfinal.pid = pizzasvide.pid WHERE commandesfinal.cid = ?";
		int id;
        int iduser;
		String date;
		ArrayList<Pizzas> pizzas;
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(queryCommande);
			PreparedStatement psPizzas = con.prepareStatement(queryPizzas);
			while(rs.next()) {
				id = rs.getInt(1);
				iduser = rs.getInt(2);
				date = rs.getString(3);
				pizzas =  new ArrayList<Pizzas>();
				psPizzas.setInt(1,id);
				ResultSet rsPizzas = psPizzas.executeQuery();
				while (rsPizzas.next()) {
					pizzas.add(dao.findById(rsPizzas.getInt("pid")));
				}
				list.add(new Commandes(id, iduser,date,pizzas));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
    public void saveCommande(Commandes commande) {
		try {
			String query="INSERT INTO commandes VALUES(  ? , ? , ? )";
			String queryPizzas="INSERT INTO commandesfinal VALUES( ? , ? )";
			PreparedStatement ps = con.prepareStatement(query);
			PreparedStatement psPizzas = con.prepareStatement(queryPizzas);
			ps.setInt(1, commande.getId());
			ps.setInt(2,commande.getIduser());
            LocalDate date = LocalDate.parse(commande.getDate(), DateTimeFormatter.ISO_DATE);
			ps.setDate(3,java.sql.Date.valueOf(date));
			ps.executeUpdate();
			for(Pizzas pizza: commande.getPizzas()) {
				psPizzas.setInt(1, commande.getId());
				psPizzas.setInt(2, pizza.getId());
				psPizzas.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    public void addPizza(int id, int pid) {
		try {
			String query = "INSERT INTO commandesfinal VALUES ( ? , ? )";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, id);
			ps.setInt(2, pid);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
