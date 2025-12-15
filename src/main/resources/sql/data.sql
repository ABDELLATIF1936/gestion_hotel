-- =============================================
-- Données de test pour le système de gestion d'hôtel
-- =============================================

-- Insertion de clients
INSERT INTO client (nom, prenom, telephone, email, typeClient) VALUES
('Dupont', 'Jean', '0612345678', 'jean.dupont@email.com', 'REGULIER'),
('Martin', 'Marie', '0623456789', 'marie.martin@email.com', 'VIP'),
('Bernard', 'Pierre', '0634567890', 'pierre.bernard@email.com', 'REGULIER'),
('Dubois', 'Sophie', '0645678901', 'sophie.dubois@email.com', 'ENTREPRISE'),
('Moreau', 'Luc', '0656789012', 'luc.moreau@email.com', 'REGULIER');

-- Insertion de chambres
INSERT INTO chambre (numeroChambre, categorie, statut, prixNuit, description) VALUES
(101, 'SIMPLE', 'DISPONIBLE', 80.00, 'Chambre simple avec vue sur la cour'),
(102, 'SIMPLE', 'DISPONIBLE', 80.00, 'Chambre simple avec vue sur la cour'),
(201, 'DOUBLE', 'DISPONIBLE', 120.00, 'Chambre double avec lit king-size'),
(202, 'DOUBLE', 'DISPONIBLE', 120.00, 'Chambre double avec lit king-size'),
(203, 'DOUBLE', 'DISPONIBLE', 120.00, 'Chambre double avec balcon'),
(301, 'SUITE', 'DISPONIBLE', 250.00, 'Suite luxueuse avec salon et vue panoramique'),
(302, 'SUITE', 'DISPONIBLE', 250.00, 'Suite luxueuse avec jacuzzi'),
(103, 'SIMPLE', 'HORS_SERVICE', 80.00, 'Chambre en maintenance');

-- Insertion d'employés
INSERT INTO employer (nom, prenom, role, telephone, email, actif) VALUES
('Lefebvre', 'Anne', 'RECEPTIONNISTE', '0712345678', 'anne.lefebvre@hotel.com', TRUE),
('Garcia', 'Marc', 'ENTRETIEN', '0723456789', 'marc.garcia@hotel.com', TRUE),
('Roux', 'Julie', 'ENTRETIEN', '0734567890', 'julie.roux@hotel.com', TRUE),
('Simon', 'Thomas', 'MANAGER', '0745678901', 'thomas.simon@hotel.com', TRUE),
('Michel', 'Laura', 'RECEPTIONNISTE', '0756789012', 'laura.michel@hotel.com', TRUE);

-- Insertion de services supplémentaires
INSERT INTO servicesupplementaire (nom, prix, description, actif) VALUES
('Petit-déjeuner', 15.00, 'Petit-déjeuner buffet complet', TRUE),
('Parking', 10.00, 'Place de parking sécurisée', TRUE),
('Spa', 50.00, 'Accès au spa et sauna', TRUE),
('Service en chambre', 25.00, 'Service de restauration en chambre', TRUE),
('WiFi Premium', 5.00, 'Connexion WiFi haut débit', TRUE),
('Mini-bar', 20.00, 'Accès au mini-bar', TRUE);

-- Insertion de réservations
INSERT INTO reservation (idClient, numeroChambre, dateDebut, dateFin, statut, nbPersonnes, notes) VALUES
(1, 101, DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'CONFIRMEE', 1, 'Arrivée prévue à 14h'),
(2, 201, DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'CONFIRMEE', 2, 'Anniversaire de mariage'),
(3, 202, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'EN_COURS', 2, NULL),
(4, 301, DATE_ADD(CURDATE(), INTERVAL 7 DAY), DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'EN_ATTENTE', 2, 'Client VIP');

-- Insertion de services pour les réservations
INSERT INTO inclure (idReservation, idService, quantite, prixUnitaire) VALUES
(1, 1, 2, 15.00), -- Petit-déjeuner pour 2 jours
(1, 2, 1, 10.00), -- Parking
(2, 1, 3, 15.00), -- Petit-déjeuner pour 3 jours
(2, 3, 1, 50.00), -- Spa
(3, 1, 2, 15.00), -- Petit-déjeuner pour 2 jours
(4, 1, 3, 15.00), -- Petit-déjeuner pour 3 jours
(4, 2, 1, 10.00), -- Parking
(4, 3, 1, 50.00); -- Spa

-- Insertion de factures
INSERT INTO facture (idReservation, dateEmission, montantTotal, statut, notes) VALUES
(1, CURDATE(), 0.00, 'EN_ATTENTE', 'Facture à générer à la fin du séjour'),
(2, CURDATE(), 0.00, 'EN_ATTENTE', 'Facture à générer à la fin du séjour'),
(3, CURDATE(), 0.00, 'EN_ATTENTE', 'Facture en cours de génération');

-- Insertion de tâches d'entretien
INSERT INTO tacheentretien (idEmploye, numeroChambre, type, date, statut, description) VALUES
(2, 103, 'REPARATION', CURDATE(), 'EN_COURS', 'Réparation de la climatisation'),
(3, 101, 'NETTOYAGE', DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'EN_ATTENTE', 'Nettoyage après départ client'),
(2, 203, 'INSPECTION', DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'EN_ATTENTE', 'Inspection de sécurité'),
(3, 102, 'NETTOYAGE', CURDATE(), 'TERMINEE', 'Nettoyage terminé');

