/** Mise en forme d'affichage uniquement (aucune règle métier). */
export const heure = (iso: string) =>
  new Date(iso).toLocaleString("fr-FR", { dateStyle: "short", timeStyle: "short" });
