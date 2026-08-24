// Contrôle d'accès aux pages privées. Angular exécute cette fonction
// avant d'afficher l'écran : elle renvoie true (on passe) ou false (bloqué).
//
// À noter : ce guard est un confort d'affichage, pas une sécurité.
// Le vrai contrôle est côté back end, qui revérifie le token à chaque
// requête. Ici on évite juste de montrer un écran vide.

import { CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};