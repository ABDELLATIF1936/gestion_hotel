# Système de Gestion d'Hôtel

Application JavaFX complète pour la gestion d'un hôtel avec architecture MVC et design patterns.

## 📋 Description

Cette application permet de gérer :
- Les chambres (disponibilité, statut, catégories)
- Les réservations (création, modification, annulation)
- Les clients (enregistrement, historique)
- La facturation (génération automatique, export PDF)
- Les services supplémentaires
- Le personnel et les tâches d'entretien

## 🛠️ Technologies

- **Java 11+**
- **JavaFX 17** (Interface graphique)
- **MySQL 8.0** (Base de données)
- **Maven** (Gestion des dépendances)
- **HikariCP** (Pool de connexions)
- **iTextPDF** (Génération de factures PDF)
- **JUnit 5** (Tests unitaires)

## 📁 Structure du Projet

```
hotel_management_system/
├── src/
│   ├── main/
│   │   ├── java/com/hotel/
│   │   │   ├── model/          # Entités métier
│   │   │   ├── dao/            # Accès aux données
│   │   │   ├── service/        # Logique métier
│   │   │   ├── controller/     # Contrôleurs MVC
│   │   │   ├── view/           # Interface JavaFX
│   │   │   ├── util/           # Classes utilitaires
│   │   │   ├── factory/        # Design Pattern Factory
│   │   │   ├── strategy/       # Design Pattern Strategy
│   │   │   ├── observer/       # Design Pattern Observer
│   │   │   └── exception/      # Exceptions personnalisées
│   │   └── resources/
│   │       ├── config/         # Configuration
│   │       ├── fxml/           # Fichiers FXML
│   │       ├── css/            # Styles CSS
│   │       └── sql/            # Scripts SQL
│   └── test/                   # Tests unitaires
├── lib/                        # Bibliothèques externes
├── docs/                       # Documentation
└── pom.xml                     # Configuration Maven
```

## 🚀 Installation

### Prérequis

- Java JDK 11 ou supérieur
- Maven 3.6+
- MySQL 8.0+
- IDE (IntelliJ IDEA recommandé)

### Étapes d'installation

1. **Cloner ou télécharger le projet**
   ```bash
   cd hotel_management_system
   ```

2. **Créer la base de données MySQL**
   ```sql
   CREATE DATABASE gestion_hotel;
   ```

3. **Exécuter les scripts SQL**
   - Exécuter `src/main/resources/sql/schema.sql` pour créer les tables
   - Exécuter `src/main/resources/sql/data.sql` pour les données de test (optionnel)

4. **Configurer la connexion à la base de données**
   - Modifier `src/main/resources/config/database.properties`
   - Mettre à jour `db.url`, `db.username`, et `db.password`

5. **Installer les dépendances Maven**
   ```bash
   mvn clean install
   ```

## ▶️ Exécution

### Avec Maven
```bash
mvn javafx:run
```

### Avec IntelliJ IDEA
1. Ouvrir le projet dans IntelliJ IDEA
2. Configurer le SDK Java 11+
3. Exécuter la classe `com.hotel.Main`

### Compilation manuelle
```bash
mvn compile
java --module-path <path-to-javafx> --add-modules javafx.controls,javafx.fxml -cp target/classes com.hotel.Main
```

## 🗄️ Configuration de la Base de Données

Le fichier `src/main/resources/config/database.properties` contient la configuration de connexion :

```properties
db.url=jdbc:mysql://localhost:3306/gestion_hotel
db.username=root
db.password=votre_mot_de_passe
```

## 📊 Schéma de Base de Données

La base de données contient les tables suivantes :
- `chambre` - Informations sur les chambres
- `client` - Données des clients
- `employer` - Informations du personnel
- `reservation` - Réservations
- `facture` - Factures
- `lignefacture` - Lignes de facture
- `servicesupplementaire` - Services proposés
- `tacheentretien` - Tâches de maintenance

## 🧪 Tests

Exécuter les tests unitaires :
```bash
mvn test
```

## 📚 Documentation

- **Javadoc** : Générer avec `mvn javafx:javadoc`
- **Diagrammes UML** : Disponibles dans `docs/uml/`
- **Rapport** : Voir `docs/rapport.pdf`

## 🏗️ Architecture

L'application suit une architecture **MVC** avec :
- **Model** : Entités métier dans `model/`
- **View** : Interfaces JavaFX dans `view/`
- **Controller** : Contrôleurs dans `controller/`

### Design Patterns implémentés :
- **DAO** : Accès aux données
- **Factory** : Création d'objets
- **Strategy** : Stratégies de tarification
- **Observer** : Notifications d'événements
- **Singleton** : Gestionnaires uniques

## 👥 Auteurs

Projet académique - Système de Gestion d'Hôtel

## 📝 Licence

Ce projet est destiné à un usage académique.

## 🔧 Dépannage

### Problème de connexion à la base de données
- Vérifier que MySQL est démarré
- Vérifier les credentials dans `database.properties`
- Vérifier que la base de données `gestion_hotel` existe

### Problème avec JavaFX
- Vérifier que JavaFX SDK est correctement configuré
- Utiliser `mvn javafx:run` pour l'exécution

### Erreurs de compilation
- Vérifier la version Java (minimum 11)
- Exécuter `mvn clean install`

---

**Date de création** : Décembre 2024  
**Version** : 1.0.0

