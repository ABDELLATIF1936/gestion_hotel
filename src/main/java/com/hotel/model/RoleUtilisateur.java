package com.hotel.model;

/**
 * Enumération des rôles utilisateurs dans le système.
 */
public enum RoleUtilisateur {
    ADMIN("ADMIN", "Administrateur"),
    RECEPTIONNISTE("RECEPTIONNISTE", "Réceptionniste");

    private final String role;
    private final String libelle;

    RoleUtilisateur(String role, String libelle) {
        this.role = role;
        this.libelle = libelle;
    }

    public String getRoleAsString() {
        return role;
    }

    public String getLibelle() {
        return libelle;
    }

    /**
     * Parse une chaîne de caractères en RoleUtilisateur.
     *
     * @param role la chaîne à parser
     * @return le RoleUtilisateur correspondant
     * @throws IllegalArgumentException si le rôle n'est pas reconnu
     */
    public static RoleUtilisateur parseRole(String role) {
        if (role == null) {
            throw new IllegalArgumentException("Le rôle ne peut pas être null");
        }

        String roleUpper = role.toUpperCase().trim();
        for (RoleUtilisateur r : values()) {
            if (r.role.equals(roleUpper)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Rôle non reconnu: " + role);
    }
}
