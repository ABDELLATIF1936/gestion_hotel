-- =======================================================================================
-- SCRIPT COMPLET DE BASE DE DONNÉES - HOTEL MANAGEMENT SYSTEM
-- Ce script contient : Schéma, Données de Test, Configuration de l'Authentification (Admin)
-- =======================================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- 1. NETTOYAGE (DROP TABLES)
-- =============================================
DROP TABLE IF EXISTS audit_log;
DROP TABLE IF EXISTS lignefacture;
DROP TABLE IF EXISTS facture;
DROP TABLE IF EXISTS inclure;
DROP TABLE IF EXISTS tacheentretien;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS servicesupplementaire;
DROP TABLE IF EXISTS chambre;
DROP TABLE IF EXISTS utilisateur;
DROP TABLE IF EXISTS employer;
DROP TABLE IF EXISTS client;

-- =============================================
-- 2. CRÉATION DU SCHÉMA (TABLES)
-- =============================================

-- Table: client
CREATE TABLE client (
    idClient INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    telephone VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    typeClient VARCHAR(50) NOT NULL DEFAULT 'REGULIER',
    INDEX idx_email (email),
    INDEX idx_type (typeClient)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: chambre
CREATE TABLE chambre (
    numeroChambre INT PRIMARY KEY,
    categorie ENUM('SIMPLE', 'DOUBLE', 'SUITE') NOT NULL DEFAULT 'SIMPLE',
    statut ENUM('DISPONIBLE', 'OCCUPEE', 'HORS_SERVICE', 'RESERVEE') NOT NULL DEFAULT 'DISPONIBLE',
    prixNuit DECIMAL(10, 2) NOT NULL,
    description TEXT,
    INDEX idx_statut (statut),
    INDEX idx_categorie (categorie)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: employer
CREATE TABLE employer (
    idEmploye INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    role ENUM('RECEPTIONNISTE', 'MANAGER', 'ADMINISTRATEUR') NOT NULL DEFAULT 'RECEPTIONNISTE',
    telephone VARCHAR(20),
    email VARCHAR(255),
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_role (role),
    INDEX idx_actif (actif)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: utilisateur (Authentification)
CREATE TABLE utilisateur (
    idUtilisateur INT(11) NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    idEmploye INT(11) NOT NULL,
    role ENUM('ADMIN', 'RECEPTIONNISTE') NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    dateCreation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    dernierLogin TIMESTAMP NULL,
    PRIMARY KEY (idUtilisateur),
    FOREIGN KEY (idEmploye) REFERENCES employer(idEmploye) ON DELETE CASCADE,
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_actif (actif)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: reservation
CREATE TABLE reservation (
    idReservation INT AUTO_INCREMENT PRIMARY KEY,
    idClient INT NOT NULL,
    numeroChambre INT NOT NULL,
    dateDebut DATE NOT NULL,
    dateFin DATE NOT NULL,
    statut ENUM('EN_ATTENTE', 'CONFIRMEE', 'EN_COURS', 'TERMINEE', 'ANNULEE') NOT NULL DEFAULT 'EN_ATTENTE',
    nbPersonnes INT NOT NULL DEFAULT 1,
    notes TEXT,
    check_in DATE NULL,
    check_out DATE NULL,
    dateCreation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (idClient) REFERENCES client(idClient) ON DELETE CASCADE,
    FOREIGN KEY (numeroChambre) REFERENCES chambre(numeroChambre) ON DELETE RESTRICT,
    INDEX idx_client (idClient),
    INDEX idx_chambre (numeroChambre),
    INDEX idx_dates (dateDebut, dateFin),
    INDEX idx_statut (statut),
    CHECK (dateFin > dateDebut),
    CHECK (nbPersonnes > 0 AND nbPersonnes <= 10)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: servicesupplementaire
CREATE TABLE servicesupplementaire (
    idService INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    prix DECIMAL(10, 2) NOT NULL,
    description TEXT,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_actif (actif)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: inclure (Reservation <-> Service)
CREATE TABLE inclure (
    idReservation INT NOT NULL,
    idService INT NOT NULL,
    quantite INT NOT NULL DEFAULT 1,
    prixUnitaire DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (idReservation, idService),
    FOREIGN KEY (idReservation) REFERENCES reservation(idReservation) ON DELETE CASCADE,
    FOREIGN KEY (idService) REFERENCES servicesupplementaire(idService) ON DELETE RESTRICT,
    CHECK (quantite > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: facture
CREATE TABLE facture (
    idFacture INT AUTO_INCREMENT PRIMARY KEY,
    idReservation INT NOT NULL,
    dateEmission DATE NOT NULL,
    montantTotal DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    statut ENUM('EN_ATTENTE', 'PAYEE', 'ANNULEE') NOT NULL DEFAULT 'EN_ATTENTE',
    notes TEXT,
    FOREIGN KEY (idReservation) REFERENCES reservation(idReservation) ON DELETE RESTRICT,
    INDEX idx_reservation (idReservation),
    INDEX idx_statut (statut),
    INDEX idx_date (dateEmission),
    CHECK (montantTotal >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: lignefacture
CREATE TABLE lignefacture (
    idLigne INT AUTO_INCREMENT PRIMARY KEY,
    idFacture INT NOT NULL,
    idService INT NULL,
    description VARCHAR(255) NOT NULL,
    quantite INT NOT NULL DEFAULT 1,
    prixUnitaire DECIMAL(10, 2) NOT NULL,
    montantTotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (idFacture) REFERENCES facture(idFacture) ON DELETE CASCADE,
    FOREIGN KEY (idService) REFERENCES servicesupplementaire(idService) ON DELETE SET NULL,
    INDEX idx_facture (idFacture),
    CHECK (quantite > 0),
    CHECK (prixUnitaire >= 0),
    CHECK (montantTotal >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: tacheentretien
CREATE TABLE tacheentretien (
    idTache INT AUTO_INCREMENT PRIMARY KEY,
    idEmploye INT NULL,
    numeroChambre INT NOT NULL,
    type ENUM('NETTOYAGE', 'REPARATION', 'INSPECTION', 'AUTRE') NOT NULL DEFAULT 'NETTOYAGE',
    date DATE NOT NULL,
    statut ENUM('EN_ATTENTE', 'EN_COURS', 'TERMINEE', 'ANNULEE') NOT NULL DEFAULT 'EN_ATTENTE',
    description TEXT,
    notes TEXT,
    FOREIGN KEY (idEmploye) REFERENCES employer(idEmploye) ON DELETE SET NULL,
    FOREIGN KEY (numeroChambre) REFERENCES chambre(numeroChambre) ON DELETE CASCADE,
    INDEX idx_employe (idEmploye),
    INDEX idx_chambre (numeroChambre),
    INDEX idx_date (date),
    INDEX idx_statut (statut)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: audit_log
CREATE TABLE audit_log (
    idLog INT(11) NOT NULL AUTO_INCREMENT,
    idUtilisateur INT(11) NOT NULL,
    action VARCHAR(100) NOT NULL,
    table_affectee VARCHAR(50) NOT NULL,
    id_enregistrement INT(11) NULL,
    details TEXT NULL,
    date_action TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (idLog),
    FOREIGN KEY (idUtilisateur) REFERENCES utilisateur(idUtilisateur) ON DELETE CASCADE,
    INDEX idx_utilisateur (idUtilisateur),
    INDEX idx_action (action),
    INDEX idx_date (date_action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =============================================
-- 3. INSERTION DONNÉES DE TEST
-- =============================================

-- Clients
INSERT INTO client (nom, prenom, telephone, email, typeClient) VALUES
('Dupont', 'Jean', '0612345678', 'jean.dupont@email.com', 'REGULIER'),
('Martin', 'Marie', '0623456789', 'marie.martin@email.com', 'VIP'),
('Bernard', 'Pierre', '0634567890', 'pierre.bernard@email.com', 'REGULIER'),
('Dubois', 'Sophie', '0645678901', 'sophie.dubois@email.com', 'ENTREPRISE'),
('Moreau', 'Luc', '0656789012', 'luc.moreau@email.com', 'REGULIER');

-- Chambres
INSERT INTO chambre (numeroChambre, categorie, statut, prixNuit, description) VALUES
(101, 'SIMPLE', 'DISPONIBLE', 80.00, 'Chambre simple avec vue sur la cour'),
(102, 'SIMPLE', 'DISPONIBLE', 80.00, 'Chambre simple avec vue sur la cour'),
(201, 'DOUBLE', 'DISPONIBLE', 120.00, 'Chambre double avec lit king-size'),
(202, 'DOUBLE', 'DISPONIBLE', 120.00, 'Chambre double avec lit king-size'),
(203, 'DOUBLE', 'DISPONIBLE', 120.00, 'Chambre double avec balcon'),
(301, 'SUITE', 'DISPONIBLE', 250.00, 'Suite luxueuse avec salon et vue panoramique'),
(302, 'SUITE', 'DISPONIBLE', 250.00, 'Suite luxueuse avec jacuzzi'),
(103, 'SIMPLE', 'HORS_SERVICE', 80.00, 'Chambre en maintenance');

-- Employés
INSERT INTO employer (nom, prenom, role, telephone, email, actif) VALUES
('System', 'Admin', 'ADMINISTRATEUR', '0000000000', 'admin@hotel.com', TRUE),
('Lefebvre', 'Anne', 'RECEPTIONNISTE', '0712345678', 'anne.lefebvre@hotel.com', TRUE),
('Simon', 'Thomas', 'MANAGER', '0745678901', 'thomas.simon@hotel.com', TRUE),
('Michel', 'Laura', 'RECEPTIONNISTE', '0756789012', 'laura.michel@hotel.com', TRUE);

-- Services supplémentaires
INSERT INTO servicesupplementaire (nom, prix, description, actif) VALUES
('Petit-déjeuner', 15.00, 'Petit-déjeuner buffet complet', TRUE),
('Parking', 10.00, 'Place de parking sécurisée', TRUE),
('Spa', 50.00, 'Accès au spa et sauna', TRUE),
('Service en chambre', 25.00, 'Service de restauration en chambre', TRUE),
('WiFi Premium', 5.00, 'Connexion WiFi haut débit', TRUE),
('Mini-bar', 20.00, 'Accès au mini-bar', TRUE);

-- Réservations
INSERT INTO reservation (idClient, numeroChambre, dateDebut, dateFin, statut, nbPersonnes, notes) VALUES
(1, 101, DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'CONFIRMEE', 1, 'Arrivée prévue à 14h'),
(2, 201, DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'CONFIRMEE', 2, 'Anniversaire de mariage'),
(3, 202, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'EN_COURS', 2, NULL),
(4, 301, DATE_ADD(CURDATE(), INTERVAL 7 DAY), DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'EN_ATTENTE', 2, 'Client VIP');

-- Services inclus
INSERT INTO inclure (idReservation, idService, quantite, prixUnitaire) VALUES
(1, 1, 2, 15.00), 
(1, 2, 1, 10.00), 
(2, 1, 3, 15.00), 
(2, 3, 1, 50.00), 
(3, 1, 2, 15.00), 
(4, 1, 3, 15.00), 
(4, 2, 1, 10.00), 
(4, 3, 1, 50.00); 

-- Factures
INSERT INTO facture (idReservation, dateEmission, montantTotal, statut, notes) VALUES
(1, CURDATE(), 0.00, 'EN_ATTENTE', 'Facture à générer à la fin du séjour'),
(2, CURDATE(), 0.00, 'EN_ATTENTE', 'Facture à générer à la fin du séjour'),
(3, CURDATE(), 0.00, 'EN_ATTENTE', 'Facture en cours de génération');


-- =============================================
-- 4. CONFIGURATION COMPTES UTILISATEUR (AUTHENTIFICATION)
-- =============================================

-- Admin (mdp: Admin123!)
INSERT INTO utilisateur (username, password, idEmploye, role, actif) 
SELECT 'admin', '3eb3fe66b31e3b4d10fa70b5cad49c7112294af6ae4e476a1c405155d45aa121', 
       idEmploye, 'ADMIN', TRUE
FROM employer 
WHERE role = 'ADMINISTRATEUR' 
LIMIT 1;

-- Réceptionniste (mdp: Reception123!)
INSERT INTO utilisateur (username, password, idEmploye, role, actif) 
SELECT 'reception', '1b0d33348e535a7cf9b39bd45bd8b7f577a45f7289a554d7d92188e72add5c90', 
       idEmploye, 'RECEPTIONNISTE', TRUE
FROM employer 
WHERE role = 'RECEPTIONNISTE' 
LIMIT 1;

SET FOREIGN_KEY_CHECKS = 1;
SELECT 'Base de données installée avec succès !' AS Status;
