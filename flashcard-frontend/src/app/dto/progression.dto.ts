// ============================================
// PROGRESSION DTO - Système SRS (répétition espacée)
// Suit l'avancement de l'utilisateur sur chaque flashcard
// L'algorithme calcule automatiquement quand revoir chaque carte :
// - reussi=true → prochaine révision dans 1j, 7j ou 30j (selon le niveau atteint)
// - reussi=false → prochaine révision dans 1j (reset)
// ============================================

// Les états possibles d'une flashcard pour un utilisateur
export type EtatProgression = 'NOUVEAU' | 'EN_COURS' | 'CONNU';

// Ce que le back end renvoie pour une progression
export interface ProgressionDto {
  id: number;
  utilisateurId: number;
  flashcardId: number;
  question: string;               // Le mot de la flashcard (pour affichage)
  etat: EtatProgression;          // NOUVEAU / EN_COURS / CONNU
  niveauConnaissance: number;     // 0 à 5
  prochaineRevision: string;      // Date de la prochaine révision
  nbRevisionsReussies: number;    // Révisions réussies consécutives
  derniereRevision: string;       // Date de la dernière révision
  createdAt: string;
}

// Ce qu'on envoie après avoir révisé une flashcard
export interface RevisionRequest {
  flashcardId: number;
  reussi: boolean;  // true = je connais, false = je ne connais pas
}