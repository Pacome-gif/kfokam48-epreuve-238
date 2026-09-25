"use client";

import { useState } from "react";
import { ApiError, ouvrirSession, type SessionOuverte } from "@/lib/api";
import { heure } from "@/lib/format";
import { Etat } from "@/components/Etat";

/** EF1 : le formateur ouvre une séance et obtient le code à afficher. */
export function OuvrirSeance({ promotionId, onOuverte }: { promotionId: number; onOuverte: () => void }) {
  const [titre, setTitre] = useState("");
  const [envoi, setEnvoi] = useState(false);
  const [erreur, setErreur] = useState<string | null>(null);
  const [ouverte, setOuverte] = useState<SessionOuverte | null>(null);

  async function soumettre(ev: React.FormEvent) {
    ev.preventDefault();
    setEnvoi(true);
    setErreur(null);
    try {
      setOuverte(await ouvrirSession(titre, promotionId));
      setTitre("");
      onOuverte();
    } catch (e) {
      setErreur(e instanceof ApiError ? e.message : "Erreur inattendue.");
    } finally {
      setEnvoi(false);
    }
  }

  return (
    <form onSubmit={soumettre}>
      <fieldset>
        <legend>Ouvrir une séance</legend>
        <label>
          Titre
          <input value={titre} onChange={(e) => setTitre(e.target.value)} placeholder="Séance 2 — JPA" />
        </label>
        <button disabled={envoi}>{envoi ? "Ouverture…" : "Ouvrir et obtenir le code"}</button>
        <Etat erreur={erreur} />
        {ouverte && (
          <div>
            <p>Code de présence :</p>
            <p className="code">{ouverte.code}</p>
            <p className="info">Valable jusqu&apos;à {heure(ouverte.expirationAt)}</p>
          </div>
        )}
      </fieldset>
    </form>
  );
}
