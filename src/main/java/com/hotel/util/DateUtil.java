package com.hotel.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Classe utilitaire pour la manipulation des dates.
 */
public class DateUtil {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Convertit une LocalDate en String au format yyyy-MM-dd.
     *
     * @param date la date à convertir
     * @return la date formatée en String
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DATE_FORMATTER);
    }

    /**
     * Convertit une String en LocalDate (format yyyy-MM-dd).
     *
     * @param dateString la date en String
     * @return la LocalDate correspondante
     * @throws DateTimeParseException si le format est invalide
     */
    public static LocalDate parseDate(String dateString) throws DateTimeParseException {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }

    /**
     * Formate une date pour l'affichage (dd/MM/yyyy).
     *
     * @param date la date à formater
     * @return la date formatée pour l'affichage
     */
    public static String formatDateForDisplay(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DISPLAY_FORMATTER);
    }

    /**
     * Vérifie si une date est dans le futur.
     *
     * @param date la date à vérifier
     * @return true si la date est dans le futur
     */
    public static boolean isFutureDate(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.isAfter(LocalDate.now());
    }

    /**
     * Vérifie si une date est dans le passé.
     *
     * @param date la date à vérifier
     * @return true si la date est dans le passé
     */
    public static boolean isPastDate(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.isBefore(LocalDate.now());
    }

    /**
     * Calcule le nombre de nuits entre deux dates.
     *
     * @param dateDebut la date de début
     * @param dateFin la date de fin
     * @return le nombre de nuits
     */
    public static int calculateNights(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null || dateFin == null) {
            return 0;
        }
        if (dateFin.isBefore(dateDebut) || dateFin.isEqual(dateDebut)) {
            return 0;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(dateDebut, dateFin);
    }

    /**
     * Vérifie si deux périodes se chevauchent.
     *
     * @param debut1 date de début de la première période
     * @param fin1 date de fin de la première période
     * @param debut2 date de début de la deuxième période
     * @param fin2 date de fin de la deuxième période
     * @return true si les périodes se chevauchent
     */
    public static boolean periodsOverlap(LocalDate debut1, LocalDate fin1, 
                                         LocalDate debut2, LocalDate fin2) {
        if (debut1 == null || fin1 == null || debut2 == null || fin2 == null) {
            return false;
        }
        return !(fin1.isBefore(debut2) || fin2.isBefore(debut1));
    }
}

