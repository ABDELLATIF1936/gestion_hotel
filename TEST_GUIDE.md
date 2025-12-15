# Guide de Test - Système de Gestion d'Hôtel

## Tests de Base

### 1. Test de la Connexion et du Logger

Une classe de test a été créée pour vérifier que tout fonctionne correctement.

#### Exécution du test

**Avec IntelliJ IDEA:**
1. Ouvrez le fichier `src/main/java/com/hotel/TestConnection.java`
2. Clic droit → Run 'TestConnection.main()'

**Avec la ligne de commande (si Java est configuré):**
```bash
cd src/main/java
javac -cp "../../../lib/*:." com/hotel/TestConnection.java
java -cp "../../../lib/*:." com.hotel.TestConnection
```

#### Ce que le test vérifie:

1. **Logger** : Vérifie que le système de logging fonctionne
2. **ConfigReader** : Vérifie que la configuration est lue correctement
3. **DatabaseConnection** : Vérifie que la connexion à MySQL fonctionne

### 2. Configuration de la Base de Données

Avant de lancer les tests, assurez-vous que:

1. **MySQL est démarré** et accessible sur le port configuré (par défaut 3306 ou 3307)
2. **La base de données existe** : `gestion_hotel`
3. **Les credentials sont corrects** dans `src/main/resources/config/database.properties`

#### Création de la base de données:

```sql
CREATE DATABASE gestion_hotel CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### Exécution du schéma:

```bash
mysql -u root -p gestion_hotel < src/main/resources/sql/schema.sql
```

#### Exécution des données de test (optionnel):

```bash
mysql -u root -p gestion_hotel < src/main/resources/sql/data.sql
```

### 3. Vérification des Dépendances

Assurez-vous que toutes les dépendances Maven sont téléchargées:

**Avec IntelliJ IDEA:**
- File → Project Structure → Libraries
- Vérifiez que toutes les dépendances sont présentes

**Avec Maven (si installé):**
```bash
mvn dependency:resolve
```

### 4. Problèmes Courants

#### Erreur: "Cannot connect to database"
- Vérifiez que MySQL est démarré
- Vérifiez le port dans `database.properties` (3306 ou 3307)
- Vérifiez les credentials (username/password)

#### Erreur: "Database 'gestion_hotel' doesn't exist"
- Créez la base de données avec la commande SQL ci-dessus

#### Erreur: "ClassNotFoundException: com.mysql.cj.jdbc.Driver"
- Vérifiez que `mysql-connector-java` est dans le classpath
- Avec Maven: `mvn dependency:resolve`

#### Erreur avec le Logger
- Vérifiez que `logback-classic` est dans les dépendances
- Le fichier `logback.xml` doit être dans `src/main/resources`

### 5. Logs

Les logs sont écrits dans:
- Console (sortie standard)
- Fichier: `logs/hotel-management.log`

Vérifiez les logs pour plus d'informations en cas d'erreur.

## Résultat Attendu

Si tout fonctionne correctement, vous devriez voir:

```
========================================
  TEST DE CONNEXION ET LOGGER
========================================

1. TEST DU LOGGER
-------------------
✓ Logger fonctionne correctement

2. TEST DE CONFIGREADER
------------------------
URL: jdbc:mysql://localhost:3306/gestion_hotel...
Username: root
Password: ***
Driver: com.mysql.cj.jdbc.Driver
Pool min: 5, max: 20
✓ ConfigReader fonctionne correctement

3. TEST DE DATABASECONNECTION
-------------------------------
✓ Instance DatabaseConnection créée
✓ Pool actif: true
Tentative de connexion à la base de données...
✓ Connexion obtenue avec succès
✓ Requête SQL exécutée avec succès
  - Test value: 1
  - Base de données: gestion_hotel
✓ Connexion fermée proprement
✓ Pool de connexions fermé
✓ DatabaseConnection fonctionne correctement

========================================
  TESTS TERMINÉS
========================================
```

