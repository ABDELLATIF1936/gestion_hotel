# Architecture du Projet - Système de Gestion d'Hôtel

## 📐 Structure du Projet

La structure du projet suit les meilleures pratiques Java avec une séparation claire des responsabilités selon le pattern MVC.

## 🏗️ Organisation des Packages

### `com.hotel.model`
Contient toutes les entités métier (POJOs) représentant les tables de la base de données :
- `Client.java` - Représente un client
- `Reservation.java` - Représente une réservation
- `Chambre.java` - Représente une chambre
- `Facture.java` - Représente une facture
- `LigneFacture.java` - Ligne détaillée d'une facture
- `ServiceSupplementaire.java` - Service proposé par l'hôtel
- `TacheEntretien.java` - Tâche de maintenance
- `Employe.java` - Employé de l'hôtel
- `Inclure.java` - Relation entre réservation et services

### `com.hotel.dao`
Couche d'accès aux données avec pattern DAO :
- **interfaces/** : Définit les contrats pour l'accès aux données
- **impl/** : Implémentations concrètes avec JDBC

### `com.hotel.service`
Couche de logique métier :
- **interfaces/** : Contrats des services métier
- **impl/** : Implémentations avec validation et règles métier

### `com.hotel.controller`
Contrôleurs MVC pour gérer les interactions entre la vue et le modèle.

### `com.hotel.view`
Interface graphique JavaFX :
- Vues principales pour chaque module
- **components/** : Composants réutilisables (factories)

### `com.hotel.util`
Classes utilitaires :
- `DatabaseConnection.java` - Gestion du pool de connexions
- `DateUtil.java` - Utilitaires pour les dates
- `ValidationUtil.java` - Validation des données
- `ConfigReader.java` - Lecture de la configuration
- `Logger.java` - Gestion des logs

### `com.hotel.factory`
Design Pattern Factory pour la création d'objets.

### `com.hotel.strategy`
Design Pattern Strategy pour les stratégies de tarification.

### `com.hotel.observer`
Design Pattern Observer pour les notifications d'événements.

### `com.hotel.exception`
Exceptions personnalisées pour une meilleure gestion des erreurs.

## 🔧 Configuration

### Maven (pom.xml)
- **Java 11** : Version minimale requise
- **JavaFX 17.0.2** : Framework d'interface graphique
- **MySQL Connector 8.0.33** : Driver JDBC pour MySQL
- **HikariCP 5.0.1** : Pool de connexions performant
- **Hibernate 5.6.15** : ORM (optionnel, pour évolution future)
- **iTextPDF 5.5.13.2** : Génération de factures PDF
- **JUnit 5.9.2** : Framework de tests
- **SLF4J + Logback** : Système de logging

### Base de Données
Configuration dans `src/main/resources/config/database.properties` :
- URL de connexion MySQL
- Credentials
- Paramètres du pool de connexions HikariCP

## 📊 Design Patterns Implémentés

1. **MVC (Model-View-Controller)** : Architecture principale
2. **DAO (Data Access Object)** : Abstraction de l'accès aux données
3. **Factory** : Création d'objets (DAO, Service, View)
4. **Strategy** : Stratégies de tarification (standard, saisonnière, groupe)
5. **Observer** : Notifications (changements de statut, nouvelles réservations)
6. **Singleton** : Pour les gestionnaires uniques (à implémenter)

## 🗄️ Base de Données

### Schéma
La base de données `gestion_hotel` contient :
- Tables principales : chambre, client, employer, reservation, facture
- Tables de liaison : lignefacture, tacheentretien
- Table de services : servicesupplementaire

### Scripts SQL
- `schema.sql` : Création des tables et contraintes
- `data.sql` : Données de test (optionnel)

## 📝 Prochaines Étapes

1. Implémenter `DatabaseConnection` avec HikariCP
2. Créer les entités model avec validation
3. Implémenter les DAO avec JDBC
4. Développer les services métier
5. Créer les contrôleurs JavaFX
6. Développer les vues FXML
7. Ajouter les styles CSS
8. Implémenter les tests unitaires

## 🔍 Points d'Attention

- **Transactions** : Utiliser des transactions pour les opérations critiques
- **Validation** : Valider toutes les entrées utilisateur
- **Exceptions** : Utiliser les exceptions personnalisées
- **Logging** : Logger toutes les opérations importantes
- **Performance** : Utiliser le pool de connexions efficacement
- **Sécurité** : Ne jamais exposer les credentials en dur

---

**Date de création** : Décembre 2024  
**Version** : 1.0.0

