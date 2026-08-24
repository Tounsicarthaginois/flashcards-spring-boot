// Écran de connexion. Le composant se contente de récupérer ce qui est
// tapé et de le passer à AuthService : c'est le service qui parle au
// back end et qui range le token.

import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LoginRequest } from '../../dto/user.dto';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class LoginComponent {

  // Cet objet est relié aux champs du formulaire par [(ngModel)].
  // L'utilisateur tape, les valeurs se remplissent toutes seules :
  // on n'a jamais besoin d'aller lire le contenu des champs.
  loginData: LoginRequest = {
    email: '',
    password: ''
  };

  errorMessage = '';
  isLoading = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    // L'application tourne sans Zone.js : Angular ne détecte pas seul
    // l'arrivée d'une réponse réseau, il faut lui demander de redessiner.
    private cdr: ChangeDetectorRef
  ) {}

  onLogin(): void {
    this.isLoading = true;
    this.errorMessage = '';

    // subscribe envoie la demande sans bloquer l'application : Angular
    // exécutera next quand la réponse arrive, ou error si ça échoue.
    this.authService.login(this.loginData).subscribe({
      next: () => {
        // Le token est déjà rangé par le service, il ne reste qu'à changer de page.
        this.router.navigate(['/decks']);
      },
      error: () => {
        this.errorMessage = 'Email ou mot de passe incorrect.';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }
}