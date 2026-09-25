/** Affichage uniforme des états de chargement et d'erreur. */
export function Etat({ chargement, erreur }: { chargement?: boolean; erreur?: string | null }) {
  if (chargement) return <p className="info">Chargement…</p>;
  if (erreur) return <p className="erreur" role="alert">{erreur}</p>;
  return null;
}
