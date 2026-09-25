"use client";

import { useState } from "react";
import { deposerExercice, listerSessions, type Etudiant } from "@/lib/api";
import { LIBELLE_STATUT } from "@/lib/libelles";
import { useChargement } from "@/lib/useChargement";
import { useEnvoi } from "@/lib/useEnvoi";
import { Etat } from "@/components/Etat";

/** EF3 : dépôt du lien d'exercice pour une séance de sa promotion. */
export function DeposerExercice({ etudiant }: { etudiant: Etudiant }) {
  const sessions = useChargement(() => listerSessions(etudiant.promotionId), `sessions-${etudiant.promotionId}`);
  const [sessionId, setSessionId] = useState("");
  const [lien, setLien] = useState("");
  const { envoi, erreur, succes, executer } = useEnvoi();
  const ouvertes = sessions.donnees?.filter((s) => !s.clotureAt) ?? [];

  return (
    <form
      onSubmit={(ev) => {
        ev.preventDefault();
        executer(
          () => deposerExercice(Number(sessionId), etudiant.id, lien),
          (r) => `Exercice déposé ✔ — ${LIBELLE_STATUT[r.statut]}`,
        );
      }}
    >
      <fieldset>
        <legend>Déposer mon exercice</legend>
        <Etat chargement={sessions.chargement} erreur={sessions.erreur} />
        <label>
          Séance
          <select value={sessionId} onChange={(e) => setSessionId(e.target.value)}>
            <option value="">— choisir —</option>
            {ouvertes.map((s) => (
              <option key={s.id} value={s.id}>{s.titre}</option>
            ))}
          </select>
        </label>
        <label>
          Lien de l&apos;exercice
          <input type="url" value={lien} onChange={(e) => setLien(e.target.value)} placeholder="https://github.com/…" />
        </label>
        <button disabled={envoi || !sessionId || !lien}>{envoi ? "Envoi…" : "Déposer"}</button>
        <Etat erreur={erreur} />
        {succes && <p className="succes">{succes}</p>}
      </fieldset>
    </form>
  );
}
