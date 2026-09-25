// Couche d'accès à l'API : le SEUL fichier du frontend qui appelle fetch (F3).
// Les types reprennent les schémas de api/contrat.yaml. Aucun calcul métier ici.

export class ApiError extends Error {
  constructor(
    public readonly status: number,
    public readonly code: string,
    message: string,
  ) {
    super(message);
  }
}

async function requete<T>(chemin: string, init?: RequestInit): Promise<T> {
  let reponse: Response;
  try {
    reponse = await fetch(chemin, {
      ...init,
      headers: { "Content-Type": "application/json", ...init?.headers },
      cache: "no-store",
    });
  } catch {
    throw new ApiError(0, "RESEAU", "Serveur injoignable. Vérifie ta connexion.");
  }
  const texte = await reponse.text();
  const corps = texte ? JSON.parse(texte) : null;
  if (!reponse.ok) {
    throw new ApiError(reponse.status, corps?.code ?? "INCONNU", corps?.message ?? `Erreur ${reponse.status}`);
  }
  return corps as T;
}

// --- Types du contrat ---

export type Promotion = { id: number; nom: string };
export type Etudiant = { id: number; nom: string; promotionId: number };
export type SessionOuverte = { id: number; code: string; ouvertureAt: string; expirationAt: string };
export type Presence = { id: number; sessionId: number; etudiantId: number; source: "ETUDIANT" | "FORMATEUR" };
export type StatutExercice = "DEPOSE" | "EN_ATTENTE_RELECTURE" | "RELU";
export type ExerciceCree = { id: number; statut: StatutExercice };
export type RelectureAssignee = {
  id: number;
  exerciceId: number;
  sessionTitre: string;
  lien: string;
  rendue: boolean;
  note: number | null;
  commentaire: string | null;
};
export type Session = SessionOuverte & { titre: string; promotionId: number; clotureAt: string | null };

// --- Référentiel (EF7) ---

export const listerPromotions = () => requete<Promotion[]>("/api/promotions");

export const listerEtudiants = (promotionId: number) =>
  requete<Etudiant[]>(`/api/promotions/${promotionId}/etudiants`);

// --- Séances (EF1) ---

export const ouvrirSession = (titre: string, promotionId: number) =>
  requete<SessionOuverte>("/api/sessions", { method: "POST", body: JSON.stringify({ titre, promotionId }) });

export const listerSessions = (promotionId: number) => requete<Session[]>(`/api/sessions?promotionId=${promotionId}`);

// --- Présences (EF2) ---

export const marquerPresence = (code: string, etudiantId: number) =>
  requete<Presence>("/api/presences", { method: "POST", body: JSON.stringify({ code, etudiantId }) });

// --- Exercices (EF3) ---

export const deposerExercice = (sessionId: number, etudiantId: number, lien: string) =>
  requete<ExerciceCree>("/api/exercices", { method: "POST", body: JSON.stringify({ sessionId, etudiantId, lien }) });

// --- Relectures (EF5, EF8) — identité déclarée dans X-Etudiant-Id (H2) ---

export const mesRelectures = (etudiantId: number) =>
  requete<RelectureAssignee[]>(`/api/etudiants/${etudiantId}/relectures`);

export const rendreRelecture = (relectureId: number, etudiantId: number, note: number, commentaire: string) =>
  requete<RelectureAssignee>(`/api/relectures/${relectureId}`, {
    method: "POST",
    headers: { "X-Etudiant-Id": String(etudiantId) },
    body: JSON.stringify({ note, commentaire }),
  });
