# gestion_hotel
=======
# Système de Gestion Hôtelière

Ce projet est une application de bureau complète pour la gestion hôtelière, réalisée dans le cadre du module **Programmation Orientée Objet (POO)** du Master Sciences et Techniques (RSI) à la FST de Settat.

## 👥 Membres du groupe

Projet réalisé par :
* **Saad ADDAR**
* **Abdellatif HARAKAT**
* **Nadir ALMELLOUKI**
* **Omar EL KHAIRI**

**Encadrant :** Pr. Said El Kafhali  
**Année Universitaire :** 2025-2026

---

## ⚙️ Prérequis techniques

Avant de lancer le projet, assurez-vous de disposer des éléments suivants :
* **Java JDK 25** (ou version 17+ minimum)
* **Maven** (pour la gestion des dépendances et la compilation)
* **MySQL 8.0** (Serveur de base de données)
* **Git** (pour le clonage du dépôt)

---

## 🚀 Instructions pour compiler et exécuter

Suivez ces étapes pour installer et lancer l'application sur votre machine locale :

### 1. Configuration de la Base de Données
1.  Ouvrez votre gestionnaire de base de données (MySQL Workbench, phpMyAdmin, ou ligne de commande).
2.  Créez une base de données vide nommée `gestion_hotel`.
3.  Exécutez le script SQL d'initialisation situé dans le dossier des ressources :
    * Chemin : `src/main/resources/sql/database_init.sql` (ou `database.sql` selon votre fichier).
    * *Ce script créera les tables (Client, Chambre, Reservation, etc.) et insérera le compte administrateur par défaut.*

### 2. Configuration de l'application
1.  Accédez au fichier de configuration de la base de données :
    * `src/main/resources/config/database.properties`
2.  Modifiez, si nécessaire, les identifiants pour correspondre à votre installation MySQL locale :
    ```properties
    db.url=jdbc:mysql://localhost:3306/gestion_hotel
    db.user=root
    db.password=VOTRE_MOT_DE_PASSE
    ```

### 3. Compilation et Lancement (via Maven)
Ouvrez un terminal à la racine du projet (là où se trouve le fichier `pom.xml`) et exécutez les commandes suivantes :

**Pour nettoyer et installer les dépendances :**
```bash
mvn clean install
