"use client";

import { useState } from "react";
import { ajouterPresenceManuelle, listerEtudiants, listerPresences } from "@/lib/api";
import { useChargement } from "@/lib/useChargement";
import { useEnvoi } from "@/lib/useEnvoi";
import { Etat } from "@/components/Etat";

/** Présences d'une séance et ajout manuel par le formateur (EF9, Q14). */
export function PresencesSeance({ sessionId, promotionId }: { sessionId: number; promotionId: number }) {
  const presences = useChargement(() => listerPresences(sessionId), `presences-${sessionId}`);
  const etudiants = useChargement(() => listerEtudiants(promotionId), `etudiants-${promotionId}`);
  const [etudiantId, setEtudiantId] = useState("");
  const { envoi, erreur, succes, executer } = useEnvoi();

  return (
    <div>
      <Etat chargement={presences.chargement} erreur={presences.erreur} />
      {presences.donnees?.length === 0 && <p className="info">Aucune présence.</p>}
      <ul>
        {presences.donnees?.map((p) => (
          <li key={p.id}>
            {p.nom}
            {p.source === "FORMATEUR" && <strong> — ajoutée par le formateur</strong>}
          </li>
        ))}
      </ul>
      <form
        onSubmit={async (ev) => {
          ev.preventDefault();
          const r = await executer(() => ajouterPresenceManuelle(sessionId, Number(etudiantId)), () => "Présence ajoutée");
          if (r) presences.recharger();
        }}
      >
        <label>
          Ajouter une présence à la main
          <select value={etudiantId} onChange={(e) => setEtudiantId(e.target.value)}>
            <option value="">— étudiant —</option>
            {etudiants.donnees?.map((e) => (
              <option key={e.id} value={e.id}>{e.nom}</option>
            ))}
          </select>
        </label>
        <button disabled={envoi || !etudiantId}>{envoi ? "Ajout…" : "Ajouter"}</button>
        <Etat erreur={erreur} />
        {succes && <p className="succes">{succes}</p>}
      </form>
    </div>
  );
}
