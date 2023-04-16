package controleurs;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import dao.IngredientDAOList;
import java.util.Arrays;
import dto.Ingredients;
import java.util.List;

@WebServlet("/ingredients/*")
public class IngredientRestAPI extends HttpServlet {

    IngredientDAOList dao = new IngredientDAOList();

    public void doGet(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
    res.setContentType("application/json;charset=UTF-8");
    PrintWriter out = res.getWriter();
    ObjectMapper objectMapper = new ObjectMapper();
    String info = req.getPathInfo();
    if (info == null || info.equals("/")) {
        List<Ingredients> ingr = dao.findAll();
        String jsonstring = objectMapper.writeValueAsString(ingr);
        out.print(jsonstring+"\n");
        return;
    }
    String[] splits = info.split("/");

    if (splits.length != 2 && (splits.length != 3 || (splits.length == 3 && !splits[2].equals("name")))) {
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
    if(splits.length == 3){
        Ingredients ing = dao.findById(Integer.parseInt(id));
        out.print(objectMapper.writeValueAsString(ing.getName()));
    }
    return;
}
    
    public void doPost(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        res.setContentType("application/json;charset=UTF-8");
        PrintWriter out = res.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        String data = new BufferedReader(new InputStreamReader(req.getInputStream())).readLine();
        Ingredients ingredient = objectMapper.readValue(data, Ingredients.class);
		if (dao.findById(ingredient.getId()) != null || dao.findByName(ingredient.getName()) != null ) {
			System.out.println("Already exist");
			res.sendError(HttpServletResponse.SC_CONFLICT);
			return;
		}
        dao.save(ingredient);
        out.close();
        }
        public void doDelete(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
            res.setContentType("application/json;charset=UTF-8");
            PrintWriter out = res.getWriter();
            String info = req.getPathInfo();
            String[] splits = info.split("/");
            System.out.println(Arrays.toString(splits));
            if (splits.length != 2) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }else{
                String id = splits[1];
                Ingredients ing = dao.findById(Integer.parseInt(id));
                dao.delete(ing.getId());
                out.close();
            }
        }
}
    

