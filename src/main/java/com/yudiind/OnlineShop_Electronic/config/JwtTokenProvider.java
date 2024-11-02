package com.yudiind.OnlineShop_Electronic.config;

import com.yudiind.OnlineShop_Electronic.model.entity.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private Long validityInMilliseconds; // 1 hours

    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtTokenProvider(@Lazy UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init(){
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }


    public String createToken(String email, Set<Role> roles){
       try {
           log.info("Memulai pembuatan token untuk email: {}", email);

           Claims claims = Jwts.claims().setSubject(email);
           claims.put("roles", roles
                   .stream()
                   // Gunakan getAuthority untuk mendapatkan nama role
                   .map(Role::getAuthority)
                   .collect(Collectors.toList()));

           Date now = new Date();
           Date validity = new Date(now.getTime() + validityInMilliseconds);

           String token = Jwts.builder()
                   .setClaims(claims)
                   .setIssuedAt(now)
                   .setExpiration(validity)
                   .signWith(SignatureAlgorithm.HS256, secretKey)
                   .compact();

           log.info("Token berhasil dibuat untuk: {} dengan masa berlaku sampai: {}", email, validity);
           return token;
       } catch (Exception e){
           log.error("Gagal membuat token untuk email: {}. Error: {}", email, e.getMessage());
           throw new RuntimeException("Error saat membuat token", e);
       }
    }

    public boolean validateToken(String token){
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                                     .setSigningKey(secretKey)
                                     .build()
                                     .parseClaimsJws(token);
            return true;
        }catch (JwtException | IllegalArgumentException e){
            logger.error("Invalid token: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token){
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        }catch (ExpiredJwtException e){
            logger.error("Token is Expired: {}", e.getMessage());
            return true;
        }
    }

    public String getEmail(String token){

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Authentication getAuthentication(String token){

        String email = getEmail(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

    }
}
