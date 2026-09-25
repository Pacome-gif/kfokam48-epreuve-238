"use client";

import { useState } from "react";
import { listerEtudiants, listerPromotions, type Etudiant } from "@/lib/api";
import { useChargement } from "@/lib/useChargement";
import { Etat } from "./Etat";

/** Pas de mot de passe (Q1) : l'étudiant choisit sa promotion puis son nom (EF7). */
export function ChoixEtudiant({ onChoix }: { onChoix: (e: Etudiant | null) => void }) {
  const [promotionId, setPromotionId] = useState<number | null>(null);
  const promotions = useChargement(listerPromotions, "promotions");
  const etudiants = useChargement(() => listerEtudiants(promotionId!), promotionId ? `etudiants-${promotionId}` : null);

  return (
    <fieldset>
      <legend>Qui es-tu ?</legend>
      <Etat chargement={promotions.chargement} erreur={promotions.erreur} />
      <label>
        Promotion
        <select
          value={promotionId ?? ""}
          onChange={(ev) => {
            setPromotionId(ev.target.value ? Number(ev.target.value) : null);
            onChoix(null);
          }}
        >
          <option value="">— choisir —</option>
          {promotions.donnees?.map((p) => (
            <option key={p.id} value={p.id}>{p.nom}</option>
          ))}
        </select>
      </label>
      <Etat chargement={etudiants.chargement} erreur={etudiants.erreur} />
      {etudiants.donnees && (
        <label>
          Nom
          <select
            defaultValue=""
            onChange={(ev) => onChoix(etudiants.donnees?.find((e) => e.id === Number(ev.target.value)) ?? null)}
          >
            <option value="">— choisir —</option>
            {etudiants.donnees.map((e) => (
              <option key={e.id} value={e.id}>{e.nom}</option>
            ))}
          </select>
        </label>
      )}
    </fieldset>
  );
}
