// Écran d'inscription, construit comme celui de connexion.
// Différence : après une création réussie on ne connecte pas
// l'utilisateur, on le renvoie vers /login.

import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { RegisterRequest } from '../../dto/user.dto';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.scss'
})
export class RegisterComponent {

  // Correspond exactement aux champs du back end
  registerData: RegisterRequest = {
    email: '',
    password: '',
    nom: '',
    prenom: ''
  };

  errorMessage = '';
  successMessage = '';
  isLoading = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef  // Pour forcer la mise à jour de la vue après réponse HTTP
  ) {}

  onRegister(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.authService.register(this.registerData).subscribe({
      next: () => {
        this.successMessage = 'Compte créé ! Redirection...';
        this.cdr.detectChanges();
        // Petite pause pour laisser le temps de lire le message.
        setTimeout(() => this.router.navigate(['/login']), 1500);
      },
      error: () => {
        // Cas le plus fréquent : l'email est déjà pris (le back end refuse).
        this.errorMessage = 'Erreur lors de l\'inscription. Email déjà utilisé ?';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }
}