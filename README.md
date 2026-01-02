# Système de Gestion Hôtelière (Hotel Management System)

Application de gestion hôtelière complète développée en **Java 11**, **JavaFX** et **MySQL**.  
Conçue dans le cadre d'un projet académique, elle respecte une architecture MVC rigoureuse et implémente plusieurs Design Patterns.

## 🚀 Fonctionnalités Clés

### 1. Gestion des Réservations (Front Office)
*   **Création** : Sélection intuitive des dates avec vérification de disponibilité en temps réel.
*   **Check-in / Check-out** : Suivi précis des dates d'arrivée et de départ.
*   **Filtres** : Recherche avancée par nom client, date ou statut.
*   **Services** : Ajout de services optionnels (Petit-déjeuner, Spa, Parking...) lors de la réservation.

### 2. Facturation & Comptabilité
*   **Génération Automatique** : Calcul du montant total incluant nuitées et services.
*   **Export PDF** : Bouton dédié pour générer et télécharger une facture professionnelle au format PDF.
*   **Suivi** : Gestion des statuts de paiement (Payée, En attente).

### 3. Gestion des Chambres & Entretien (Housekeeping)
*   **État des Lieux** : Suivi des statuts (Disponible, Occupée, Hors Service).
*   **Entretien** : Module dédié pour assigner et suivre les tâches de nettoyage/réparation.

### 4. Sécurité & Rôles (RBAC)
Système robuste de contrôle d'accès basé sur les rôles :
*   **Administrateur (ADMIN)** : Accès total (Gestion employés, configuration, utilisateurs).
*   **Réceptionniste** : Accès restreint (Réservations, Clients, Factures).
    *   *Restriction* : Ne peut PAS supprimer de clients ni créer/supprimer des chambres (lecture seule/modif statut uniquement).

## 🛠technologies Utilisées

*   **Langage** : Java 11
*   **Interface Graphique** : JavaFX 17
*   **Base de Données** : MySQL 8.0
*   **ORM / DAO** : JDBC natif avec Pattern DAO
*   **PDF** : iTextPDF 5.5.13
*   **Gestion de dépendances** : Maven

## 📂 Structure du Projet

```
Com.hotel
├── controller   # Contrôleurs (Logique de présentation)
├── model        # Entités Métier (Reservation, Chambre...)
├── view         # Vues JavaFX (IHM)
├── service      # Logique Métier (Interfaces & Implémentations)
├── dao          # Accès aux Données (Pattern DAO)
├── security     # Gestion des Permissions (RBAC)
└── util         # Utilitaires (Connexion DB, PDF, Logs)
```

## 📋 Installation & Démarrage

1.  **Base de Données** :
    *   Ouvrez votre gestionnaire MySQL (phpMyAdmin, Workbench).
    *   Créez une base de données nommée `gestion_hotel`.
    *   Importez le fichier **`database.sql`** situé à la racine du projet.

2.  **Configuration** :
    *   Vérifiez les paramètres de connexion dans `com.hotel.util.DatabaseConnection` si nécessaire (défaut: `root`/` `).

3.  **Lancement** :
    *   Exécutez la classe principale : `com.hotel.Main`.

4.  **Connexion** :
    *   **Admin** : `admin` / `Admin123!`
    *   **Réception** : `reception` / `Reception123!`

## 📐 Conception (Diagrammes)

Le dossier `diagrammes/` contient la documentation technique complète :
*   **`class_diagram_logical.puml`** : Architecture en couches.
*   **`class_diagram_patterns.puml`** : Patterns Factory, Observer, Strategy.
*   **`sequence_diagram_*.puml`** : Flux détaillés (Login, Réservation, Facturation).

---
*Projet Académique - 2024/2025*
