-- Script pour ajouter les colonnes check_in et check_out à la table reservation
-- Exécuter ce script si la table reservation existe déjà sans ces colonnes

ALTER TABLE reservation 
ADD COLUMN check_in DATE NULL AFTER notes,
ADD COLUMN check_out DATE NULL AFTER check_in;

