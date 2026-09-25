"use client";

import { tableau } from "@/lib/api";
import { useChargement } from "@/lib/useChargement";
import { Etat } from "@/components/Etat";

/** EF6 : valeurs affichées telles que renvoyées par l'API (F3 — aucun recalcul). */
export function Tableau({ promotionId }: { promotionId: number }) {
  const lignes = useChargement(() => tableau(promotionId), `tableau-${promotionId}`);

  return (
    <section>
      <h2>
        Tableau de suivi <button type="button" onClick={lignes.recharger}>Actualiser</button>
      </h2>
      <Etat chargement={lignes.chargement} erreur={lignes.erreur} />
      {lignes.donnees && (
        <table>
          <thead>
            <tr>
              <th>Étudiant</th>
              <th>Présences</th>
              <th>Exercices déposés</th>
              <th>Exercices en attente de relecture</th>
              <th>Moyenne reçue</th>
              <th>Relectures à faire</th>
            </tr>
          </thead>
          <tbody>
            {lignes.donnees.map((l) => (
              <tr key={l.etudiantId}>
                <td>{l.nom}</td>
                <td>{l.presences}</td>
                <td>{l.exercicesDeposes}</td>
                <td className={l.exercicesEnAttente > 0 ? "attente" : undefined}>
                  {l.exercicesEnAttente > 0 ? `${l.exercicesEnAttente} en attente` : "—"}
                </td>
                <td className={l.moyenneProvisoire ? "attente" : undefined}>
                  {l.moyenne ?? "—"}
                  {l.moyenneProvisoire && " (provisoire)"}
                </td>
                <td className={l.relecturesEnAttente > 0 ? "attente" : undefined}>{l.relecturesEnAttente}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  );
}
