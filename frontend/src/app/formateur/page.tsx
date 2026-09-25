"use client";

import { useState } from "react";
import { listerPromotions, listerSessions } from "@/lib/api";
import { heure } from "@/lib/format";
import { useChargement } from "@/lib/useChargement";
import { Etat } from "@/components/Etat";
import { OuvrirSeance } from "./OuvrirSeance";

export default function PageFormateur() {
  const [promotionId, setPromotionId] = useState<number | null>(null);
  const promotions = useChargement(listerPromotions, "promotions");
  const sessions = useChargement(() => listerSessions(promotionId!), promotionId ? `sessions-${promotionId}` : null);

  return (
    <>
      <h1>Espace formateur</h1>
      <Etat chargement={promotions.chargement} erreur={promotions.erreur} />
      <label>
        Promotion
        <select value={promotionId ?? ""} onChange={(e) => setPromotionId(e.target.value ? Number(e.target.value) : null)}>
          <option value="">— choisir —</option>
          {promotions.donnees?.map((p) => (
            <option key={p.id} value={p.id}>{p.nom}</option>
          ))}
        </select>
      </label>

      {promotionId && (
        <>
          <OuvrirSeance promotionId={promotionId} onOuverte={sessions.recharger} />

          <h2>Séances</h2>
          <Etat chargement={sessions.chargement} erreur={sessions.erreur} />
          {sessions.donnees?.length === 0 && <p className="info">Aucune séance.</p>}
          <ul>
            {sessions.donnees?.map((s) => (
              <li key={s.id}>
                <strong>{s.titre}</strong> — code <code>{s.code}</code>, ouverte le {heure(s.ouvertureAt)}
                {s.clotureAt && <> — clôturée</>}
              </li>
            ))}
          </ul>
        </>
      )}
    </>
  );
}
