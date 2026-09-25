import type { Metadata } from "next";
import Link from "next/link";
import "./globals.css";

export const metadata: Metadata = {
  title: "KFOKAM48 — Présence & Relecture",
  description: "Présence par code et relecture par les pairs",
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="fr">
      <body>
        <nav>
          <Link href="/">KFOKAM48</Link>
          <Link href="/formateur">Formateur</Link>
          <Link href="/etudiant">Étudiant</Link>
          <Link href="/relecteur">Relecteur</Link>
        </nav>
        <main>{children}</main>
      </body>
    </html>
  );
}
