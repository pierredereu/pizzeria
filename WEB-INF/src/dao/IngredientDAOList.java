package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.DriverManager;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import dto.Ingredients;

public class IngredientDAOList {
	private Connection con;

	public IngredientDAOList(Connection con) {
		this.con = con;
	}

	public IngredientDAOList() {
		try{
			Class.forName("org.postgresql.Driver");
			this.con = DriverManager.getConnection("jdbc:postgresql://psqlserv/but2", "bastienleleuetu", "moi");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public Ingredients findByName(String name) {
		Ingredients ingredient = null;
		try {
			String query = "SELECT * FROM ingredients WHERE name = ?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ingredient = new Ingredients(rs.getInt("id"), name,rs.getFloat("prix"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ingredient;
	}
	
	public Ingredients findById(int id) {
		Ingredients ingredient = null;
		try {
			String query = "SELECT * FROM ingredients WHERE id = ?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ingredient = new Ingredients(id, rs.getString("name"), rs.getFloat("prix"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ingredient;
	}

	public List<Ingredients> findAll() {
		List<Ingredients> list = new ArrayList<>();
		String query = "SELECT * FROM ingredients";
		String id, name, prix;
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				id = rs.getString(1);
				name = rs.getString(2);
				prix = rs.getString(3);

				list.add(new Ingredients(Integer.parseInt(id), name,Double.parseDouble(prix)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public void save(Ingredients ingredient) {
		try {
			String query = "INSERT INTO ingredients VALUES( ? , ? , ?)";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, ingredient.getId());
			ps.setString(2, ingredient.getName());
			ps.setDouble(3,ingredient.getPrix());
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void delete(int id) {
		try {
			String query = "DELETE FROM ingredients WHERE id=?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1,id);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}