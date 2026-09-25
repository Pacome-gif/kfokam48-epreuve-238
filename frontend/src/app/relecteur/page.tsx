"use client";

import { useState } from "react";
import { mesRelectures, type Etudiant } from "@/lib/api";
import { useChargement } from "@/lib/useChargement";
import { ChoixEtudiant } from "@/components/ChoixEtudiant";
import { Etat } from "@/components/Etat";
import { FormulaireRelecture } from "./FormulaireRelecture";

export default function PageRelecteur() {
  const [etudiant, setEtudiant] = useState<Etudiant | null>(null);
  const relectures = useChargement(() => mesRelectures(etudiant!.id), etudiant ? `relectures-${etudiant.id}` : null);

  return (
    <>
      <h1>Espace relecteur</h1>
      <ChoixEtudiant onChoix={setEtudiant} />
      {etudiant && (
        <>
          <h2>Relectures assignées à {etudiant.nom}</h2>
          <Etat chargement={relectures.chargement} erreur={relectures.erreur} />
          {relectures.donnees?.length === 0 && <p className="info">Aucune relecture assignée.</p>}
          {relectures.donnees?.map((r) => (
            <fieldset key={r.id}>
              <legend>{r.sessionTitre}</legend>
              <p>
                Exercice : <a href={r.lien} target="_blank" rel="noreferrer">{r.lien}</a>
              </p>
              {r.rendue ? (
                <p className="succes">Rendue : {r.note}/20 — « {r.commentaire} »</p>
              ) : (
                <FormulaireRelecture relecture={r} relecteurId={etudiant.id} onRendue={relectures.recharger} />
              )}
            </fieldset>
          ))}
        </>
      )}
    </>
  );
}
