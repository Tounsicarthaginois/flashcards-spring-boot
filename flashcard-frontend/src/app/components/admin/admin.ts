// Page d'administration, protégée par adminGuard. Elle regroupe trois
// sections indépendantes : les langues, les decks à valider et les
// utilisateurs. Aucune notion nouvelle ici, c'est le même schéma que
// l'écran des decks appliqué trois fois.

import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { LangueService } from '../../services/langue.service';
import { DeckService } from '../../services/deck.service';
import { UtilisateurService } from '../../services/utilisateur.service';
import { AuthService } from '../../services/auth.service';
import { LangueDto } from '../../dto/langue.dto';
import { DeckDto } from '../../dto/deck.dto';
import { UtilisateurDto } from '../../dto/utilisateur.dto';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.html',
  styleUrl: './admin.scss'
})
export class AdminComponent implements OnInit {

  // ─── LANGUES ───────────────────────────────
  langues: LangueDto[] = [];
  showLangueForm = false;
  editingLangue: LangueDto | null = null;
  langueFormData: Partial<LangueDto> = { nom: '', code: '' };

  // ─── DECKS EN ATTENTE ──────────────────────
  // Decks passés en PUBLIQUE par leur auteur, qui attendent une validation.
  decksEnAttente: DeckDto[] = [];

  // ─── UTILISATEURS ──────────────────────────
  utilisateurs: UtilisateurDto[] = [];

  // ─── COMMUN ────────────────────────────────
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private langueService: LangueService,
    private deckService: DeckService,           // Pour récupérer et valider les decks publiques
    private utilisateurService: UtilisateurService,
    public authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadLangues();
    this.loadDecksEnAttente();
    this.loadUtilisateurs();
  }

  // ─── MÉTHODES LANGUES ──────────────────────

  loadLangues(): void {
    this.langueService.getAll().subscribe({
      next: (langues) => { this.langues = langues; this.cdr.detectChanges(); },
      error: () => { this.errorMessage = 'Erreur chargement langues.'; this.cdr.detectChanges(); }
    });
  }

  openCreateLangueForm(): void {
    this.editingLangue = null;
    this.langueFormData = { nom: '', code: '' };
    this.showLangueForm = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  openEditLangueForm(langue: LangueDto): void {
    this.editingLangue = langue;
    this.langueFormData = { nom: langue.nom, code: langue.code };
    this.showLangueForm = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  onSubmitLangue(): void {
    if (this.editingLangue) {
      // PUT /api/langues/{id}
      this.langueService.update(this.editingLangue.id, this.langueFormData).subscribe({
        next: () => { this.successMessage = 'Langue modifiée.'; this.showLangueForm = false; this.loadLangues(); },
        error: () => { this.errorMessage = 'Erreur modification.'; this.cdr.detectChanges(); }
      });
    } else {
      // POST /api/langues
      this.langueService.create(this.langueFormData).subscribe({
        next: () => { this.successMessage = 'Langue créée.'; this.showLangueForm = false; this.loadLangues(); },
        error: () => { this.errorMessage = 'Erreur création.'; this.cdr.detectChanges(); }
      });
    }
  }

  deleteLangue(id: number): void {
    if (confirm('Supprimer cette langue ?')) {
      this.langueService.delete(id).subscribe({
        next: () => { this.successMessage = 'Langue supprimée.'; this.loadLangues(); },
        error: () => { this.errorMessage = 'Erreur suppression.'; this.cdr.detectChanges(); }
      });
    }
  }

  // ─── MÉTHODES VALIDATION DECKS ─────────────
  // Le cycle de vie d'un deck : PRIVEE (visible par son auteur), puis
  // PUBLIQUE quand il le soumet, puis OFFICIELLE une fois validé ici.

  loadDecksEnAttente(): void {
    this.deckService.getPubliques().subscribe({
      next: (decks) => { this.decksEnAttente = decks; this.cdr.detectChanges(); },
      error: () => { this.errorMessage = 'Erreur chargement decks.'; this.cdr.detectChanges(); }
    });
  }

  // On ne retire pas le deck de la liste à la main : on recharge, et
  // comme il n'est plus "en attente", il n'y figure simplement plus.
  validerDeck(id: number): void {
    if (confirm('Valider ce deck ? Il deviendra visible par tous les utilisateurs.')) {
      this.deckService.valider(id).subscribe({
        next: () => {
          this.successMessage = 'Deck validé ! Il est maintenant OFFICIEL.';
          this.loadDecksEnAttente();
          this.cdr.detectChanges();
        },
        error: () => { this.errorMessage = 'Erreur lors de la validation.'; this.cdr.detectChanges(); }
      });
    }
  }

  // Rejeter ne supprime rien : on repasse simplement le deck en PRIVEE,
  // son auteur le récupère intact et pourra le resoumettre plus tard.
  rejeterDeck(id: number): void {
    if (confirm('Rejeter ce deck ? Il redeviendra privé.')) {
      this.deckService.update(id, { type: 'PRIVEE' }).subscribe({
        next: () => {
          this.successMessage = 'Deck rejeté, renvoyé en PRIVEE.';
          this.loadDecksEnAttente();
          this.cdr.detectChanges();
        },
        error: () => { this.errorMessage = 'Erreur lors du rejet.'; this.cdr.detectChanges(); }
      });
    }
  }

  // ─── MÉTHODES UTILISATEURS ─────────────────

  loadUtilisateurs(): void {
    this.utilisateurService.getAll().subscribe({
      next: (utilisateurs) => { this.utilisateurs = utilisateurs; this.cdr.detectChanges(); },
      error: () => { this.errorMessage = 'Erreur chargement utilisateurs.'; this.cdr.detectChanges(); }
    });
  }

  deleteUtilisateur(id: number): void {
    if (confirm('Supprimer cet utilisateur ? Ses decks et progressions seront aussi supprimés.')) {
      this.utilisateurService.delete(id).subscribe({
        next: () => { this.successMessage = 'Utilisateur supprimé.'; this.loadUtilisateurs(); },
        error: () => { this.errorMessage = 'Erreur suppression.'; this.cdr.detectChanges(); }
      });
    }
  }

  // ─── NAVIGATION ────────────────────────────

  goBack(): void { this.router.navigate(['/decks']); }
  logout(): void { this.authService.logout(); this.router.navigate(['/login']); }
}