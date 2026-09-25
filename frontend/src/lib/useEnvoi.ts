"use client";

import { useState } from "react";
import { ApiError } from "./api";

/** Envoi d'un formulaire vers l'API : états envoi / erreur / succès (F3). */
export function useEnvoi() {
  const [envoi, setEnvoi] = useState(false);
  const [erreur, setErreur] = useState<string | null>(null);
  const [succes, setSucces] = useState<string | null>(null);

  async function executer<T>(action: () => Promise<T>, messageSucces: (r: T) => string) {
    setEnvoi(true);
    setErreur(null);
    setSucces(null);
    try {
      const resultat = await action();
      setSucces(messageSucces(resultat));
      return resultat;
    } catch (e) {
      setErreur(e instanceof ApiError ? e.message : "Erreur inattendue.");
      return null;
    } finally {
      setEnvoi(false);
    }
  }

  return { envoi, erreur, succes, executer };
}
