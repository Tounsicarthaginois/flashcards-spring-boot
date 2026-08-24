// Le plan du site : quelle adresse mène à quel écran.
// Chaque route se lit avec 3 clés : path (l'adresse), canActivate
// (le contrôle d'accès) et loadComponent (l'écran à afficher).

import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { adminGuard } from './guards/admin.guard';

export const routes: Routes = [
  // Adresse racine : pas de page d'accueil publique dans l'application,
  // on envoie directement vers la connexion.
  { path: '', redirectTo: '/login', pathMatch: 'full' },

  // Les deux seules pages accessibles sans être connecté.
  {
    path: 'login',
    loadComponent: () => import('./components/login/login').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./components/register/register').then(m => m.RegisterComponent)
  },

  // Pages privées. authGuard s'exécute avant l'affichage : sans token,
  // l'écran n'apparaît jamais. C'est ce qui empêche d'atteindre /decks
  // en tapant l'adresse à la main.
  {
    path: 'decks',
    canActivate: [authGuard],
    loadComponent: () => import('./components/decks/decks').then(m => m.DecksComponent)
  },
  {
    // Le ":deckId" est une partie variable : /flashcards/7 ouvre le deck 7.
    // Le composant récupère ce numéro au chargement (voir flashcards.ts).
    path: 'flashcards/:deckId',
    canActivate: [authGuard],
    loadComponent: () => import('./components/flashcards/flashcards').then(m => m.FlashcardsComponent)
  },
  {
    path: 'review/:deckId',
    canActivate: [authGuard],
    loadComponent: () => import('./components/review/review').then(m => m.ReviewComponent)
  },

  // Ici le guard est différent : adminGuard vérifie en plus le rôle.
  // Un utilisateur connecté mais simple USER est renvoyé vers ses decks.
  {
    path: 'admin',
    canActivate: [adminGuard],
    loadComponent: () => import('./components/admin/admin').then(m => m.AdminComponent)
  },

  // Filet de sécurité pour toute adresse inconnue. Doit rester en dernier :
  // Angular s'arrête à la première route qui correspond.
  { path: '**', redirectTo: '/login' }
];