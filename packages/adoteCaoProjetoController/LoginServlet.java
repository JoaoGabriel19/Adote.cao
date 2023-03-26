package adoteCaoProjetoController;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Validations validations = new Validations();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
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
			setSession(request, response, login, true);
			}else if(!confirmations.get(0) && confirmations.get(1)) {
				setSession(request, response, login, false);
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
	private void setSession(HttpServletRequest request, HttpServletResponse response, String login, boolean isOng) throws IOException {
		HttpSession session = request.getSession();
		session.setAttribute("login", login);
		session.setAttribute("isLogged", true);
		if(isOng) {
			session.setAttribute("isOng", true);
		}else {
			session.setAttribute("isOng", false);
		}
		Map<String, Object> sessionData = new HashMap<>();
		sessionData.put("login", session.getAttribute("login"));
		sessionData.put("isLogged", session.getAttribute("isLogged"));
		sessionData.put("isOng", session.getAttribute("isOng"));
		
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
}
