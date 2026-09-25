"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import { ApiError } from "./api";

type Resultat<T> = { pour: string; donnees: T | null; erreur: string | null };

/**
 * Charge une donnée de l'API et expose les états chargement / erreur / données (F3).
 * `cle` identifie la requête : quand elle change, on recharge ; `null` = rien à charger.
 */
export function useChargement<T>(charger: () => Promise<T>, cle: string | null) {
  const chargerRef = useRef(charger);
  const [version, setVersion] = useState(0);
  const [resultat, setResultat] = useState<Resultat<T>>({ pour: "", donnees: null, erreur: null });
  const demande = cle === null ? null : `${cle}#${version}`;

  useEffect(() => {
    chargerRef.current = charger;
  });

  useEffect(() => {
    if (demande === null) return;
    let actif = true;
    chargerRef.current()
      .then((donnees) => actif && setResultat({ pour: demande, donnees, erreur: null }))
      .catch((e) =>
        actif &&
        setResultat({ pour: demande, donnees: null, erreur: e instanceof ApiError ? e.message : "Erreur inattendue." }),
      );
    return () => {
      actif = false;
    };
  }, [demande]);

  const recharger = useCallback(() => setVersion((v) => v + 1), []);
  const aJour = demande !== null && resultat.pour === demande;

  return {
    donnees: demande === null ? null : resultat.donnees,
    erreur: aJour ? resultat.erreur : null,
    chargement: demande !== null && !aJour,
    recharger,
  };
}
