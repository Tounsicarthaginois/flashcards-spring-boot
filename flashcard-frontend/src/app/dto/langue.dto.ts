// ============================================
// LANGUE DTO - Les langues disponibles dans l'app
// Utilisé pour choisir la langue d'un deck
// ============================================

export interface LangueDto {
  id: number;
  nom: string;    // "Français", "Anglais", "Espagnol"
  code: string;   // "fr", "en", "es" (code ISO)
}