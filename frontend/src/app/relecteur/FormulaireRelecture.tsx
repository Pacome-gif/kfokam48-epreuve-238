"use client";

import { useState } from "react";
import { rendreRelecture, type RelectureAssignee } from "@/lib/api";
import { useEnvoi } from "@/lib/useEnvoi";
import { Etat } from "@/components/Etat";

/** EF5 : note et commentaire. Les bornes et le caractère entier (RG8) sont contrôlés par l'API. */
export function FormulaireRelecture({
  relecture,
  relecteurId,
  onRendue,
}: {
  relecture: RelectureAssignee;
  relecteurId: number;
  onRendue: () => void;
}) {
  const [note, setNote] = useState("");
  const [commentaire, setCommentaire] = useState("");
  const { envoi, erreur, executer } = useEnvoi();

  return (
    <form
      onSubmit={async (ev) => {
        ev.preventDefault();
        const r = await executer(
          () => rendreRelecture(relecture.id, relecteurId, Number(note), commentaire),
          () => "Relecture envoyée",
        );
        if (r) onRendue();
      }}
    >
      <label>
        Note sur 20
        <input type="number" step="any" value={note} onChange={(e) => setNote(e.target.value)} />
      </label>
      <label>
        Commentaire
        <textarea rows={3} value={commentaire} onChange={(e) => setCommentaire(e.target.value)} />
      </label>
      <p className="info">Attention : une relecture envoyée est définitive.</p>
      <button disabled={envoi || note === "" || !commentaire}>{envoi ? "Envoi…" : "Envoyer la relecture"}</button>
      <Etat erreur={erreur} />
    </form>
  );
}
