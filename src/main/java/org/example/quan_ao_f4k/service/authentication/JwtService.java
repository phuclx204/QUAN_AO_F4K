package org.example.quan_ao_f4k.service.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {
	private static final String SECRET_KEY = "ABCE7A4042A9FEA6C1021F3243605FA5E2D0A1430F3A7655F46EE3E7E5D6E93BA3DF688980CABF24872CCA65C70CBA3367610B6C0D4430046FAA0E7F6F36B25C";
	//Thời gian của token = phút
	private static final long MINUTES_EXPIRE = System.currentTimeMillis() + 30 * 60 * 1000;

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	// extract information from JWT
	private Claims extractAllClaims(String token) {
		return Jwts
				.parserBuilder()
				.setSigningKey(getSignInKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	// decode and get the key
	private Key getSignInKey() {
		// decode SECRET_KEY
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(keyBytes);
	}


	public String generateToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails);
	}

	// generate token using Jwt utility class and return token as String
	public String generateToken(
			Map<String, Object> extraClaims,
			UserDetails userDetails
	) {
		String roles = userDetails.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));
		extraClaims.put("role", roles);
		return Jwts
				.builder()
				.setClaims(extraClaims)
				.setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(MINUTES_EXPIRE))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	// if token is valid by checking if token is expired for current user
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	// if token is expired
	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	// get expiration date from token
	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}


}
