# Guide de Lancement de l'Application

## Méthode 1 : Avec IntelliJ IDEA (Recommandé)

### Étapes :

1. **Ouvrir le projet dans IntelliJ IDEA**
   - File → Open → Sélectionner le dossier `hotel_management_system`

2. **Configurer le SDK Java**
   - File → Project Structure → Project
   - Vérifier que le SDK est configuré (Java 11 ou supérieur)

3. **Résoudre les dépendances Maven**
   - Clic droit sur `pom.xml` → Maven → Reload Project
   - Ou : View → Tool Windows → Maven → Cliquer sur l'icône "Reload All Maven Projects"

4. **Vérifier la configuration de la base de données**
   - Ouvrir `src/main/resources/config/database.properties`
   - Vérifier que les paramètres sont corrects :
     - `db.url` : URL de votre base MySQL
     - `db.username` : Votre nom d'utilisateur MySQL
     - `db.password` : Votre mot de passe MySQL

5. **Créer la base de données (si pas encore fait)**
   ```sql
   CREATE DATABASE gestion_hotel CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
   
   Puis exécuter les scripts SQL :
   - `src/main/resources/sql/schema.sql` (création des tables)
   - `src/main/resources/sql/data.sql` (données de test - optionnel)

6. **Lancer l'application**
   - Ouvrir `src/main/java/com/hotel/Main.java`
   - Clic droit sur la classe `Main` → Run 'Main.main()'
   - Ou utiliser le raccourci : `Shift + F10`

### Configuration JavaFX dans IntelliJ (si nécessaire)

Si vous avez des erreurs liées à JavaFX :

1. File → Project Structure → Libraries
2. Ajouter le SDK JavaFX si nécessaire
3. Ou utiliser le plugin JavaFX Maven qui est déjà configuré dans `pom.xml`

---

## Méthode 2 : Avec Maven en ligne de commande

### Prérequis :
- Maven installé et dans le PATH
- Java 11+ installé
- MySQL démarré

### Commandes :

```bash
# 1. Se placer dans le dossier du projet
cd C:\Users\DELL\IdeaProjects\hotel_management_system

# 2. Compiler le projet
mvn clean compile

# 3. Lancer l'application
mvn javafx:run
```

---

## Méthode 3 : Exécution directe avec Java

### Prérequis :
- Java 11+ installé
- Toutes les dépendances dans le classpath

### Commandes :

```bash
# Compiler (si pas déjà fait)
javac -cp "lib/*:target/classes" src/main/java/com/hotel/Main.java

# Exécuter
java -cp "lib/*:target/classes" com.hotel.Main
```

---

## Vérifications Avant le Lancement

### ✅ Checklist :

- [ ] MySQL est démarré et accessible
- [ ] La base de données `gestion_hotel` existe
- [ ] Les tables sont créées (exécuter `schema.sql`)
- [ ] Les credentials dans `database.properties` sont corrects
- [ ] Les dépendances Maven sont téléchargées
- [ ] Java 11+ est configuré

---

## Résolution de Problèmes

### Erreur : "Cannot connect to database"
- Vérifier que MySQL est démarré
- Vérifier les credentials dans `database.properties`
- Vérifier que le port MySQL est correct (3306 ou 3307)

### Erreur : "Database 'gestion_hotel' doesn't exist"
- Créer la base de données avec la commande SQL ci-dessus
- Exécuter `schema.sql` pour créer les tables

### Erreur : "JavaFX runtime components are missing"
- Vérifier que JavaFX est dans les dépendances Maven
- Utiliser `mvn javafx:run` au lieu de `java` directement

### Erreur : "ClassNotFoundException"
- Exécuter `mvn clean install` pour compiler et installer les dépendances
- Vérifier que toutes les dépendances sont dans `pom.xml`

---

## Interface de l'Application

Une fois lancée, vous devriez voir :

1. **Sidebar gauche** : Navigation entre les modules
   - 📊 Tableau de bord
   - 👥 Clients
   - 🛏️ Chambres
   - 📅 Réservations
   - 💰 Facturation
   - ⭐ Services
   - 🔧 Entretien

2. **Zone centrale** : Contenu du module sélectionné

3. **Tableau de bord** : Statistiques en temps réel
   - Total clients
   - Chambres disponibles
   - Réservations actives
   - Factures en attente
   - Taux d'occupation
   - Revenus du mois

---

## Test de l'Application

### Test 1 : Tableau de bord
1. Lancer l'application
2. Vérifier que le tableau de bord s'affiche
3. Vérifier que les statistiques se chargent

### Test 2 : Gestion des clients
1. Cliquer sur "👥 Clients"
2. Tester l'ajout d'un client
3. Tester la recherche
4. Tester la modification
5. Tester la suppression

### Test 3 : Navigation
1. Naviguer entre les différents modules
2. Vérifier que chaque module s'affiche correctement

---

## Commandes Utiles

```bash
# Nettoyer et compiler
mvn clean compile

# Compiler et exécuter les tests
mvn test

# Générer la Javadoc
mvn javadoc:javadoc

# Créer un JAR exécutable
mvn clean package
```

---

## Support

En cas de problème :
1. Vérifier les logs dans `logs/hotel-management.log`
2. Vérifier la console pour les messages d'erreur
3. Vérifier que toutes les dépendances sont installées

