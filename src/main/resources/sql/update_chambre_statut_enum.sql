-- Script pour mettre à jour l'ENUM du statut de la chambre
-- Ajoute RESERVEE à l'ENUM existant

-- Pour MySQL, on doit modifier la colonne pour ajouter la nouvelle valeur
ALTER TABLE chambre 
MODIFY COLUMN statut ENUM('DISPONIBLE', 'OCCUPEE', 'HORS_SERVICE', 'RESERVEE') 
NOT NULL DEFAULT 'DISPONIBLE';


