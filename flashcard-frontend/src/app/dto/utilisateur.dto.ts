// ============================================
// UTILISATEUR DTO - Infos d'un utilisateur
// IMPORTANT : pas de champ password (sécurité)
// Le back end exclut toujours le password des réponses
// ============================================

export interface UtilisateurDto {
  id: number;      // Identifiant unique en base de données
  email: string;   // Email (utilisé comme login)
  nom: string;     // Nom de famille
  prenom: string;  // Prénom
}