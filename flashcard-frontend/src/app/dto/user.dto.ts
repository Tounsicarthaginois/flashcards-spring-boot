// Ce qu'on envoie pour se connecter
export interface LoginRequest {
  email: string;
  password: string;
}

// Ce qu'on envoie pour s'inscrire
export interface RegisterRequest {
  email: string;
  password: string;
  nom: string;
  prenom: string;
}

// Ce que le back end renvoie après login/register
export interface AuthResponse {
  token: string;
  type: string;    // Toujours "Bearer"
  email: string;   // Email de l'utilisateur connecté
  role: string;    // "USER" ou "GESTIONNAIRE"
  userId: number;  // ID de l'utilisateur — pour vérifier s'il est le créateur d'un deck
}