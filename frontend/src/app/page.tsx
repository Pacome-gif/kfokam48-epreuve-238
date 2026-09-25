import Link from "next/link";

export default function Accueil() {
  return (
    <>
      <h1>Présence &amp; Relecture</h1>
      <ul>
        <li><Link href="/formateur">Formateur</Link> : ouvrir une séance, voir le tableau</li>
        <li><Link href="/etudiant">Étudiant</Link> : marquer sa présence, déposer son exercice</li>
        <li><Link href="/relecteur">Relecteur</Link> : relire l&apos;exercice d&apos;un pair</li>
      </ul>
    </>
  );
}
