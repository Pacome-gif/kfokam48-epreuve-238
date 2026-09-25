"use client";

import { useState } from "react";
import type { Etudiant } from "@/lib/api";
import { ChoixEtudiant } from "@/components/ChoixEtudiant";
import { DeposerExercice } from "./DeposerExercice";
import { MarquerPresence } from "./MarquerPresence";

export default function PageEtudiant() {
  const [etudiant, setEtudiant] = useState<Etudiant | null>(null);

  return (
    <>
      <h1>Espace étudiant</h1>
      <ChoixEtudiant onChoix={setEtudiant} />
      {etudiant && (
        <>
          <p>Bonjour <strong>{etudiant.nom}</strong></p>
          <MarquerPresence key={`p-${etudiant.id}`} etudiant={etudiant} />
          <DeposerExercice key={`d-${etudiant.id}`} etudiant={etudiant} />
        </>
      )}
    </>
  );
}
