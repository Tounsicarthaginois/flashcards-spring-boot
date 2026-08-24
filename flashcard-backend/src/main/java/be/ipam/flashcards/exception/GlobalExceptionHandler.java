package be.ipam.flashcards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Gestionnaire global des exceptions
 *
 * Intercepte toutes les exceptions lancées dans l'application
 * et retourne des réponses HTTP formatées au client
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gère les exceptions ResourceNotFoundException (404)
     *
     * Quand on lance : throw new ResourceNotFoundException("Deck", "id", 5)
     * Cette méthode intercepte l'exception et retourne une réponse 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),  // 404
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Gère les exceptions IllegalArgumentException (400)
     *
     * Utilisé pour les erreurs de validation
     * Exemple : nom de deck vide, email invalide
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),  // 400
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Gère les échecs d'authentification (401)
     *
     * Lancé par l'AuthenticationManager dans AuthService.login()
     * quand l'email n'existe pas ou que le mot de passe est faux
     * (BadCredentialsException hérite de AuthenticationException).
     *
     * Le message reste volontairement générique : on ne dit jamais
     * au client si c'est l'email OU le mot de passe qui est faux,
     * sinon on permet d'énumérer les comptes existants.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),  // 401
                "Email ou mot de passe incorrect",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Gère les refus d'autorisation (403)
     *
     * Lancé quand l'utilisateur est bien identifié, mais qu'il n'a pas le
     * droit d'accéder à cette donnée précise. Typiquement : tenter d'ouvrir
     * le deck privé de quelqu'un d'autre en devinant son identifiant.
     *
     * La distinction avec le 401 est importante :
     *   401 = "je ne sais pas qui tu es"     → le token manque ou a expiré
     *   403 = "je sais qui tu es, mais non"  → l'identité est bonne, pas les droits
     *
     * Côté Angular, l'interceptor intercepte ce 403 et ramène
     * automatiquement l'utilisateur vers ses propres decks.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),  // 403
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    /**
     * Gère toutes les autres exceptions non prévues (500)
     *
     * C'est le filet de sécurité pour les erreurs imprévues
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),  // 500
                "Une erreur interne s'est produite : " + ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
