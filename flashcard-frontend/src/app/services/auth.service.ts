// Tout ce qui touche à l'authentification est regroupé ici : connexion,
// inscription, déconnexion, et la mémoire de l'utilisateur connecté.
// C'est le seul fichier qui écrit dans le localStorage — les guards et
// l'interceptor passent par lui pour savoir qui est connecté.

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { tap } from 'rxjs/operators';
import { LoginRequest, RegisterRequest, AuthResponse } from '../dto/user.dto';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private apiUrl = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient) {}

  // POST /api/auth/login → le back end renvoie token + email + role + userId
  login(request: LoginRequest): Observable<AuthResponse> {
    const securityError = this.getHttpsAuthError();
    if (securityError) {
      return throwError(() => securityError);
    }

    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
      // Le moment clé de la connexion : on garde en mémoire ce que le
      // back end vient de renvoyer. Le localStorage survit au
      // rafraîchissement de la page, c'est ce qui nous garde connecté.
      tap(response => {
        localStorage.setItem('token', response.token);   // repris par l'interceptor
        localStorage.setItem('email', response.email);   // affiché dans la barre du haut
        localStorage.setItem('role', response.role);     // lu par adminGuard
        localStorage.setItem('userId', String(response.userId)); // sert à savoir si on est le créateur d'un deck
      })
    );
  }

  // POST /api/auth/register → crée un compte
  register(request: RegisterRequest): Observable<AuthResponse> {
    const securityError = this.getHttpsAuthError();
    if (securityError) {
      return throwError(() => securityError);
    }

    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request);
  }

  // Se déconnecter revient simplement à vider le localStorage : sans
  // token, isLoggedIn() renvoie false et les guards bloquent tout.
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('email');
    localStorage.removeItem('role');
    localStorage.removeItem('userId');
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getEmail(): string | null {
    return localStorage.getItem('email');
  }

  getRole(): string | null {
    return localStorage.getItem('role');
  }

  // Utilisé dans flashcards.ts pour comparer "qui suis-je" avec
  // "qui a créé ce deck", et en déduire le droit de modifier.
  getUserId(): number | null {
    const id = localStorage.getItem('userId');
    return id ? Number(id) : null;
  }

  // Sécurité : en production, on refuse d'envoyer un mot de passe si la
  // connexion n'est pas en HTTPS. En développement local le drapeau est
  // à false (voir environments/environment.ts), sinon rien ne marcherait.
  private getHttpsAuthError(): Error | null {
    if (!environment.enforceHttpsForAuth) {
      return null;
    }

    const url = new URL(this.apiUrl, window.location.origin);
    return url.protocol === 'https:'
      ? null
      : new Error('Les mots de passe doivent etre envoyes uniquement via HTTPS.');
  }
}
