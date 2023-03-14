package adoteCaoProjetoController;

import java.io.IOException;
import java.io.PrintWriter;
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

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
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
		request.setCharacterEncoding("UTF-8");
		
		boolean isOng = Boolean.parseBoolean(request.getParameter("isOng"));
		boolean duplicity = true;
		
		String login = request.getParameter("login");
		String password = request.getParameter("password");
		String name = request.getParameter("name");
		String cpf = request.getParameter("cpf");
		String birth = request.getParameter("birth");
		
		if(isOng == true) {
			// CADASTRO DE ONG
			try {
				// VERIFICACAO DE DUPLICIDADE NO SISTEMA
				boolean emailDuplicity = dao.checkForDuplicityOngEmail(login);
				boolean ongNameDuplicity = dao.checkForDuplicityOngName(request.getParameter("ongName"));
				boolean cpfDuplicity = dao.checkForDuplicityOngCPF(cpf);
				if(emailDuplicity == false && ongNameDuplicity == false && cpfDuplicity == false) {
					duplicity = false;
				}
			} catch (ClassNotFoundException | IOException e2) {
				e2.printStackTrace();
			}
			if(duplicity == false) {
				
			String ongName = request.getParameter("ongName");
			String state = request.getParameter("state");
			String city = request.getParameter("city");
			String neighborhood = request.getParameter("neighborhood");
			String cep = request.getParameter("cep");
			
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
				dao.registerUserOng(ong, id);
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (ClassNotFoundException | SQLException | IOException e) {
				e.printStackTrace();
				}
			}else if(duplicity == true) {
				response.setContentType("text/plain");
				response.setStatus(HttpServletResponse.SC_CONFLICT);
				
				/*TESTE PARA O TRATAMENTO DE DUPLICIDADE
				 * Ainda é preciso fazer outras verificacoes para efetuar
				 * um cadastro de conta.
				 * O cadastro de conta de usuario adotante ainda esta
				 * sem nenhuma verificacao
				 * */
				 PrintWriter writer = response.getWriter();
				    writer.println("Email Já cadastrado");
				    writer.flush();
				    writer.close();
			}
		}else if (isOng == false) {
			UserAdopter adopter = new UserAdopter();
			adopter.setLogin(login);
			adopter.setPw(password);
			adopter.setUsername(name);
			adopter.setCpf(cpf);
			adopter.setBirth(birth);
			try {
				dao.registerUserAdopter(adopter);
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (ClassNotFoundException | SQLException | IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(login+ password+ name+ cpf+ birth);
	}

	private void configureCors(HttpServletResponse response) {
	    response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
	    response.setHeader("Access-Control-Allow-Methods", "GET, POST");
	    response.setHeader("Access-Control-Allow-Headers", "Content-Type");
	}
}
