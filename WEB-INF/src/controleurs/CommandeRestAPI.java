package controleurs;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import dao.IngredientDAOList;
import java.util.Arrays;
import dto.Ingredients;
import dto.Pizzas;
import dto.Commandes;
import java.util.List;
import dao.PizzasDAO;
import dao.CommandesDao;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParseException;

@WebServlet("/commandes/*")
public class CommandeRestAPI extends HttpServlet {
    CommandesDao dao =new CommandesDao();
    PizzasDAO daoPizz = new PizzasDAO();

    public void doGet(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
    res.setContentType("application/json;charset=UTF-8");
    PrintWriter out = res.getWriter();
    ObjectMapper objectMapper = new ObjectMapper();
    String info = req.getPathInfo();
    if (info == null || info.equals("/")) {
        System.out.println("pathinfo == null || /");
        List<Commandes> commandes = dao.findAll();
        String jsonstring = objectMapper.writeValueAsString(commandes);
        out.print(jsonstring+"\n");
        return;
    }
    String[] splits = info.split("/");

    if (splits.length != 2 && (splits.length != 3 || (splits.length == 3 && !splits[2].equals("prixfinal")))) {
        System.out.println("bad parameter");
        res.sendError(HttpServletResponse.SC_BAD_REQUEST);
        return;
    }
    
    String id = splits[1];
    if (dao.findById(Integer.parseInt(id))==null) {
        System.out.println("no id");
        res.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
    }
    if(splits.length == 2) {
        out.print(objectMapper.writeValueAsString(dao.findById(Integer.parseInt(id))));
    }
    String choix = splits[2];
    if(splits.length == 3){
        if(choix.equals("prixfinal")){
            String jsonstring = objectMapper.writeValueAsString(dao.findById(Integer.parseInt(id)).prixFinal());
            out.print(jsonstring+"\n");
        }else{
            try{ 
                Integer.parseInt(choix); 
            } catch(NumberFormatException nfe){
                System.out.println(nfe.getMessage()) ;
            }
        }
    }
    return;
}

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
        res.setContentType("applications/json");
        
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            System.out.println("pathinfo == null || /");
        	String data = new BufferedReader(new InputStreamReader(req.getInputStream())).readLine();
            System.out.println(data);
            System.out.println("data read");
            Commandes commande = null;
            try{
            	commande = objectMapper.readValue(data, Commandes.class);
                System.out.println("obj mapper commande");
            } catch (JsonParseException e) {
            	System.out.println(e.getMessage());
            }
            if(dao.findById(commande.getId()) != null) {
                System.out.println("existe pas");
            	res.sendError(HttpServletResponse.SC_CONFLICT);
            	return;
            }
            System.out.println("existe");
            dao.saveCommande(commande);
            out.print(data);
        	return;
        }
        String[] splits = pathInfo.split("/");
        if (splits.length != 2) {
	        res.sendError(HttpServletResponse.SC_BAD_REQUEST);
	        return;
        }
        
        String id = splits[1];
        try {
        	Integer.parseInt(id);
        } catch (Exception e) {
        	res.sendError(HttpServletResponse.SC_BAD_REQUEST);
	        return;
        }
		if (dao.findById(Integer.parseInt(id))==null) {
			System.out.println(id);
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		String data = new BufferedReader(new InputStreamReader(req.getInputStream())).readLine();
		Pizzas pizza = objectMapper.readValue(data, Pizzas.class);
		int IDpizz = pizza.getId();
		if (daoPizz.findById(IDpizz)==null) {
			System.out.println(IDpizz);
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		dao.addPizza(Integer.parseInt(id), IDpizz);
		out.close();
	} 
}

    


