package be.ipam.flashcards.dto;

public class UtilisateurDto {

    private Long id;
    private String email;
    private String displayName;
    private String role;

    // ⚠️ CONSTRUCTEUR VIDE (obligatoire)
    public UtilisateurDto() {
    }

    // ⚠️ CONSTRUCTEUR COMPLET (celui utilisé par le mapper)
    public UtilisateurDto(Long id, String email, String displayName, String role) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.role = role;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}