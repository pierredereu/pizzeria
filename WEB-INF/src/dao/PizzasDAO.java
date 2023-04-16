package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import dto.Ingredients;
import dto.Pizzas;
import dto.Pates;

public class PizzasDAO {

	private Connection con;

	public PizzasDAO(Connection con) {
		this.con = con;
	}

	public PizzasDAO() {
		try{
			Class.forName("org.postgresql.Driver");
			this.con = DriverManager.getConnection("jdbc:postgresql://psqlserv/but2", "bastienleleuetu", "moi");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public Pizzas findById(int id) {
		Pizzas pizzas = null;
		ArrayList<Ingredients> ingredients = new ArrayList<Ingredients>();
		try {
			String queryPizzas = "SELECT * FROM Pizzasvide WHERE pid = ? ";
			String queryIngredients = "SELECT * FROM Pizzas JOIN ingredients ON Pizzas.idingredient = ingredients.id"
					+ " WHERE pizzas.idPizza = ? ";
			PreparedStatement ps = con.prepareStatement(queryPizzas);
			PreparedStatement psIngredients = con.prepareStatement(queryIngredients);
			ps.setInt(1,id);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				psIngredients.setInt(1,id);
				ResultSet rsIngredients = psIngredients.executeQuery();
				while (rsIngredients.next()) {
					ingredients.add(new Ingredients(Integer.parseInt(rsIngredients.getString("id")),
							rsIngredients.getString("name"),Double.parseDouble(rsIngredients.getString("prix"))));
				}
				pizzas = new Pizzas(id,rs.getString("name"),rs.getDouble("prix"), Pates.valueOf(rs.getString("type").toUpperCase()),ingredients);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return pizzas;
	}

	public List<Pizzas> findAll() {
		ArrayList<Pizzas> list = new ArrayList<Pizzas>();
		String query = "SELECT * FROM Pizzasvide";
		String queryIngredients = "SELECT * FROM pizzas JOIN ingredients ON pizzas.idingredient = ingredients.id"
				+ " WHERE Pizzas.idPizza = ? ";
		int id;
		String name;
		String type;
		double prix;
		ArrayList<Ingredients> ingredients;
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(query);
			PreparedStatement psIngredients = con.prepareStatement(queryIngredients);
			while(rs.next()) {
				id = rs.getInt(1);
				name = rs.getString(2);
				type = rs.getString(3);
				prix = rs.getDouble(4);
				ingredients =  new ArrayList<Ingredients>();
				psIngredients.setInt(1,id);
				ResultSet rsIngredients = psIngredients.executeQuery();
				while (rsIngredients.next()) {
					ingredients.add(new Ingredients(rsIngredients.getInt("id"),
							rsIngredients.getString("name"),rsIngredients.getDouble("prix")));
				}
				list.add(new Pizzas(id, name, prix, Pates.valueOf(type.toUpperCase()), ingredients));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public void saveIngr(int pizzas,Ingredients ingr) {
		try {
			String query = "INSERT INTO pizzas VALUES( ? , ? )";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, pizzas);
			ps.setInt(2, ingr.getId());
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void savePizza(Pizzas pizza) {
		try {
			String query = "INSERT INTO pizzasvide VALUES( ? , ? , ? , ?)";
			String queryIngredients = "INSERT INTO pizzas VALUES( ? , ? )";
			PreparedStatement ps = con.prepareStatement(query);
			PreparedStatement psIngredients = con.prepareStatement(queryIngredients);
			ps.setInt(1, pizza.getId());
			ps.setString(2, pizza.getName());
			ps.setString(3, pizza.getType().name());
			ps.setDouble(4, pizza.getPrix());
			ps.executeUpdate();
			for (Ingredients i : pizza.getIngredients()) {
				psIngredients.setInt(1, pizza.getId());
				psIngredients.setInt(2, i.getId());
				psIngredients.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteIngr(int id) {
		try {
			String query = "DELETE FROM pizzas WHERE idingredient =? ";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void deletePizza(int id) {
		try {
			String query = "DELETE FROM Pizzasvide WHERE pid = ? ";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public boolean ingredientExist(int pid, int iid) {
        try {
            String query = "SELECT FROM pizzas WHERE idPizza = ? AND idingredient= ? ";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, pid);
            ps.setInt(2, iid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

	}
	public void update(int id, double prix) {
		try {
			if (prix == -1)
				return;
			String query = "UPDATE pizzasvide SET prix = ? WHERE pid = ? ";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setDouble(1, prix);
			ps.setInt(2, id);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}