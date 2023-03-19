package adoteCaoProjetoController;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import adoteCaoProjetoModel.Adress;
import adoteCaoProjetoModel.Dao;
import adoteCaoProjetoModel.UserAdopter;
import adoteCaoProjetoModel.UserOng;

/**
 * Servlet implementation class RegisterServlet
 */
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Dao dao = new Dao();
	Encrypt encrypt = new Encrypt();
	Validations validations = new Validations();
	int responseToClient = -1;
	boolean hasError = true;


    public RegisterServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		configureCors(response);
		request.setCharacterEncoding("UTF-8");
		
		boolean isOng = Boolean.parseBoolean(request.getParameter("isOng"));
		
		String login = request.getParameter("login");
		String password = request.getParameter("password");
		String name = request.getParameter("name");
		String cpf = request.getParameter("cpf");
		String birth = request.getParameter("birth");
		String state = request.getParameter("state");
		String city = request.getParameter("city");
		String neighborhood = request.getParameter("neighborhood");
		String cep = request.getParameter("cep");
		String ongName;
		int validate = 1;
		if(isOng) {
			ongName = request.getParameter("ongName");
		}else {
			ongName = "NaoEUmaOng";
		}
		
		try {
			validate = validations.validateInputs(city, neighborhood, cep, login, password, name, cpf, birth, ongName, isOng);
		} catch (ClassNotFoundException | IOException e2) {
			e2.printStackTrace();
		}	
		switch(validate) {
		case Validations.INVALID_CITY:
			sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, Validations.INVALID_CITY);
			break;
		case Validations.INVALID_CEP:
			sendErrorResponse(response, 422, Validations.INVALID_CEP);
			break;
		case Validations.INVALID_LOGIN:
			sendErrorResponse(response, 422, Validations.INVALID_LOGIN);
			break;
		case Validations.INVALID_PASSWORD:
			sendErrorResponse(response, 422, Validations.INVALID_PASSWORD);
			break;
		case Validations.INVALID_CPF:
			sendErrorResponse(response, 422, Validations.INVALID_CPF);
			break;
		case Validations.INVALID_BIRTH:
			sendErrorResponse(response, 422, Validations.INVALID_BIRTH);
			break;
		case Validations.FIELD_IS_EMPTY:
			sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, Validations.FIELD_IS_EMPTY);
			break;
		case Validations.CPF_ALREADY_EXISTS:
			sendErrorResponse(response, HttpServletResponse.SC_CONFLICT, Validations.CPF_ALREADY_EXISTS);
			break;
		case Validations.ONG_ALREADY_EXISTS:
			sendErrorResponse(response, HttpServletResponse.SC_CONFLICT, Validations.ONG_ALREADY_EXISTS);
			break;
		case Validations.LOGIN_ALREADY_EXISTS:
			sendErrorResponse(response, HttpServletResponse.SC_CONFLICT, Validations.LOGIN_ALREADY_EXISTS);
			break;
		case Validations.NO_ERROR:
			if(isOng) {
				UserOng ong = new UserOng();
				Adress adress = new Adress();
				ong.setLogin(login);
				try {
					ong.setPw(encrypt.toHash(password));
				} catch (NoSuchAlgorithmException e1) {
					e1.printStackTrace();
				}
				ong.setUsername(name);
				ong.setCpf(cpf);
				ong.setBirth(birth);
				ong.setOngName(ongName);
				
				List<String> keys = encrypt.generateKeys(password);
				String publicKey = keys.get(0);
				String privateKey = keys.get(1);
				
				ong.setPublicKey(publicKey);
				ong.setPrivateKey(privateKey);
				
				adress.setState(state);
				adress.setCity(city);
				adress.setNeighborhood(neighborhood);
				adress.setCep(cep);
				
				try {
					
					int id = dao.registerAdress(adress);
					hasError = dao.registerUserOng(ong, id);
					if(hasError) {
						response.setStatus(HttpServletResponse.SC_OK);
					}
					else {
						sendErrorResponse(response, HttpServletResponse.SC_NOT_IMPLEMENTED, Validations.DATABASE_ERROR);
					}
				} catch (ClassNotFoundException | SQLException | IOException e) {
					sendErrorResponse(response, HttpServletResponse.SC_NOT_IMPLEMENTED, Validations.DATABASE_ERROR);
					e.printStackTrace();
					}
			}else {
				UserAdopter adopter = new UserAdopter();
				Adress adress = new Adress();
				adopter.setLogin(login);
				try {
					adopter.setPw(encrypt.toHash(password));
				} catch (NoSuchAlgorithmException e1) {
					e1.printStackTrace();
				}
				adopter.setUsername(name);
				adopter.setCpf(cpf);
				adopter.setBirth(birth);
				
				List<String> keys = encrypt.generateKeys(password);
				String publicKey = keys.get(0);
				String privateKey = keys.get(1);
				

				adopter.setPublicKey(publicKey);
				adopter.setPrivateKey(privateKey);
				
				adress.setState(state);
				adress.setCity(city);
				adress.setNeighborhood(neighborhood);
				adress.setCep(cep);
				try {
					int idAdress = dao.registerAdress(adress);
					hasError = dao.registerUserAdopter(adopter, idAdress);
					if(hasError) {
					response.setStatus(HttpServletResponse.SC_OK);
					} else {
						sendErrorResponse(response, HttpServletResponse.SC_NOT_IMPLEMENTED, Validations.DATABASE_ERROR);
					}
				} catch (ClassNotFoundException | SQLException | IOException e) {
					e.printStackTrace();
					sendErrorResponse(response, HttpServletResponse.SC_NOT_IMPLEMENTED, Validations.DATABASE_ERROR);
				}
			}
			break;
		case -1:
			break;
		}
			
	}
	private void configureCors(HttpServletResponse response) {
	    response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
	    response.setHeader("Access-Control-Allow-Methods", "GET, POST");
	    response.setHeader("Access-Control-Allow-Headers", "Content-Type");
	}
	private void sendErrorResponse(HttpServletResponse response, int status, int message) throws IOException {
	    response.setContentType("text/plain");
	    response.setStatus(status);
	    response.getWriter().println(message);
	    response.getWriter().flush();
	    response.getWriter().close();
	}
}
