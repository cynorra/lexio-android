"""Themes for generated trial-simulation cases (English only for now).

Each theme is (slug, title, anchor, area). `title` and `anchor` double as the research query (see
sources.fetch_candidates), `area` becomes the case's category label. The generator publishes the next
theme that has no case yet, one per country per run, so the library grows steadily and never repeats a
theme. Add themes freely: the app picks new cases up with no release.

Cases are fictional scenarios built around real legal rules; parties are identified by initials.
"""

CASE_THEMES = {
    "US": [
        ("case-us-traffic-stop-search", "Vehicle Search After a Traffic Stop", "U.S. Const. amend. IV; Terry v. Ohio (1968); automobile exception", "Criminal Procedure"),
        ("case-us-miranda-interrogation", "Statements Taken Without Miranda Warnings", "Miranda v. Arizona (1966); custodial interrogation", "Criminal Procedure"),
        ("case-us-self-defense-assault", "Self-Defense or Aggravated Assault", "self-defense doctrine; proportional force; duty to retreat", "Criminal Law"),
        ("case-us-supply-contract-breach", "Breach of a Goods Supply Contract", "UCC Article 2; expectation damages; perfect tender", "Contract Law"),
        ("case-us-slip-and-fall", "Slip and Fall in a Grocery Store", "negligence: duty, breach, causation, damages; premises liability", "Tort Law"),
        ("case-us-protest-permit", "Protest Permit Denied", "U.S. Const. amend. I; time, place and manner restrictions", "Constitutional Law"),
        ("case-us-workplace-discrimination", "Workplace Discrimination Claim", "Title VII of the Civil Rights Act of 1964; McDonnell Douglas framework", "Employment Law"),
        ("case-us-wire-fraud", "Wire Fraud Charge", "18 U.S.C. 1343; scheme to defraud; intent", "Criminal Law"),
    ],
    "UK": [
        ("case-uk-theft-dishonesty", "Theft and the Test for Dishonesty", "Theft Act 1968 s.1; Ivey v Genting Casinos (2017)", "Criminal Law"),
        ("case-uk-wounding", "Wounding With Intent or Unlawful Wounding", "Offences Against the Person Act 1861 ss.18 and 20", "Criminal Law"),
        ("case-uk-negligence-duty", "Duty of Care to a Passer-By", "Donoghue v Stevenson (1932); Caparo v Dickman (1990)", "Tort Law"),
        ("case-uk-contract-formation", "Was There a Binding Offer?", "offer, acceptance, consideration; Carlill v Carbolic Smoke Ball (1893)", "Contract Law"),
        ("case-uk-police-search", "Stop and Search Without Reasonable Grounds", "Police and Criminal Evidence Act 1984; s.78 exclusion of evidence", "Criminal Procedure"),
        ("case-uk-judicial-review", "Judicial Review of a Council Decision", "Council of Civil Service Unions v Minister for the Civil Service (1985); Wednesbury unreasonableness", "Public Law"),
        ("case-uk-defamation", "Online Defamation Claim", "Defamation Act 2013 s.1 serious harm; honest opinion", "Tort Law"),
        ("case-uk-unfair-dismissal", "Unfair Dismissal Claim", "Employment Rights Act 1996 Part X; reasonableness of dismissal", "Employment Law"),
    ],
    "DE": [
        ("case-de-diebstahl", "Theft Under the StGB", "StGB 242; Wegnahme and Zueignungsabsicht", "Criminal Law"),
        ("case-de-koerperverletzung", "Bodily Harm in a Bar Fight", "StGB 223, 224; dangerous bodily harm", "Criminal Law"),
        ("case-de-betrug", "Fraud and Deception", "StGB 263; Taeuschung, Irrtum, Vermoegensverfuegung", "Criminal Law"),
        ("case-de-kaufvertrag-mangel", "Defective Goods in a Sale", "BGB 433, 434, 437 warranty rights", "Contract Law"),
        ("case-de-delikt-823", "Tort Liability After a Cycling Accident", "BGB 823 para. 1; fault and causation", "Tort Law"),
        ("case-de-notwehr", "Self-Defence Under German Law", "StGB 32 Notwehr; Gebotenheit", "Criminal Law"),
        ("case-de-meinungsfreiheit", "Freedom of Expression and Its Limits", "Grundgesetz art. 5; Lueth judgment of the Bundesverfassungsgericht (1958)", "Constitutional Law"),
        ("case-de-kuendigung", "Dismissal Protection Dispute", "Kuendigungsschutzgesetz (KSchG) 1, 2; socially justified dismissal", "Employment Law"),
    ],
    "FR": [
        ("case-fr-vol", "Theft Under the Code penal", "Code penal art. 311-1; soustraction frauduleuse", "Criminal Law"),
        ("case-fr-legitime-defense", "Legitimate Self-Defence", "Code penal art. 122-5; proportionnalite", "Criminal Law"),
        ("case-fr-responsabilite-civile", "Civil Liability for Damage Caused", "Code civil art. 1240 and 1241; faute, dommage, lien de causalite", "Tort Law"),
        ("case-fr-dol-contrat", "Contract Voided for Fraud", "Code civil art. 1130, 1137; vices du consentement", "Contract Law"),
        ("case-fr-garde-a-vue", "Rights During Police Custody", "Code de procedure penale art. 62-2, 63-1; garde a vue", "Criminal Procedure"),
        ("case-fr-escroquerie", "Swindling Charge", "Code penal art. 313-1; manoeuvres frauduleuses", "Criminal Law"),
        ("case-fr-licenciement", "Dismissal Without Real and Serious Cause", "Code du travail art. L1232-1; cause reelle et serieuse", "Employment Law"),
        ("case-fr-liberte-expression", "Freedom of Expression and Its Limits", "Declaration of 1789 art. 11; loi du 29 juillet 1881", "Constitutional Law"),
    ],
    "ES": [
        ("case-es-hurto", "Theft Under the Codigo Penal", "Codigo Penal art. 234; hurto vs robo", "Criminal Law"),
        ("case-es-lesiones", "Injury Offence After a Fight", "Codigo Penal art. 147; lesiones", "Criminal Law"),
        ("case-es-estafa", "Fraud and Deception", "Codigo Penal art. 248; engano bastante", "Criminal Law"),
        ("case-es-responsabilidad-civil", "Non-Contractual Civil Liability", "Codigo Civil art. 1902; culpa o negligencia", "Tort Law"),
        ("case-es-incumplimiento-contrato", "Breach of Contract and Damages", "Codigo Civil art. 1101, 1124", "Contract Law"),
        ("case-es-legitima-defensa", "Legitimate Self-Defence", "Codigo Penal art. 20.4; eximente de legitima defensa", "Criminal Law"),
        ("case-es-despido", "Disciplinary Dismissal Dispute", "Estatuto de los Trabajadores art. 54; despido disciplinario", "Employment Law"),
        ("case-es-derecho-reunion", "Right of Assembly Restricted", "Constitucion Espanola art. 21; derecho de reunion", "Constitutional Law"),
    ],
    "IT": [
        ("case-it-furto", "Theft Under the Codice penale", "Codice penale art. 624; sottrazione e impossessamento", "Criminal Law"),
        ("case-it-lesioni", "Personal Injury Offence", "Codice penale art. 582; lesioni personali", "Criminal Law"),
        ("case-it-truffa", "Fraud (Truffa)", "Codice penale art. 640; artifizi e raggiri", "Criminal Law"),
        ("case-it-responsabilita-extracontrattuale", "Non-Contractual Liability", "Codice civile art. 2043; danno ingiusto", "Tort Law"),
        ("case-it-inadempimento", "Breach of Contract", "Codice civile art. 1453; risoluzione per inadempimento", "Contract Law"),
        ("case-it-legittima-difesa", "Legitimate Self-Defence", "Codice penale art. 52; proporzione", "Criminal Law"),
        ("case-it-licenziamento", "Dismissal Without Just Cause", "Legge 604/1966; giusta causa e giustificato motivo", "Employment Law"),
        ("case-it-liberta-espressione", "Freedom of Expression and Its Limits", "Costituzione art. 21; liberta di manifestazione del pensiero", "Constitutional Law"),
    ],
    "TR": [
        ("case-tr-hirsizlik", "Theft Under the Turkish Penal Code", "TCK m.141; hirsizlik", "Criminal Law"),
        ("case-tr-yaralama", "Intentional Injury", "TCK m.86; kasten yaralama", "Criminal Law"),
        ("case-tr-dolandiricilik", "Fraud Charge", "TCK m.157; dolandiricilik", "Criminal Law"),
        ("case-tr-mesru-mudafaa", "Legitimate Defence", "TCK m.25; mesru mudafaa", "Criminal Law"),
        ("case-tr-haksiz-fiil", "Tort Liability", "Turk Borclar Kanunu m.49; haksiz fiil", "Tort Law"),
        ("case-tr-sozlesme-ihlali", "Breach of Contract", "Turk Borclar Kanunu m.112; borcun ifa edilmemesi", "Contract Law"),
        ("case-tr-is-feshi", "Termination of Employment", "Is Kanunu m.18; fesih icin gecerli neden", "Employment Law"),
        ("case-tr-ifade-ozgurlugu", "Freedom of Expression and Its Limits", "Anayasa m.26; ifade ozgurlugu", "Constitutional Law"),
    ],
}
