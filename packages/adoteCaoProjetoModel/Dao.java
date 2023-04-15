package adoteCaoProjetoModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Dao {
    private static final Logger LOGGER = Logger.getLogger(Dao.class.getName());

    private String getConfigValueByKey(String key) throws IOException {
        File file = new File("C:\\Users\\Pichau\\Desktop\\novoWorkspace\\adoteCaoProjeto\\admin\\config.ini");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2 && parts[0].equals(key)) {
                        return parts[1].trim();
                    }
                }
            }
        }
        return null;
    }

    public Connection connectDB() throws SQLException, IOException, ClassNotFoundException {
        String url = getConfigValueByKey("url");
        String user = getConfigValueByKey("user");
        String password = getConfigValueByKey("password");
        Class.forName("com.mysql.cj.jdbc.Driver");
        //Class.forName("org.postgresql.Driver");

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to the database", e);
            throw e;
        }
        return connection;
    }
    
    public boolean registerUserOng(UserOng user, int idAdress) throws SQLException, ClassNotFoundException, IOException {
		// se precisar colocar public.userOng (em todas as tabelas que fazem transação)
        String sql = "INSERT INTO userOng (login, pw, username, cpf, birth, ongName, publicKey, privateKey, idAdress) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = this.connectDB();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPw());
            statement.setString(3, user.getUsername());
            statement.setString(4, user.getCpf());
            statement.setString(5, user.getBirth());
            statement.setString(6, user.getOngName());
            statement.setString(7, user.getPublicKey());
            statement.setString(8, user.getPrivateKey());
            statement.setInt(9, idAdress);
            int update = statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                return true;
            } else {
                throw new SQLException("Nenhuma chave primaria foi gerada.");
            }
        } catch (SQLException e) {
            System.err.println("Erro durante o registro: " + e.getMessage());
            return false;
        }
    }

	public boolean registerUserAdopter(UserAdopter user, int idAdress) throws SQLException, ClassNotFoundException, IOException {
		 String sql = "INSERT INTO userAdopter (login, pw, username, cpf, birth, publicKey, privateKey, idAdress) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	        try (Connection connection = this.connectDB();
	             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
	            statement.setString(1, user.getLogin());
	            statement.setString(2, user.getPw());
	            statement.setString(3, user.getUsername());
	            statement.setString(4, user.getCpf());
	            statement.setString(5, user.getBirth());
	            statement.setString(6, user.getPublicKey());
	            statement.setString(7, user.getPrivateKey());
	            statement.setInt(8, idAdress);
	            int update = statement.executeUpdate();
	            ResultSet keys = statement.getGeneratedKeys();
	            if (keys.next()) {
	                int id = keys.getInt(1);
	                return true;
	            } else {
	                throw new SQLException("Nenhuma chave primaria foi gerada.");
	            }
	        } catch (SQLException e) {
	            System.err.println("Erro durante o registro: " + e.getMessage());
	            return false;
	        }
	    }

	public int registerAdress(Adress adress) throws ClassNotFoundException, IOException {
		String sql = "INSERT INTO adress (state, city, neighbor, cep) VALUES (?, ?, ?, ?)";
		try (Connection connection = this.connectDB();
	             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, adress.getState());
			statement.setString(2, adress.getCity());
			statement.setString(3, adress.getNeighborhood());
			statement.setString(4, adress.getCep());
			int update = statement.executeUpdate();
			ResultSet keys = statement.getGeneratedKeys();
			if(keys.next()) {
				int id = keys.getInt(1);
				return id;
			}else {
				throw new SQLException("Nenhuma chave primaria foi gerada.");
			}
		}catch(SQLException e) {
			System.err.println("Erro durante o registro: "+e.getMessage());
			return -1;
		}
		
	}

	public boolean checkForDuplicityOngEmail(String login) throws ClassNotFoundException, IOException {
		String sql = "SELECT * FROM userOng WHERE login = ?";
		try (Connection conn = this.connectDB();
		         PreparedStatement statement = conn.prepareStatement(sql)) {
		        statement.setString(1, login);
		        ResultSet rs = statement.executeQuery();
		        return rs.next();
		    } catch (SQLException e) {
		        System.out.println(e.getMessage());
		        return false;
		    }
		
		
	}

	public boolean checkForDuplicityOngName(String ongName) throws ClassNotFoundException, IOException {
		String sql = "SELECT * FROM userOng WHERE ongName = ?";
		try (Connection conn = this.connectDB();
		         PreparedStatement statement = conn.prepareStatement(sql)) {
		        statement.setString(1, ongName);
		        ResultSet rs = statement.executeQuery();
		        return rs.next();
		    } catch (SQLException e) {
		        System.out.println(e.getMessage());
		        return false;
		    }
	}

	public boolean checkForDuplicityOngCPF(String cpf) throws ClassNotFoundException, IOException {
		String sql = "SELECT * FROM userOng WHERE cpf = ?";
		try (Connection conn = this.connectDB();
		         PreparedStatement statement = conn.prepareStatement(sql)) {
		        statement.setString(1, cpf);
		        ResultSet rs = statement.executeQuery();
		        return rs.next();
		    } catch (SQLException e) {
		        System.out.println(e.getMessage());
		        return false;
		    }
	}
	public boolean checkForDuplicityAdopterEmail(String login) throws ClassNotFoundException, IOException {
		String sql = "SELECT * FROM userAdopter WHERE login = ?";
		try (Connection conn = this.connectDB();
		         PreparedStatement statement = conn.prepareStatement(sql)) {
		        statement.setString(1, login);
		        ResultSet rs = statement.executeQuery();
		        return rs.next();
		    } catch (SQLException e) {
		        System.out.println(e.getMessage());
		        return false;
		    }
		
		
	}


	public boolean checkForDuplicityAdopterCPF(String cpf) throws ClassNotFoundException, IOException {
		String sql = "SELECT * FROM userAdopter WHERE cpf = ?";
		try (Connection conn = this.connectDB();
		         PreparedStatement statement = conn.prepareStatement(sql)) {
		        statement.setString(1, cpf);
		        ResultSet rs = statement.executeQuery();
		        return rs.next();
		    } catch (SQLException e) {
		        System.out.println(e.getMessage());
		        return false;
		    }
	}

	public boolean validateLoginOng(String hashPassword, String login) throws ClassNotFoundException, IOException {
		String sql = "SELECT * FROM userOng WHERE login =? AND pw =?";
		try(Connection conn = this.connectDB();
				PreparedStatement statement = conn.prepareStatement(sql)){
					statement.setString(1, login);
					statement.setString(2, hashPassword);
					ResultSet rs = statement.executeQuery();
					return rs.next();
				}catch(SQLException e) {
					System.out.println(e.getMessage());
				}
		return false;
	}
	public boolean validateLoginAdopter(String hashPassword, String login) throws ClassNotFoundException, IOException {
		String sql = "SELECT * FROM userAdopter WHERE login =? AND pw =?";
		try(Connection conn = this.connectDB();
				PreparedStatement statement = conn.prepareStatement(sql)){
					statement.setString(1, login);
					statement.setString(2, hashPassword);
					ResultSet rs = statement.executeQuery();
					return rs.next();
				}catch(SQLException e) {
					System.out.println(e.getMessage());
				}
		return false;
	}

	public boolean insertJWT(String jwt, boolean isOng, String login) throws ClassNotFoundException, SQLException, IOException {
		String sql;
		if(isOng) {
			sql = "UPDATE userOng set jwt=? WHERE login=?";
		}else {
			sql = "UPDATE userAdopter set jwt=? WHERE login=?";
		}
		try(Connection conn = this.connectDB();
				PreparedStatement statement = conn.prepareStatement(sql)){
			statement.setString(1, jwt);
			statement.setString(2, login);
			int rowsUpdated = statement.executeUpdate();
			if(rowsUpdated == 0) {
				return false;
			}else {
				return true;
			}
		}catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
