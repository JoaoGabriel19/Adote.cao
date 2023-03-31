package adoteCaoProjetoController;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import adoteCaoProjetoModel.Dao;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Validations validations = new Validations();
	Encrypt encrypt = new Encrypt();
	Dao dao = new Dao();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		configureCors(response);
		String login = request.getParameter("login");
		String password = request.getParameter("password");
		List<Boolean> confirmations = new ArrayList<Boolean>();
		try {
			confirmations = validations.verifyLogin(login, password);
		} catch (NoSuchAlgorithmException | ClassNotFoundException | IOException e) {
			sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, Validations.WRONG_CREDENTIALS);
			e.printStackTrace();
			}
		
		if(confirmations.get(0) && confirmations.get(1)) {
			setSessionAndSendResponse(request, response, login, true);
			}else if(!confirmations.get(0) && confirmations.get(1)) {
				setSessionAndSendResponse(request, response, login, false);
			}else {
				sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, Validations.WRONG_CREDENTIALS);
			}
		}

	private void configureCors(HttpServletResponse response) {
	    response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
	    response.setHeader("Access-Control-Allow-Methods", "GET, POST");
	    response.setHeader("Access-Control-Allow-Headers", "Content-Type");
	    response.setHeader("Access-Control-Max-Age", "86400"); // 24 hours
	}
	private void setSessionAndSendResponse(HttpServletRequest request, HttpServletResponse response, String login, boolean isOng) throws IOException {
		HttpSession session = request.getSession();
		session.setAttribute("login", login);
		session.setAttribute("isLogged", true);
		
		if(isOng) {
			session.setAttribute("isOng", true);
		}else {
			session.setAttribute("isOng", false);
		}
		
		String id = idGenerator();
		String subject = login;
		String jwt = encrypt.createJWT(id, subject, Encrypt.JWT_ISSUER);
		try {
			dao.insertJWT(jwt, isOng, login);
		} catch (ClassNotFoundException | SQLException | IOException e1) {
			sendErrorResponse(response, HttpServletResponse.SC_NOT_IMPLEMENTED, Validations.SERVER_ERROR);
			e1.printStackTrace();
		}
		
		Map<String, Object> sessionData = new HashMap<>();
		sessionData.put("isLogged", session.getAttribute("isLogged"));
		sessionData.put("isOng", session.getAttribute("isOng"));
		sessionData.put("jwt", jwt);
		
		Gson gson = new Gson();
		String json = gson.toJson(sessionData);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		try {
			PrintWriter out = response.getWriter();
			out.print(json);
			out.flush();
			out.close();
		} catch (IOException e) {
			sendErrorResponse(response, HttpServletResponse.SC_NOT_IMPLEMENTED, Validations.SERVER_ERROR);
			e.printStackTrace();
		}
	}
	private void sendErrorResponse(HttpServletResponse response, int status, int message) throws IOException {
	    response.setContentType("text/plain");
	    response.setStatus(status);
	    response.getWriter().println(message);
	    response.getWriter().flush();
	    response.getWriter().close();
	}
	
	private String idGenerator() {
		        Random random = new Random();
		        long newId;
		        do {
		            newId = (long)(random.nextDouble() * 888888888888888L + 111111111111111L);
		        } while (hasRepeatedDigits(newId));
		        return Long.toString(newId);
		    }
	 private static boolean hasRepeatedDigits(long number) {
	        String strNumber = Long.toString(number);
	        char firstChar = strNumber.charAt(0);
	        for (int i = 1; i < strNumber.length(); i++) {
	            if (strNumber.charAt(i) != firstChar) {
	                return false;
	            }
	        }
	        return true;
	    }	
}
