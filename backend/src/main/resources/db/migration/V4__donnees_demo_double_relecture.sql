-- V4 — données de démonstration du changement « deux relecteurs » (RG6 v2, RG21).
-- Séance 2 de la promotion A : un exercice avec une note PROVISOIRE (1 relecture rendue sur 2)
-- et un exercice RELU avec la moyenne de ses deux notes.

INSERT INTO session_cours (titre, promotion_id, code, ouverture_at, expiration_at)
SELECT 'Séance 2 — Persistance avec JPA', id, 'DEMO02',
       TIMESTAMP WITH TIME ZONE '2026-09-22 08:00:00+00', TIMESTAMP WITH TIME ZONE '2026-09-22 08:15:00+00'
FROM promotion WHERE nom = 'KFOKAM48 — Promotion A';

INSERT INTO presence (session_id, etudiant_id, source, created_at)
SELECT s.id, e.id, 'ETUDIANT', TIMESTAMP WITH TIME ZONE '2026-09-22 08:04:00+00'
FROM session_cours s, etudiant e
WHERE s.code = 'DEMO02' AND e.nom IN ('Awa Mballa', 'Brice Tchoua', 'Daniel Fotso', 'Estelle Kamga');

INSERT INTO exercice (session_id, etudiant_id, lien, statut, depose_at, relecteurs_requis)
SELECT s.id, e.id, 'https://github.com/kfokam48-demo/' || REPLACE(LOWER(e.nom), ' ', '-') || '-seance2',
       CASE e.nom WHEN 'Daniel Fotso' THEN 'EN_ATTENTE_RELECTURE' ELSE 'RELU' END,
       TIMESTAMP WITH TIME ZONE '2026-09-22 10:00:00+00', 2
FROM session_cours s, etudiant e
WHERE s.code = 'DEMO02' AND e.nom IN ('Daniel Fotso', 'Estelle Kamga');

-- Daniel : Awa a rendu 14, Brice n'a pas encore rendu -> note retenue 14, provisoire
INSERT INTO relecture (exercice_id, relecteur_id, note, commentaire, assignee_at, rendue_at)
SELECT x.id, r.id, 14, 'Bonne utilisation des repositories, attention aux requêtes N+1.',
       TIMESTAMP WITH TIME ZONE '2026-09-22 10:00:00+00', TIMESTAMP WITH TIME ZONE '2026-09-22 11:00:00+00'
FROM exercice x JOIN etudiant a ON a.id = x.etudiant_id JOIN session_cours s ON s.id = x.session_id, etudiant r
WHERE s.code = 'DEMO02' AND a.nom = 'Daniel Fotso' AND r.nom = 'Awa Mballa';

INSERT INTO relecture (exercice_id, relecteur_id, assignee_at)
SELECT x.id, r.id, TIMESTAMP WITH TIME ZONE '2026-09-22 10:00:00+00'
FROM exercice x JOIN etudiant a ON a.id = x.etudiant_id JOIN session_cours s ON s.id = x.session_id, etudiant r
WHERE s.code = 'DEMO02' AND a.nom = 'Daniel Fotso' AND r.nom = 'Brice Tchoua';

-- Estelle : Awa a rendu 13, Daniel a rendu 16 -> note retenue 14,5, définitive
INSERT INTO relecture (exercice_id, relecteur_id, note, commentaire, assignee_at, rendue_at)
SELECT x.id, r.id, 13, 'Entités propres, il manque la validation.',
       TIMESTAMP WITH TIME ZONE '2026-09-22 10:00:00+00', TIMESTAMP WITH TIME ZONE '2026-09-22 11:30:00+00'
FROM exercice x JOIN etudiant a ON a.id = x.etudiant_id JOIN session_cours s ON s.id = x.session_id, etudiant r
WHERE s.code = 'DEMO02' AND a.nom = 'Estelle Kamga' AND r.nom = 'Awa Mballa';

INSERT INTO relecture (exercice_id, relecteur_id, note, commentaire, assignee_at, rendue_at)
SELECT x.id, r.id, 16, 'Très lisible, les tests sont pertinents.',
       TIMESTAMP WITH TIME ZONE '2026-09-22 10:00:00+00', TIMESTAMP WITH TIME ZONE '2026-09-22 12:00:00+00'
FROM exercice x JOIN etudiant a ON a.id = x.etudiant_id JOIN session_cours s ON s.id = x.session_id, etudiant r
WHERE s.code = 'DEMO02' AND a.nom = 'Estelle Kamga' AND r.nom = 'Daniel Fotso';
