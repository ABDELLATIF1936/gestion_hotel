package com.hotel.model;

import java.time.LocalDateTime;

/**
 * Classe représentant un log d'audit pour tracer les actions sensibles.
 */
public class AuditLog {
    private int idLog;
    private int idUtilisateur;
    private String action;
    private String tableAffectee;
    private Integer idEnregistrement;
    private String details;
    private LocalDateTime dateAction;

    // Constructeurs
    public AuditLog() {
        this.dateAction = LocalDateTime.now();
    }

    public AuditLog(int idUtilisateur, String action, String tableAffectee) {
        this();
        this.idUtilisateur = idUtilisateur;
        this.action = action;
        this.tableAffectee = tableAffectee;
    }

    // Getters et Setters
    public int getIdLog() {
        return idLog;
    }

    public void setIdLog(int idLog) {
        this.idLog = idLog;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTableAffectee() {
        return tableAffectee;
    }

    public void setTableAffectee(String tableAffectee) {
        this.tableAffectee = tableAffectee;
    }

    public Integer getIdEnregistrement() {
        return idEnregistrement;
    }

    public void setIdEnregistrement(Integer idEnregistrement) {
        this.idEnregistrement = idEnregistrement;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getDateAction() {
        return dateAction;
    }

    public void setDateAction(LocalDateTime dateAction) {
        this.dateAction = dateAction;
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "idLog=" + idLog +
                ", idUtilisateur=" + idUtilisateur +
                ", action='" + action + '\'' +
                ", tableAffectee='" + tableAffectee + '\'' +
                ", dateAction=" + dateAction +
                '}';
    }
}


