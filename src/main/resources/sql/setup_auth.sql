-- =============================================
-- Script de configuration automatique de l'authentification
-- Ce script crée les tables et insère les données de test
-- =============================================

-- 1. Créer la table utilisateur
CREATE TABLE IF NOT EXISTS `utilisateur` (
    `idUtilisateur` INT(11) NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `idEmploye` INT(11) NOT NULL,
    `role` ENUM('ADMIN', 'RECEPTIONNISTE') NOT NULL,
    `actif` BOOLEAN NOT NULL DEFAULT TRUE,
    `dateCreation` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `dernierLogin` TIMESTAMP NULL,
    PRIMARY KEY (`idUtilisateur`),
    FOREIGN KEY (`idEmploye`) REFERENCES `employer`(`idEmploye`) ON DELETE CASCADE,
    INDEX idx_username (`username`),
    INDEX idx_role (`role`),
    INDEX idx_actif (`actif`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Créer la table audit_log
CREATE TABLE IF NOT EXISTS `audit_log` (
    `idLog` INT(11) NOT NULL AUTO_INCREMENT,
    `idUtilisateur` INT(11) NOT NULL,
    `action` VARCHAR(100) NOT NULL,
    `table_affectee` VARCHAR(50) NOT NULL,
    `id_enregistrement` INT(11) NULL,
    `details` TEXT NULL,
    `date_action` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`idLog`),
    FOREIGN KEY (`idUtilisateur`) REFERENCES `utilisateur`(`idUtilisateur`) ON DELETE CASCADE,
    INDEX idx_utilisateur (`idUtilisateur`),
    INDEX idx_action (`action`),
    INDEX idx_date (`date_action`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. S'assurer qu'il y a un employé ADMINISTRATEUR
INSERT INTO `employer` (`nom`, `prenom`, `role`, `telephone`, `email`, `actif`) 
SELECT 'Admin', 'Système', 'ADMINISTRATEUR', '0100000000', 'admin@hotel.com', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM `employer` WHERE role = 'ADMINISTRATEUR' LIMIT 1
);

-- 4. Insérer les utilisateurs avec les hashs corrects
-- Hash SHA-256 de "Admin123!" = 3eb3fe66b31e3b4d10fa70b5cad49c7112294af6ae4e476a1c405155d45aa121
-- Hash SHA-256 de "Reception123!" = 1b0d33348e535a7cf9b39bd45bd8b7f577a45f7289a554d7d92188e72add5c90
-- Hash SHA-256 de "Entretien123!" = 65f91dd7ba66286628bd64243a2550db5360fbf9ff0604585dda944bd65ea963

-- Admin
INSERT INTO `utilisateur` (`username`, `password`, `idEmploye`, `role`, `actif`) 
SELECT 'admin', '3eb3fe66b31e3b4d10fa70b5cad49c7112294af6ae4e476a1c405155d45aa121', 
       idEmploye, 'ADMIN', TRUE
FROM `employer` 
WHERE role = 'ADMINISTRATEUR' 
LIMIT 1
ON DUPLICATE KEY UPDATE 
    password = '3eb3fe66b31e3b4d10fa70b5cad49c7112294af6ae4e476a1c405155d45aa121',
    actif = TRUE;

-- Réceptionniste
INSERT INTO `utilisateur` (`username`, `password`, `idEmploye`, `role`, `actif`) 
SELECT 'reception', '1b0d33348e535a7cf9b39bd45bd8b7f577a45f7289a554d7d92188e72add5c90', 
       idEmploye, 'RECEPTIONNISTE', TRUE
FROM `employer` 
WHERE role = 'RECEPTIONNISTE' 
LIMIT 1
ON DUPLICATE KEY UPDATE 
    password = '1b0d33348e535a7cf9b39bd45bd8b7f577a45f7289a554d7d92188e72add5c90',
    actif = TRUE;



SELECT 'Configuration de l''authentification terminée avec succès!' AS message;


