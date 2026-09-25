-- V2 — données de démonstration (sans identifiants explicites, pour ne pas désynchroniser les séquences)

INSERT INTO promotion (nom) VALUES ('KFOKAM48 — Promotion A');
INSERT INTO promotion (nom) VALUES ('KFOKAM48 — Promotion B');

INSERT INTO etudiant (nom, promotion_id) SELECT 'Awa Mballa',      id FROM promotion WHERE nom = 'KFOKAM48 — Promotion A';
INSERT INTO etudiant (nom, promotion_id) SELECT 'Brice Tchoua',    id FROM promotion WHERE nom = 'KFOKAM48 — Promotion A';
INSERT INTO etudiant (nom, promotion_id) SELECT 'Carine Ngo',      id FROM promotion WHERE nom = 'KFOKAM48 — Promotion A';
INSERT INTO etudiant (nom, promotion_id) SELECT 'Daniel Fotso',    id FROM promotion WHERE nom = 'KFOKAM48 — Promotion A';
INSERT INTO etudiant (nom, promotion_id) SELECT 'Estelle Kamga',   id FROM promotion WHERE nom = 'KFOKAM48 — Promotion A';
INSERT INTO etudiant (nom, promotion_id) SELECT 'Franck Essomba',  id FROM promotion WHERE nom = 'KFOKAM48 — Promotion A';

INSERT INTO etudiant (nom, promotion_id) SELECT 'Grace Ateba',     id FROM promotion WHERE nom = 'KFOKAM48 — Promotion B';
INSERT INTO etudiant (nom, promotion_id) SELECT 'Hervé Nkoulou',   id FROM promotion WHERE nom = 'KFOKAM48 — Promotion B';
INSERT INTO etudiant (nom, promotion_id) SELECT 'Inès Mbarga',     id FROM promotion WHERE nom = 'KFOKAM48 — Promotion B';
INSERT INTO etudiant (nom, promotion_id) SELECT 'Joël Owona',      id FROM promotion WHERE nom = 'KFOKAM48 — Promotion B';

-- Une séance passée de la promotion A, avec présences, exercices et relectures,
-- pour que le tableau du formateur ne soit pas vide au premier lancement.
INSERT INTO session_cours (titre, promotion_id, code, ouverture_at, expiration_at)
SELECT 'Séance 1 — Introduction à Spring Boot', id, 'DEMO01',
       TIMESTAMP WITH TIME ZONE '2026-09-18 08:00:00+00', TIMESTAMP WITH TIME ZONE '2026-09-18 08:15:00+00'
FROM promotion WHERE nom = 'KFOKAM48 — Promotion A';

INSERT INTO presence (session_id, etudiant_id, source, created_at)
SELECT s.id, e.id, 'ETUDIANT', TIMESTAMP WITH TIME ZONE '2026-09-18 08:05:00+00'
FROM session_cours s, etudiant e
WHERE s.code = 'DEMO01' AND e.nom IN ('Awa Mballa', 'Brice Tchoua', 'Carine Ngo', 'Daniel Fotso');

INSERT INTO presence (session_id, etudiant_id, source, created_at)
SELECT s.id, e.id, 'FORMATEUR', TIMESTAMP WITH TIME ZONE '2026-09-18 08:30:00+00'
FROM session_cours s, etudiant e
WHERE s.code = 'DEMO01' AND e.nom = 'Estelle Kamga';

INSERT INTO exercice (session_id, etudiant_id, lien, statut, depose_at)
SELECT s.id, e.id, 'https://github.com/kfokam48-demo/' || REPLACE(LOWER(e.nom), ' ', '-') || '-seance1',
       CASE e.nom WHEN 'Carine Ngo' THEN 'EN_ATTENTE_RELECTURE' ELSE 'RELU' END,
       TIMESTAMP WITH TIME ZONE '2026-09-18 10:00:00+00'
FROM session_cours s, etudiant e
WHERE s.code = 'DEMO01' AND e.nom IN ('Awa Mballa', 'Brice Tchoua', 'Carine Ngo');

-- Awa relue par Brice (15), Brice relu par Carine (12), Carine attend la relecture de Daniel
INSERT INTO relecture (exercice_id, relecteur_id, note, commentaire, assignee_at, rendue_at)
SELECT x.id, r.id, 15, 'Code clair, il manque des tests.',
       TIMESTAMP WITH TIME ZONE '2026-09-18 10:00:00+00', TIMESTAMP WITH TIME ZONE '2026-09-18 12:00:00+00'
FROM exercice x JOIN etudiant a ON a.id = x.etudiant_id, etudiant r
WHERE a.nom = 'Awa Mballa' AND r.nom = 'Brice Tchoua';

INSERT INTO relecture (exercice_id, relecteur_id, note, commentaire, assignee_at, rendue_at)
SELECT x.id, r.id, 12, 'Fonctionne, mais la validation des entrées est absente.',
       TIMESTAMP WITH TIME ZONE '2026-09-18 10:00:00+00', TIMESTAMP WITH TIME ZONE '2026-09-18 13:00:00+00'
FROM exercice x JOIN etudiant a ON a.id = x.etudiant_id, etudiant r
WHERE a.nom = 'Brice Tchoua' AND r.nom = 'Carine Ngo';

INSERT INTO relecture (exercice_id, relecteur_id, assignee_at)
SELECT x.id, r.id, TIMESTAMP WITH TIME ZONE '2026-09-18 10:00:00+00'
FROM exercice x JOIN etudiant a ON a.id = x.etudiant_id, etudiant r
WHERE a.nom = 'Carine Ngo' AND r.nom = 'Daniel Fotso';
