import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { CanActivateFn } from '@angular/router';

// Même principe que authGuard, avec une condition en plus : le rôle,
// récupéré depuis le localStorage où il a été rangé lors de la connexion.
export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn() && authService.getRole() === 'GESTIONNAIRE') {
    return true;
  }

  // On le renvoie vers ses decks et non vers le login : il est bien
  // connecté, ce n'est pas un problème d'identité mais de droits.
  router.navigate(['/decks']);
  return false;
};