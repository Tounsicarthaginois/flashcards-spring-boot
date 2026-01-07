package be.ipam.flashcards.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Clé secrète pour signer les tokens (format hexadécimal, min 256 bits = 64 caractères hex)
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    // Durée de validité : 24 heures en millisecondes
    private static final long JWT_EXPIRATION = 86400000;  // 24h * 60m * 60s * 1000ms

    // Extrait l'email du token (stocké dans le "subject")
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);  // Claims::getSubject = référence de méthode
    }

    // Extrait une claim spécifique du token (générique)
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);  // Récupère toutes les claims
        return claimsResolver.apply(claims);  // Applique la fonction pour extraire la claim voulue
    }

    // Génère un token simple (sans claims supplémentaires)
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);  // HashMap vide
    }

    // Génère un token avec claims personnalisées
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()  // Constructeur de JWT
                .claims(extraClaims)  // Claims supplémentaires (vide dans notre cas)
                .subject(userDetails.getUsername())  // Subject = email de l'utilisateur
                .issuedAt(new Date(System.currentTimeMillis()))  // Date de création
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))  // Date d'expiration (+24h)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // Signe avec clé secrète + algo HMAC-SHA256
                .compact();  // Génère la chaîne JWT finale
    }

    // Vérifie que le token est valide (email correspond + pas expiré)
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Vérifie si le token est expiré
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());  // Date expiration < maintenant
    }

    // Extrait la date d'expiration du token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extrait toutes les claims du token (décode et vérifie la signature)
    private Claims extractAllClaims(String token) {
        // Logs de debug (à retirer en production)
        System.out.println("TOKEN REÇU: [" + token + "]");
        System.out.println("LONGUEUR: " + token.length());
        System.out.println("CONTIENT ESPACES: " + token.contains(" "));

        return Jwts.parser()  // Parser JWT
                .verifyWith(getSigningKey())  // Vérifie la signature avec la clé secrète
                .build()
                .parseSignedClaims(token)  // Parse et valide le token
                .getPayload();  // Récupère le payload (les claims)
    }

    // Récupère la clé de signature à partir de la chaîne hexadécimale
    private SecretKey getSigningKey() {
        byte[] keyBytes = hexStringToByteArray(SECRET_KEY);  // Convertit hex → bytes
        return Keys.hmacShaKeyFor(keyBytes);  // Crée une SecretKey pour HMAC
    }

    // Convertit une chaîne hexadécimale en tableau de bytes
    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];  // 2 caractères hex = 1 byte
        for (int i = 0; i < len; i += 2) {  // Parcourt 2 par 2
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)  // Premier caractère
                    + Character.digit(s.charAt(i+1), 16));  // Deuxième caractère
        }
        return data;
    }
}

// Service central pour la gestion des tokens JWT
// Structure JWT : HEADER.PAYLOAD.SIGNATURE (3 parties séparées par des points)
// Utilisé par AuthService (création token) et JwtAuthenticationFilter (validation token)