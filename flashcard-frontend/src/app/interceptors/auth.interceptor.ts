// Toutes les requêtes de l'application passent ici, parce que
// l'interceptor est branché dans app.config.ts.
//
// Il intervient à deux moments : à l'aller il ajoute le token dans
// l'en-tête Authorization, au retour il regarde le code d'erreur.
// C'est pour ça que deck.service, flashcard.service et les autres
// ne contiennent aucune ligne sur le token.

import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { environment } from '../../environments/environment';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.getToken();

  // On vérifie que la requête part bien vers notre back end. Sans ce
  // test, le token serait aussi joint aux appels vers un site externe.
  const isApiRequest = req.url.startsWith(environment.apiUrl);

  // Une requête ne peut pas être modifiée directement, on en fabrique
  // une copie avec l'en-tête en plus. "Bearer" est le format attendu
  // par Spring Security côté back end.
  const authReq = token && isApiRequest
    ? req.clone({ headers: req.headers.set('Authorization', `Bearer ${token}`) })
    : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {

      // 401 : le serveur ne reconnaît pas l'utilisateur, le token est
      // absent ou expiré. On nettoie et on renvoie à la connexion.
      // Traiter ça ici gère l'expiration pour tous les écrans d'un coup.
      if (error.status === 401) {
        authService.logout();
        router.navigate(['/login']);
      }

      // 403 : l'identité est bonne mais les droits ne suffisent pas.
      if (error.status === 403) {
        router.navigate(['/decks']);
      }

      // On relance l'erreur pour que l'écran puisse afficher son message.
      return throwError(() => error);
    })
  );
};
