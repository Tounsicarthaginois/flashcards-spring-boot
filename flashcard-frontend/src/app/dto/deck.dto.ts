// ============================================
// DECK DTO - Un deck = un paquet de flashcards
// Un deck a un type et appartient à une langue
// ============================================

// Les 3 types de deck possibles (enum du back end)
export type TypeListe = 'OFFICIELLE' | 'PRIVEE' | 'PUBLIQUE';

// Ce que le back end renvoie pour un deck
export interface DeckDto {
  id: number;
  name: string;
  description: string;
  type: TypeListe;          // OFFICIELLE / PRIVEE / PUBLIQUE
  langueId: number;         // ID de la langue étudiée
  langueNom: string;        // Nom de la langue (calculé par le back end)
  userId: number;           // ID du créateur (récupéré via JWT)
  validateurId: number;     // ID du validateur (si liste publique)
  createdAt: string;        // Date de création
  flashcardCount: number;   // Nombre de flashcards dans le deck
}