import type { StatutExercice } from "./api";

/** Libellés d'affichage des statuts du diagramme D4. */
export const LIBELLE_STATUT: Record<StatutExercice, string> = {
  DEPOSE: "Déposé — relecteur pas encore attribué",
  EN_ATTENTE_RELECTURE: "En attente de relecture",
  RELU: "Relu",
};
