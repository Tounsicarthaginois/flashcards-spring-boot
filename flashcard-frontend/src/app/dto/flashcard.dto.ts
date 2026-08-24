// ============================================
// FLASHCARD DTO - Structure complète d'une flashcard
// Une flashcard = un mot dans la langue étudiée
// + des traductions dans d'autres langues
// + des exemples d'utilisation en phrases
// Structure : Flashcard → Traductions → Exemples
// ============================================

// Exemple d'utilisation du mot dans une phrase
export interface ExempleDto {
  id?: number;
  phraseOriginal: string;   // Phrase dans la langue étudiée (ex: "Hello, how are you?")
  phraseTraduite: string;   // Traduction de la phrase (ex: "Bonjour, comment allez-vous?")
}

// Traduction d'un mot avec ses exemples
export interface TraductionDto {
  id?: number;
  texte: string;            // Le mot traduit (ex: "Bonjour")
  langueId: number;         // ID de la langue de traduction
  langueNom?: string;       // Nom de la langue (renvoyé par le back end)
  exemples: ExempleDto[];   // Liste d'exemples d'utilisation
}

// Flashcard complète avec ses traductions
export interface FlashcardDto {
  id?: number;
  question: string;             // Le mot dans la langue étudiée (ex: "Hello")
  deckId: number;               // ID du deck auquel appartient cette flashcard
  createdAt?: string;           // Date de création (renvoyée par le back end)
  traductions: TraductionDto[]; // Liste des traductions du mot
}