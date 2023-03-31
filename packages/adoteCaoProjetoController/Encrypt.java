package adoteCaoProjetoController;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class Encrypt {
    final static String ALGORITHM = "RSA";
    private static final long JWT_TTL_MILLIS = 3600000L; // 1 hour
    private static final int JWT_SECRET_KEY_LENGTH = 256;
    public static final String JWT_ISSUER = "Adote.Cao";

    public String toHash(String message) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(message.getBytes());
        byte[] digest = md.digest();

        StringBuffer hexString = new StringBuffer();
        for(int i = 0; i < digest.length; i++) {
            if((0xff & digest[i]) < 0x10) {
                hexString.append("0" + Integer.toHexString((0xFF & digest[i])));
            } else {
                hexString.append(Integer.toHexString(0xFF & digest[i]));
            }
        }

        return hexString.toString();
    }

    public List<String> generateKeys(String password) {
    	List<String> keys = new ArrayList<String>();

        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyGen.initialize(1024);

            final KeyPair keysG = keyGen.generateKeyPair();
            byte[] publicKey = keysG.getPublic().getEncoded();
            byte[] privateKey = keysG.getPrivate().getEncoded();

            String publicKeyString = Base64.getEncoder().encodeToString(publicKey);
            String privateKeyString = Base64.getEncoder().encodeToString(privateKey);

            keys.add(publicKeyString);
            keys.add(privateKeyString);

            return keys;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    } 
    public static byte[] generateSalt(int length) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return salt;
    }
    
    public String createJWT(String id, String issuer, String subject) {
    	SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    	long nowMillis = System.currentTimeMillis();
    	Date now = new Date(nowMillis);
    	byte[] apiKeySecretBytes = new byte[JWT_SECRET_KEY_LENGTH / 8];
    	SecureRandom random = new SecureRandom();
    	random.nextBytes(apiKeySecretBytes);
    	Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

    	@SuppressWarnings("deprecation")
		JwtBuilder builder = Jwts.builder().setId(id)
    			.setIssuedAt(now)
    			.setSubject(subject)
    			.setIssuer(issuer)
    			.signWith(signatureAlgorithm,signingKey);
    	
    	if(JWT_TTL_MILLIS > 0) {
    		long expMillis = nowMillis + JWT_TTL_MILLIS;
    		Date exp = new Date(expMillis);
    		builder.setExpiration(exp);
    	}
    	
    	return builder.compact();
    }
}
