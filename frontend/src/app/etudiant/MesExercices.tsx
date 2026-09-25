"use client";

import { mesExercices, type Etudiant } from "@/lib/api";
import { LIBELLE_STATUT } from "@/lib/libelles";
import { useChargement } from "@/lib/useChargement";
import { Etat } from "@/components/Etat";

/** EF12 : note retenue telle que calculée par l'API (RG21), marquée provisoire ; jamais le nom des relecteurs. */
export function MesExercices({ etudiant, version }: { etudiant: Etudiant; version: number }) {
  const exercices = useChargement(() => mesExercices(etudiant.id), `exercices-${etudiant.id}-${version}`);

  return (
    <section>
      <h2>Mes exercices</h2>
      <Etat chargement={exercices.chargement} erreur={exercices.erreur} />
      {exercices.donnees?.length === 0 && <p className="info">Aucun exercice déposé.</p>}
      {exercices.donnees?.map((e) => (
        <fieldset key={e.id}>
          <legend>{e.sessionTitre}</legend>
          <p>
            <a href={e.lien} target="_blank" rel="noreferrer">{e.lien}</a> — {LIBELLE_STATUT[e.statut]}
          </p>
          {e.note === null ? (
            <p className="info">Pas encore de note ({e.relecturesRendues}/{e.relecturesAttendues} relectures rendues).</p>
          ) : (
            <p className={e.noteProvisoire ? "attente" : "succes"}>
              Note : {e.note}/20
              {e.noteProvisoire
                ? ` — provisoire (${e.relecturesRendues}/${e.relecturesAttendues} relectures rendues)`
                : " — définitive"}
            </p>
          )}
          {e.commentaires.length > 0 && (
            <ul>
              {e.commentaires.map((c, i) => (
                <li key={i}>« {c} »</li>
              ))}
            </ul>
          )}
        </fieldset>
      ))}
    </section>
  );
}
