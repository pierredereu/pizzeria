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
import java.util.List;
import dao.PizzasDAO;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParseException;

@WebServlet("/pizzas/*")
public class PizzaRestAPI extends HttpServlet {

    PizzasDAO dao = new PizzasDAO();
    IngredientDAOList daoIngr = new IngredientDAOList();

    public void doGet(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
    res.setContentType("application/json;charset=UTF-8");
    PrintWriter out = res.getWriter();
    ObjectMapper objectMapper = new ObjectMapper();
    String info = req.getPathInfo();
    if (info == null || info.equals("/")) {
        List<Pizzas> pizz = dao.findAll();
        String jsonstring = objectMapper.writeValueAsString(pizz);
        out.print(jsonstring+"\n");
        return;
    }
    String[] splits = info.split("/");

    if (splits.length != 2 && (splits.length != 3 || (splits.length == 3 && !splits[2].equals("prixfinal")))) {
        res.sendError(HttpServletResponse.SC_BAD_REQUEST);
        return;
    }
    
    String id = splits[1];
    if (dao.findById(Integer.parseInt(id))==null) {
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
            System.out.println("data read");
            Pizzas Pizza = null;
            try{
            	Pizza = objectMapper.readValue(data, Pizzas.class);
                System.out.println("obj mapper pizza");
            } catch (JsonParseException e) {
            	System.out.println(e.getMessage());
            }
            if(dao.findById(Pizza.getId()) != null) {
            	res.sendError(HttpServletResponse.SC_CONFLICT);
            	return;
            }
            dao.savePizza(Pizza);
            out.print(data);
        	return;
        }
        String[] splits = pathInfo.split("/");
        if (splits.length != 2) {
            System.out.println("length !=2");
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
		Ingredients newIngredient = objectMapper.readValue(data, Ingredients.class);
        System.out.println("ingr mapper");
		int ingredientID = newIngredient.getId();
		if (daoIngr.findById(ingredientID)==null) {
			System.out.println(ingredientID);
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		if(dao.ingredientExist(Integer.parseInt(id), ingredientID)) {
        	res.sendError(HttpServletResponse.SC_CONFLICT);
        	return;
        }
        try{ 
            Integer.parseInt(id);
            dao.saveIngr(Integer.parseInt(id), newIngredient);
            out.print("La donnée a bien été ajoutée !");
        } catch(NumberFormatException nfe){
            System.out.println(nfe.getMessage()) ;
            out.close();
        }
        // curl -X POST http://localhost:8080/pizzaland/pizzas -H "Content-Type: application/json" -d '{"id":300, "name": "TEST", "type": "NAPOLITAINE","prix":10, "ingredients":[{"id":5}, {"id":2}, {"id":8}]}'

	}
        public void doDelete(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
            res.setContentType("application/json;charset=UTF-8");
            PrintWriter out = res.getWriter();
            String info = req.getPathInfo();
            String[] splits = info.split("/");
            if (splits.length > 2) {
                try{ 
                    int idIngr =Integer.parseInt(splits[2]);
                    dao.deleteIngr(idIngr);
                } catch(NumberFormatException nfe){
                    System.out.println(nfe.getMessage()) ;
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            }
            if(splits.length==2){
                System.out.println("else");
                try{ 
                    System.out.println("try2");
                    int id =Integer.parseInt(splits[1]);
                    Pizzas pizza = dao.findById(id);
                    dao.deletePizza(pizza.getId());
                    out.close();
                }catch(NumberFormatException nfe){
                    System.out.println(nfe.getMessage()) ;
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            }
        }
        public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
            if (req.getMethod().equalsIgnoreCase("PATCH")) {
                System.out.println("if");
                doPatch(req, res);
            } else {
                super.service(req, res);
            }
        }

        public void doPatch(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
            PrintWriter out = res.getWriter();
            res.setContentType("applications/json");
            
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                System.out.println("path info null");
                res.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            String[] splits = pathInfo.split("/");
            if (splits.length != 2) {
                System.out.println("!=2");
                res.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            String id = splits[1];
            try {
                Integer.parseInt(id);
            } catch (Exception e) {
                System.out.println("parse int");
                res.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            if (dao.findById(Integer.parseInt(id))==null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            ObjectMapper objectMapper = new ObjectMapper();
            String data = new BufferedReader(new InputStreamReader(req.getInputStream())).readLine();
            System.out.println(data);
            double prix = 1.0;
            try {
                System.out.println("try1");
                double datadouble = Double.parseDouble(data);
                System.out.println("2");
                dao.update(Integer.parseInt(id), datadouble);
            } catch (Exception e) {
                System.out.println("catch");
                System.out.println(e.getMessage());
                res.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            
        }
}
    


