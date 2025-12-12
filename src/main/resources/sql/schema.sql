-- =============================================
-- 1. NETTOYAGE (On efface tout pour être propre)
-- =============================================
SET FOREIGN_KEY_CHECKS = 0; -- Désactive temporairement la vérification pour pouvoir tout supprimer
DROP TABLE IF EXISTS tacheentretien;
DROP TABLE IF EXISTS lignefacture;
DROP TABLE IF EXISTS facture;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS servicesupplementaire;
DROP TABLE IF EXISTS employer;
DROP TABLE IF EXISTS client;
DROP TABLE IF EXISTS chambre;
SET FOREIGN_KEY_CHECKS = 1; -- Réactive la vérification

-- =============================================
-- 2. CRÉATION DES TABLES PARENTS (Sans dépendances)
-- =============================================

CREATE TABLE `chambre` (
  `numeroChambre` int(11) NOT NULL,
  `categorie` varchar(50) NOT NULL,
  `statut` varchar(20) NOT NULL DEFAULT 'Disponible',
  `prixNuit` decimal(10,2) NOT NULL,
  PRIMARY KEY (`numeroChambre`) -- La clé est définie ICI, Aiven sera content !
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `client` (
  `idClient` int(11) NOT NULL AUTO_INCREMENT,
  `nom` varchar(50) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `telephone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `typeClient` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idClient`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `employer` (
  `idEmploye` int(11) NOT NULL AUTO_INCREMENT,
  `nom` varchar(50) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `role` varchar(50) NOT NULL,
  `login` varchar(50),          -- J'ai gardé tes champs login/mdp pour plus tard
  `motDePasse` varchar(255),
  PRIMARY KEY (`idEmploye`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `servicesupplementaire` (
  `idService` int(11) NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) NOT NULL,
  `prix` decimal(10,2) NOT NULL,
  PRIMARY KEY (`idService`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- 3. CRÉATION DES TABLES ENFANTS (Avec dépendances)
-- =============================================

CREATE TABLE `reservation` (
  `idReservation` int(11) NOT NULL AUTO_INCREMENT,
  `idClient` int(11) NOT NULL,
  `numeroChambre` int(11) NOT NULL,
  `dateDebut` date NOT NULL,
  `dateFin` date NOT NULL,
  `statut` varchar(20) NOT NULL DEFAULT 'Confirmée',
  `nbPersonnes` int(11) NOT NULL,
  PRIMARY KEY (`idReservation`),
  KEY `idClient` (`idClient`),
  KEY `numeroChambre` (`numeroChambre`),
  CONSTRAINT `reservation_ibfk_1` FOREIGN KEY (`idClient`) REFERENCES `client` (`idClient`),
  CONSTRAINT `reservation_ibfk_2` FOREIGN KEY (`numeroChambre`) REFERENCES `chambre` (`numeroChambre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `facture` (
  `idFacture` int(11) NOT NULL AUTO_INCREMENT,
  `idReservation` int(11) NOT NULL,
  `dateEmission` date NOT NULL DEFAULT (CURRENT_DATE),
  `montantTotal` decimal(10,2) NOT NULL,
  `statut` varchar(20) NOT NULL DEFAULT 'En attente',
  PRIMARY KEY (`idFacture`),
  KEY `idReservation` (`idReservation`),
  CONSTRAINT `facture_ibfk_1` FOREIGN KEY (`idReservation`) REFERENCES `reservation` (`idReservation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `lignefacture` (
  `idLigne` int(11) NOT NULL AUTO_INCREMENT,
  `idFacture` int(11) NOT NULL,
  `idService` int(11) DEFAULT NULL,
  `description` varchar(255) NOT NULL,
  `quantite` int(11) NOT NULL DEFAULT 1,
  `prixUnitaire` decimal(10,2) NOT NULL,
  PRIMARY KEY (`idLigne`),
  KEY `idFacture` (`idFacture`),
  KEY `idService` (`idService`),
  CONSTRAINT `lignefacture_ibfk_1` FOREIGN KEY (`idFacture`) REFERENCES `facture` (`idFacture`),
  CONSTRAINT `lignefacture_ibfk_2` FOREIGN KEY (`idService`) REFERENCES `servicesupplementaire` (`idService`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `tacheentretien` (
  `idTache` int(11) NOT NULL AUTO_INCREMENT,
  `idEmploye` int(11) NOT NULL,
  `numeroChambre` int(11) NOT NULL,
  `type` varchar(50) NOT NULL,
  `date` date NOT NULL,
  `statut` varchar(20) NOT NULL DEFAULT 'En attente',
  PRIMARY KEY (`idTache`),
  KEY `idEmploye` (`idEmploye`),
  KEY `numeroChambre` (`numeroChambre`),
  CONSTRAINT `tacheentretien_ibfk_1` FOREIGN KEY (`idEmploye`) REFERENCES `employer` (`idEmploye`),
  CONSTRAINT `tacheentretien_ibfk_2` FOREIGN KEY (`numeroChambre`) REFERENCES `chambre` (`numeroChambre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. DONNÉES DE TEST (Optionnel - Pour vérifier que ça marche)
INSERT INTO employer (nom, prenom, role, login, motDePasse) VALUES ('Admin', 'System', 'Admin', 'admin', 'admin123');