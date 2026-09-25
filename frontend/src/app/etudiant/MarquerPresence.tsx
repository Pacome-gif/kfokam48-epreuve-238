"use client";

import { useState } from "react";
import { marquerPresence, type Etudiant } from "@/lib/api";
import { useEnvoi } from "@/lib/useEnvoi";
import { Etat } from "@/components/Etat";

/** EF2 : saisie du code de présence. Les refus (expiré, déjà présent…) viennent de l'API. */
export function MarquerPresence({ etudiant }: { etudiant: Etudiant }) {
  const [code, setCode] = useState("");
  const { envoi, erreur, succes, executer } = useEnvoi();

  return (
    <form
      onSubmit={(ev) => {
        ev.preventDefault();
        executer(() => marquerPresence(code, etudiant.id), () => "Présence enregistrée ✔");
      }}
    >
      <fieldset>
        <legend>Marquer ma présence</legend>
        <label>
          Code donné par le formateur
          <input
            value={code}
            onChange={(e) => setCode(e.target.value.toUpperCase())}
            maxLength={6}
            autoComplete="off"
            inputMode="text"
            placeholder="K7P2QX"
          />
        </label>
        <button disabled={envoi || !code}>{envoi ? "Envoi…" : "Je suis présent"}</button>
        <Etat erreur={erreur} />
        {succes && <p className="succes">{succes}</p>}
      </fieldset>
    </form>
  );
}
