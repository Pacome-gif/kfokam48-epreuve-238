-- V3 — changement de besoin (étape 3) : chaque exercice est relu par deux pairs différents (RG6 v2).
-- Migration additive : V1 et V2 ne sont pas modifiées ; les données existantes sont conservées.

-- Un exercice peut désormais avoir plusieurs relectures, mais jamais deux fois le même relecteur.
ALTER TABLE relecture DROP CONSTRAINT uk_relecture_exercice;
ALTER TABLE relecture ADD CONSTRAINT uk_relecture_exercice_relecteur UNIQUE (exercice_id, relecteur_id);
CREATE INDEX idx_relecture_exercice ON relecture (exercice_id);

-- Nombre de relectures attendues par exercice.
-- « À partir de maintenant » (H11) : les exercices déjà déposés gardent 1 relecteur requis,
-- leurs notes déjà rendues restent définitives ; les nouveaux dépôts en demandent 2.
ALTER TABLE exercice ADD COLUMN relecteurs_requis INT;
UPDATE exercice SET relecteurs_requis = 1;
ALTER TABLE exercice ALTER COLUMN relecteurs_requis SET NOT NULL;
ALTER TABLE exercice ALTER COLUMN relecteurs_requis SET DEFAULT 2;
ALTER TABLE exercice ADD CONSTRAINT ck_exercice_relecteurs_requis CHECK (relecteurs_requis BETWEEN 1 AND 2);
