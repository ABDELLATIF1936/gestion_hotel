package com.hotel.security;

import com.hotel.model.RoleUtilisateur;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Gestionnaire des permissions basé sur les rôles.
 */
public class PermissionManager {
    private static final Map<RoleUtilisateur, Set<String>> PERMISSIONS = new HashMap<>();
    
    static {
        // Permissions ADMINISTRATEUR
        Set<String> adminPerms = new HashSet<>();
        adminPerms.add("gestion.utilisateurs");
        adminPerms.add("gestion.employes");
        adminPerms.add("gestion.chambres");
        adminPerms.add("gestion.clients");
        adminPerms.add("gestion.reservations");
        adminPerms.add("gestion.factures");
        adminPerms.add("gestion.services");
        adminPerms.add("gestion.entretien");
        adminPerms.add("voir.statistiques");
        adminPerms.add("voir.rapports");
        PERMISSIONS.put(RoleUtilisateur.ADMIN, adminPerms);
        
        // Permissions RÉCEPTIONNISTE
        Set<String> receptionPerms = new HashSet<>();
        receptionPerms.add("gestion.reservations");
        receptionPerms.add("gestion.clients");
        receptionPerms.add("gestion.chambres");
        receptionPerms.add("gestion.factures");
        receptionPerms.add("gestion.services");
        receptionPerms.add("voir.statistiques");
        PERMISSIONS.put(RoleUtilisateur.RECEPTIONNISTE, receptionPerms);
        
        // Permissions PERSONNEL D'ENTRETIEN
        Set<String> entretienPerms = new HashSet<>();
        entretienPerms.add("gestion.entretien");
        entretienPerms.add("gestion.chambres");
        entretienPerms.add("voir.taches");
        PERMISSIONS.put(RoleUtilisateur.ENTRETIEN, entretienPerms);
    }
    
    /**
     * Vérifie si un rôle a une permission spécifique.
     *
     * @param role le rôle à vérifier
     * @param permission la permission à vérifier
     * @return true si le rôle a la permission
     */
    public static boolean hasPermission(RoleUtilisateur role, String permission) {
        if (role == null || permission == null) {
            return false;
        }
        
        Set<String> rolePerms = PERMISSIONS.get(role);
        return rolePerms != null && rolePerms.contains(permission);
    }
    
    /**
     * Récupère toutes les permissions d'un rôle.
     *
     * @param role le rôle
     * @return un set des permissions
     */
    public static Set<String> getPermissions(RoleUtilisateur role) {
        Set<String> perms = PERMISSIONS.get(role);
        return perms != null ? new HashSet<>(perms) : new HashSet<>();
    }
}


