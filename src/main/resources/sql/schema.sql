-- =============================================
-- Schéma de base de données pour le système de gestion d'hôtel
-- Base de données: gestion_hotel
-- =============================================

-- Suppression des tables si elles existent (dans l'ordre inverse des dépendances)
DROP TABLE IF EXISTS lignefacture;
DROP TABLE IF EXISTS facture;
DROP TABLE IF EXISTS inclure;
DROP TABLE IF EXISTS tacheentretien;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS servicesupplementaire;
DROP TABLE IF EXISTS chambre;
DROP TABLE IF EXISTS client;
DROP TABLE IF EXISTS employer;

-- =============================================
-- Table: client
-- =============================================
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

-- =============================================
-- Table: chambre
-- =============================================
CREATE TABLE chambre (
    numeroChambre INT PRIMARY KEY,
    categorie ENUM('SIMPLE', 'DOUBLE', 'SUITE') NOT NULL DEFAULT 'SIMPLE',
    statut ENUM('DISPONIBLE', 'OCCUPEE', 'HORS_SERVICE', 'RESERVEE') NOT NULL DEFAULT 'DISPONIBLE',
    prixNuit DECIMAL(10, 2) NOT NULL,
    description TEXT,
    INDEX idx_statut (statut),
    INDEX idx_categorie (categorie)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Table: employer
-- =============================================
CREATE TABLE employer (
    idEmploye INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    role ENUM('RECEPTIONNISTE', 'ENTRETIEN', 'MANAGER', 'ADMINISTRATEUR') NOT NULL DEFAULT 'ENTRETIEN',
    telephone VARCHAR(20),
    email VARCHAR(255),
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_role (role),
    INDEX idx_actif (actif)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Table: reservation
-- =============================================
CREATE TABLE reservation (
    idReservation INT AUTO_INCREMENT PRIMARY KEY,
    idClient INT NOT NULL,
    numeroChambre INT NOT NULL,
    dateDebut DATE NOT NULL,
    dateFin DATE NOT NULL,
    statut ENUM('EN_ATTENTE', 'CONFIRMEE', 'EN_COURS', 'TERMINEE', 'ANNULEE') NOT NULL DEFAULT 'EN_ATTENTE',
    nbPersonnes INT NOT NULL DEFAULT 1,
    notes TEXT,
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

-- =============================================
-- Table: servicesupplementaire
-- =============================================
CREATE TABLE servicesupplementaire (
    idService INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    prix DECIMAL(10, 2) NOT NULL,
    description TEXT,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_actif (actif)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Table: inclure (relation entre reservation et service)
-- =============================================
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

-- =============================================
-- Table: facture
-- =============================================
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

-- =============================================
-- Table: lignefacture
-- =============================================
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

-- =============================================
-- Table: tacheentretien
-- =============================================
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

-- =============================================
-- Vues utiles (optionnel)
-- =============================================

-- Vue pour les réservations actives
CREATE OR REPLACE VIEW v_reservations_actives AS
SELECT r.*, c.nom as clientNom, c.prenom as clientPrenom, ch.categorie, ch.prixNuit
FROM reservation r
JOIN client c ON r.idClient = c.idClient
JOIN chambre ch ON r.numeroChambre = ch.numeroChambre
WHERE r.statut IN ('CONFIRMEE', 'EN_COURS')
AND r.dateFin >= CURDATE();

-- Vue pour les chambres disponibles
CREATE OR REPLACE VIEW v_chambres_disponibles AS
SELECT ch.*, 
       COUNT(r.idReservation) as nbReservationsActives
FROM chambre ch
LEFT JOIN reservation r ON ch.numeroChambre = r.numeroChambre 
    AND r.statut IN ('CONFIRMEE', 'EN_COURS')
    AND CURDATE() BETWEEN r.dateDebut AND r.dateFin
WHERE ch.statut = 'DISPONIBLE'
GROUP BY ch.numeroChambre;

