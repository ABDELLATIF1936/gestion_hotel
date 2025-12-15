package com.hotel.util;

import com.hotel.exception.ValidationException;
import java.util.regex.Pattern;

/**
 * Classe utilitaire pour la validation des données.
 */
public class ValidationUtil {
    // Patterns de validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[0-9]{10,15}$"
    );

    /**
     * Valide une adresse email.
     *
     * @param email l'email à valider
     * @throws ValidationException si l'email est invalide
     */
    public static void validateEmail(String email) throws ValidationException {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("L'email ne peut pas être vide");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Format d'email invalide");
        }
    }

    /**
     * Valide un numéro de téléphone.
     *
     * @param phone le numéro de téléphone à valider
     * @throws ValidationException si le numéro est invalide
     */
    public static void validatePhone(String phone) throws ValidationException {
        if (phone == null || phone.trim().isEmpty()) {
            throw new ValidationException("Le numéro de téléphone ne peut pas être vide");
        }
        String cleanedPhone = phone.replaceAll("[\\s-()]", "");
        if (!PHONE_PATTERN.matcher(cleanedPhone).matches()) {
            throw new ValidationException("Format de numéro de téléphone invalide (10-15 chiffres requis)");
        }
    }

    /**
     * Valide qu'une chaîne n'est pas vide.
     *
     * @param value la valeur à valider
     * @param fieldName le nom du champ (pour le message d'erreur)
     * @throws ValidationException si la valeur est vide
     */
    public static void validateNotEmpty(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " ne peut pas être vide");
        }
    }

    /**
     * Valide qu'un nombre est positif.
     *
     * @param value la valeur à valider
     * @param fieldName le nom du champ
     * @throws ValidationException si la valeur n'est pas positive
     */
    public static void validatePositive(double value, String fieldName) throws ValidationException {
        if (value <= 0) {
            throw new ValidationException(fieldName + " doit être positif");
        }
    }

    /**
     * Valide qu'un nombre est positif ou zéro.
     *
     * @param value la valeur à valider
     * @param fieldName le nom du champ
     * @throws ValidationException si la valeur est négative
     */
    public static void validateNonNegative(double value, String fieldName) throws ValidationException {
        if (value < 0) {
            throw new ValidationException(fieldName + " ne peut pas être négatif");
        }
    }

    /**
     * Valide qu'un entier est dans une plage donnée.
     *
     * @param value la valeur à valider
     * @param min la valeur minimale
     * @param max la valeur maximale
     * @param fieldName le nom du champ
     * @throws ValidationException si la valeur est hors plage
     */
    public static void validateRange(int value, int min, int max, String fieldName) throws ValidationException {
        if (value < min || value > max) {
            throw new ValidationException(fieldName + " doit être entre " + min + " et " + max);
        }
    }
}

