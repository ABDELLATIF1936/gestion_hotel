-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 09, 2025 at 07:55 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `gestion_hotel`
--

-- --------------------------------------------------------

--
-- Table structure for table `chambre`
--

CREATE TABLE `chambre` (
                           `numeroChambre` int(11) NOT NULL,
                           `categorie` varchar(50) NOT NULL,
                           `statut` varchar(20) NOT NULL DEFAULT 'Disponible',
                           `prixNuit` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `client`
--

CREATE TABLE `client` (
                          `idClient` int(11) NOT NULL,
                          `nom` varchar(50) NOT NULL,
                          `prenom` varchar(50) NOT NULL,
                          `telephone` varchar(20) DEFAULT NULL,
                          `email` varchar(100) DEFAULT NULL,
                          `typeClient` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `employer`
--

CREATE TABLE `employer` (
                            `idEmploye` int(11) NOT NULL,
                            `nom` varchar(50) NOT NULL,
                            `prenom` varchar(50) NOT NULL,
                            `role` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `facture`
--

CREATE TABLE `facture` (
                           `idFacture` int(11) NOT NULL,
                           `idReservation` int(11) NOT NULL,
                           `dateEmission` date NOT NULL DEFAULT curdate(),
                           `montantTotal` decimal(10,2) NOT NULL,
                           `statut` varchar(20) NOT NULL DEFAULT 'En attente'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `lignefacture`
--

CREATE TABLE `lignefacture` (
                                `idLigne` int(11) NOT NULL,
                                `idFacture` int(11) NOT NULL,
                                `idService` int(11) DEFAULT NULL,
                                `description` varchar(255) NOT NULL,
                                `quantite` int(11) NOT NULL DEFAULT 1,
                                `prixUnitaire` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `reservation`
--

CREATE TABLE `reservation` (
                               `idReservation` int(11) NOT NULL,
                               `idClient` int(11) NOT NULL,
                               `numeroChambre` int(11) NOT NULL,
                               `dateDebut` date NOT NULL,
                               `dateFin` date NOT NULL,
                               `statut` varchar(20) NOT NULL DEFAULT 'Confirmée',
                               `nbPersonnes` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `servicesupplementaire`
--

CREATE TABLE `servicesupplementaire` (
                                         `idService` int(11) NOT NULL,
                                         `nom` varchar(100) NOT NULL,
                                         `prix` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tacheentretien`
--

CREATE TABLE `tacheentretien` (
                                  `idTache` int(11) NOT NULL,
                                  `idEmploye` int(11) NOT NULL,
                                  `numeroChambre` int(11) NOT NULL,
                                  `type` varchar(50) NOT NULL,
                                  `date` date NOT NULL,
                                  `statut` varchar(20) NOT NULL DEFAULT 'En attente'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `chambre`
--
ALTER TABLE `chambre`
    ADD PRIMARY KEY (`numeroChambre`);

--
-- Indexes for table `client`
--
ALTER TABLE `client`
    ADD PRIMARY KEY (`idClient`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `employer`
--
ALTER TABLE `employer`
    ADD PRIMARY KEY (`idEmploye`);

--
-- Indexes for table `facture`
--
ALTER TABLE `facture`
    ADD PRIMARY KEY (`idFacture`),
  ADD KEY `idReservation` (`idReservation`);

--
-- Indexes for table `lignefacture`
--
ALTER TABLE `lignefacture`
    ADD PRIMARY KEY (`idLigne`),
  ADD KEY `idFacture` (`idFacture`),
  ADD KEY `idService` (`idService`);

--
-- Indexes for table `reservation`
--
ALTER TABLE `reservation`
    ADD PRIMARY KEY (`idReservation`),
  ADD KEY `idClient` (`idClient`),
  ADD KEY `numeroChambre` (`numeroChambre`);

--
-- Indexes for table `servicesupplementaire`
--
ALTER TABLE `servicesupplementaire`
    ADD PRIMARY KEY (`idService`);

--
-- Indexes for table `tacheentretien`
--
ALTER TABLE `tacheentretien`
    ADD PRIMARY KEY (`idTache`),
  ADD KEY `idEmploye` (`idEmploye`),
  ADD KEY `numeroChambre` (`numeroChambre`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `client`
--
ALTER TABLE `client`
    MODIFY `idClient` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `employer`
--
ALTER TABLE `employer`
    MODIFY `idEmploye` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `facture`
--
ALTER TABLE `facture`
    MODIFY `idFacture` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `lignefacture`
--
ALTER TABLE `lignefacture`
    MODIFY `idLigne` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `reservation`
--
ALTER TABLE `reservation`
    MODIFY `idReservation` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `servicesupplementaire`
--
ALTER TABLE `servicesupplementaire`
    MODIFY `idService` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tacheentretien`
--
ALTER TABLE `tacheentretien`
    MODIFY `idTache` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `facture`
--
ALTER TABLE `facture`
    ADD CONSTRAINT `facture_ibfk_1` FOREIGN KEY (`idReservation`) REFERENCES `reservation` (`idReservation`);

--
-- Constraints for table `lignefacture`
--
ALTER TABLE `lignefacture`
    ADD CONSTRAINT `lignefacture_ibfk_1` FOREIGN KEY (`idFacture`) REFERENCES `facture` (`idFacture`),
  ADD CONSTRAINT `lignefacture_ibfk_2` FOREIGN KEY (`idService`) REFERENCES `servicesupplementaire` (`idService`);

--
-- Constraints for table `reservation`
--
ALTER TABLE `reservation`
    ADD CONSTRAINT `reservation_ibfk_1` FOREIGN KEY (`idClient`) REFERENCES `client` (`idClient`),
  ADD CONSTRAINT `reservation_ibfk_2` FOREIGN KEY (`numeroChambre`) REFERENCES `chambre` (`numeroChambre`);

--
-- Constraints for table `tacheentretien`
--
ALTER TABLE `tacheentretien`
    ADD CONSTRAINT `tacheentretien_ibfk_1` FOREIGN KEY (`idEmploye`) REFERENCES `employer` (`idEmploye`),
  ADD CONSTRAINT `tacheentretien_ibfk_2` FOREIGN KEY (`numeroChambre`) REFERENCES `chambre` (`numeroChambre`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
