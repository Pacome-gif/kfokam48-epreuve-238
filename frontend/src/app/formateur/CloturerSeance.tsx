"use client";

import { cloturerSession } from "@/lib/api";
import { useEnvoi } from "@/lib/useEnvoi";
import { Etat } from "@/components/Etat";

/** EF10 : clôture d'une séance ; ensuite plus de présence par code ni de dépôt (RG3, RG10). */
export function CloturerSeance({ sessionId, onCloturee }: { sessionId: number; onCloturee: () => void }) {
  const { envoi, erreur, executer } = useEnvoi();

  return (
    <>
      <button
        type="button"
        disabled={envoi}
        onClick={async () => {
          if (!window.confirm("Clôturer la séance ? Plus aucune présence ni aucun dépôt ne sera accepté.")) return;
          const r = await executer(() => cloturerSession(sessionId), () => "Séance clôturée");
          if (r) onCloturee();
        }}
      >
        {envoi ? "Clôture…" : "Clôturer"}
      </button>
      <Etat erreur={erreur} />
    </>
  );
}
