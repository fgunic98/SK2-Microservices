package tokenService;

import org.springframework.beans.factory.annotation.Value;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenServiceImpl implements TokenService{

	@Value("${oauth.jwt.secret}")
	private String jwtSecret;
	
	@Override
	public String generate(Claims claims) {
		return Jwts.builder()
				.setClaims(claims)
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
		
	}

	@Override
	public Claims parseToken(String jwt) {
		Claims claims;
		try {
			claims = Jwts.parser()
					.setSigningKey(jwtSecret)
					.parseClaimsJwt(jwt)
					.getBody();
		} catch (Exception e) {
			return null;
		}
		return claims;
	}

}
