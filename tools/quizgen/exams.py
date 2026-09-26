"""Law-exam prep: the professional / bar exams people actually study for, per country.

These are separate from the country curriculum in curriculum.py. An exam has its own daily quiz
(Firestore: examQuizzes/{exam id}/days/{YYYY-MM-DD}); the app lets the user pick the exam they are
preparing for, independent of the country selected for the regular law content. United States exams
come first (product priority), then Europe, then Turkey.

Topic tuples are (slug, title, anchor), same shape as curriculum.py. The anchor names the doctrine / rule /
provision to test; questions are still researched, written and fact-checked by generate.py.

To add an exam: append an entry to EXAMS (and its topics), add it to app/.../model/Exams.java. Nothing else.
"""

EXAM_PREFIX = "exam-"


def exam_key(exam_id):
    """The key an exam uses inside CURRICULUM / COUNTRIES (kept apart from real country codes)."""
    return EXAM_PREFIX + exam_id


def is_exam_key(key):
    return key.startswith(EXAM_PREFIX)


def exam_id_of(key):
    return key[len(EXAM_PREFIX):]


# --------------------------------------------------------------------------- United States

CIVPRO = [
    ("ep-civpro-jurisdiction", "Civil Procedure: Subject-Matter and Personal Jurisdiction", "28 U.S.C. 1331, 1332; International Shoe Co. v. Washington (1945)"),
    ("ep-civpro-pleading", "Civil Procedure: Pleading, Joinder and Discovery", "Federal Rules of Civil Procedure 8, 12, 18-20, 26"),
    ("ep-civpro-preclusion", "Civil Procedure: Claim and Issue Preclusion", "Res judicata and collateral estoppel; Parklane Hosiery Co. v. Shore (1979)"),
]
CONLAW = [
    ("ep-conlaw-justiciability", "Constitutional Law: Justiciability and Standing", "U.S. Const. art. III; Lujan v. Defenders of Wildlife (1992)"),
    ("ep-conlaw-equal-protection", "Constitutional Law: Equal Protection and Levels of Scrutiny", "U.S. Const. amend. XIV, sec. 1"),
    ("ep-conlaw-due-process", "Constitutional Law: Substantive and Procedural Due Process", "U.S. Const. amends. V, XIV; Mathews v. Eldridge (1976)"),
    ("ep-conlaw-first-amendment", "Constitutional Law: Free Speech Doctrines", "U.S. Const. amend. I; Brandenburg v. Ohio (1969)"),
    ("ep-conlaw-federal-powers", "Constitutional Law: Commerce, Taxing and Spending Powers", "U.S. Const. art. I, sec. 8; Wickard v. Filburn (1942)"),
]
CONTRACTS = [
    ("ep-contracts-formation", "Contracts: Formation, Offer and Acceptance", "Restatement (Second) of Contracts 17-70; UCC 2-204, 2-207"),
    ("ep-contracts-defenses", "Contracts: Defenses to Enforcement", "Statute of frauds; Restatement (Second) of Contracts 152-178"),
    ("ep-contracts-remedies", "Contracts: Breach and Remedies", "Hadley v. Baxendale (1854); UCC art. 2 remedies"),
    ("ep-contracts-third-parties", "Contracts: Third-Party Beneficiaries, Assignment and Delegation", "Restatement (Second) of Contracts 302-328"),
]
CRIMINAL = [
    ("ep-crim-homicide", "Criminal Law: Homicide", "Common-law murder and manslaughter; Model Penal Code 210"),
    ("ep-crim-inchoate", "Criminal Law: Attempt, Conspiracy and Solicitation", "Model Penal Code 5.01-5.03"),
    ("ep-crim-defenses", "Criminal Law: Insanity, Self-Defense and Duress", "M'Naghten rule; Model Penal Code 3.04, 4.01"),
    ("ep-crimpro-fourth", "Criminal Procedure: Fourth Amendment Searches and Seizures", "U.S. Const. amend. IV; Terry v. Ohio (1968)"),
    ("ep-crimpro-miranda", "Criminal Procedure: Miranda and the Right to Counsel", "U.S. Const. amends. V, VI; Miranda v. Arizona (1966); Gideon v. Wainwright (1963)"),
]
EVIDENCE = [
    ("ep-evidence-hearsay", "Evidence: Hearsay and Its Exceptions", "Federal Rules of Evidence 801-807"),
    ("ep-evidence-relevance", "Evidence: Relevance and Character Evidence", "Federal Rules of Evidence 401-405, 404(b)"),
    ("ep-evidence-witnesses", "Evidence: Witnesses, Impeachment and Privileges", "Federal Rules of Evidence 501, 601-613"),
]
PROPERTY = [
    ("ep-property-estates", "Real Property: Estates and Future Interests", "Fee simple, life estate, remainders; Rule Against Perpetuities"),
    ("ep-property-landlord", "Real Property: Landlord and Tenant", "Common-law lease doctrines; implied warranty of habitability"),
    ("ep-property-recording", "Real Property: Recording Acts and Conveyancing", "Notice, race and race-notice recording statutes"),
    ("ep-property-easements", "Real Property: Easements, Covenants and Servitudes", "Creation and termination of easements; covenants running with the land"),
]
TORTS = [
    ("ep-torts-negligence", "Torts: Negligence, Duty, Breach and Causation", "Palsgraf v. Long Island Railroad Co. (1928)"),
    ("ep-torts-strict", "Torts: Strict Liability and Products Liability", "Restatement (Second) of Torts 402A"),
    ("ep-torts-intentional", "Torts: Intentional Torts and Defenses", "Battery, assault, false imprisonment, consent, privilege"),
    ("ep-torts-defamation", "Torts: Defamation and Privacy", "New York Times Co. v. Sullivan (1964)"),
]
MBE = CIVPRO + CONLAW[:4] + CONTRACTS + CRIMINAL + EVIDENCE + PROPERTY + TORTS

MEE_EXTRA = [
    ("ep-wills-trusts", "Trusts and Estates: Wills, Intestacy and Trusts", "Uniform Probate Code; Restatement (Third) of Trusts"),
    ("ep-family-law", "Family Law: Marriage, Divorce, Custody and Support", "Uniform Marriage and Divorce Act; best-interests standard"),
    ("ep-business-associations", "Business Associations: Agency, Partnerships and Corporations", "Fiduciary duties; business judgment rule; veil piercing"),
    ("ep-secured-transactions", "Secured Transactions", "UCC art. 9: attachment, perfection, priority"),
    ("ep-conflict-of-laws", "Conflict of Laws", "Restatement (Second) of Conflict of Laws; full faith and credit"),
    ("ep-professional-responsibility", "Professional Responsibility for Lawyers", "ABA Model Rules of Professional Conduct 1.6, 1.7"),
]

MPRE = [
    ("ep-mpre-competence-scope", "Competence, Diligence and Scope of Representation", "ABA Model Rules 1.1, 1.2, 1.3"),
    ("ep-mpre-communication-fees", "Client Communication and Fees", "ABA Model Rules 1.4, 1.5"),
    ("ep-mpre-confidentiality", "Confidentiality of Information", "ABA Model Rule 1.6 and its exceptions"),
    ("ep-mpre-conflicts-current", "Conflicts of Interest: Current Clients", "ABA Model Rule 1.7, 1.8"),
    ("ep-mpre-conflicts-former", "Conflicts of Interest: Former Clients and Imputation", "ABA Model Rules 1.9, 1.10, 1.11"),
    ("ep-mpre-client-property", "Safekeeping Client Property and Trust Accounts", "ABA Model Rule 1.15"),
    ("ep-mpre-candor", "Candor Toward the Tribunal and Fairness to Opposing Party", "ABA Model Rules 3.3, 3.4"),
    ("ep-mpre-truthfulness-represented", "Truthfulness to Others; Communicating With Represented Persons", "ABA Model Rules 4.1, 4.2, 4.3"),
    ("ep-mpre-prosecutors", "Special Duties of Prosecutors", "ABA Model Rule 3.8"),
    ("ep-mpre-supervision", "Responsibilities of Supervising and Subordinate Lawyers", "ABA Model Rules 5.1, 5.2, 5.3"),
    ("ep-mpre-unauthorized-practice", "Unauthorized Practice and Law Firm Structure", "ABA Model Rules 5.4, 5.5"),
    ("ep-mpre-advertising", "Advertising and Solicitation", "ABA Model Rules 7.1-7.3"),
    ("ep-mpre-discipline", "Lawyer Discipline and Reporting Misconduct", "ABA Model Rules 8.3, 8.4"),
    ("ep-mpre-judicial-conduct", "Judicial Conduct", "ABA Model Code of Judicial Conduct: impartiality, recusal"),
]

CALIFORNIA_EXTRA = [
    ("ep-ca-community-property", "California Community Property", "California Family Code 760, 770, 2550"),
    ("ep-ca-remedies", "Remedies: Damages, Restitution and Injunctions", "Equitable remedies; specific performance; unjust enrichment"),
    ("ep-wills-trusts", "Trusts and Estates: Wills, Intestacy and Trusts", "California Probate Code; Restatement (Third) of Trusts"),
    ("ep-business-associations", "Business Associations: Agency, Partnerships and Corporations", "Fiduciary duties; business judgment rule; veil piercing"),
    ("ep-professional-responsibility", "Professional Responsibility for Lawyers", "California Rules of Professional Conduct"),
]

# --------------------------------------------------------------------------- United Kingdom

SQE1 = [
    ("ep-sqe-legal-system", "Legal System of England and Wales", "Sources of law, courts hierarchy, precedent, statutory interpretation"),
    ("ep-sqe-constitutional", "Constitutional and Administrative Law", "Parliamentary sovereignty; judicial review; Human Rights Act 1998"),
    ("ep-sqe-contract", "Contract Law", "Offer, acceptance, consideration, terms, misrepresentation, remedies"),
    ("ep-sqe-tort", "Tort Law", "Negligence; Caparo Industries v Dickman (1990); occupiers' liability"),
    ("ep-sqe-dispute-civil", "Dispute Resolution: Civil Procedure", "Civil Procedure Rules 1998; Part 36 offers; case management"),
    ("ep-sqe-business", "Business Law and Practice", "Company law: Companies Act 2006, directors' duties, partnerships"),
    ("ep-sqe-criminal", "Criminal Law and Practice", "Actus reus, mens rea, defences; PACE 1984; Criminal Procedure Rules"),
    ("ep-sqe-property-practice", "Property Practice", "Conveyancing process; contracts for sale; Land Registration Act 2002"),
    ("ep-sqe-land-law", "Land Law", "Legal and equitable interests; easements; mortgages"),
    ("ep-sqe-wills", "Wills and the Administration of Estates", "Wills Act 1837; intestacy rules; grants of representation"),
    ("ep-sqe-trusts", "Trusts Law", "Three certainties; trustees' duties; Trustee Act 2000"),
    ("ep-sqe-tax", "Taxation Principles", "Income tax, capital gains tax and inheritance tax basics"),
    ("ep-sqe-ethics", "Professional Conduct and Ethics", "SRA Principles and Code of Conduct for Solicitors"),
    ("ep-sqe-accounts", "Solicitors Accounts", "SRA Accounts Rules: client money, office money"),
]

# --------------------------------------------------------------------------- Germany

STAATSEXAMEN = [
    ("ep-de-bgb-at", "BGB Allgemeiner Teil: Rechtsgeschaefte und Willenserklaerungen", "BGB 104-185"),
    ("ep-de-schuldrecht-at", "Schuldrecht AT: Leistungsstoerungen", "BGB 280-286, 323"),
    ("ep-de-kaufrecht", "Schuldrecht BT: Kaufvertrag und Maengelrechte", "BGB 433-453"),
    ("ep-de-delikt", "Deliktsrecht", "BGB 823, 826, 831"),
    ("ep-de-sachenrecht", "Sachenrecht: Eigentumserwerb und gutglaeubiger Erwerb", "BGB 929-936"),
    ("ep-de-bereicherung", "Bereicherungsrecht und Geschaeftsfuehrung ohne Auftrag", "BGB 812 ff., 677 ff."),
    ("ep-de-stgb-at", "Strafrecht AT: Tatbestand, Rechtswidrigkeit, Schuld", "StGB 13-35"),
    ("ep-de-stgb-toetung", "Strafrecht BT: Toetungs- und Koerperverletzungsdelikte", "StGB 211, 212, 223 ff."),
    ("ep-de-stgb-vermoegen", "Strafrecht BT: Eigentums- und Vermoegensdelikte", "StGB 242, 249, 263"),
    ("ep-de-stpo", "Strafprozessrecht: Grundlagen und Beweisverbote", "StPO 136a, 152, 244"),
    ("ep-de-staatsorganisation", "Staatsorganisationsrecht", "Grundgesetz Art. 20, 38-69, 70-82"),
    ("ep-de-grundrechte", "Grundrechte und Verhaeltnismaessigkeit", "Grundgesetz Art. 1-19"),
    ("ep-de-verwaltungsrecht", "Allgemeines Verwaltungsrecht: Verwaltungsakt", "VwVfG 35, 48, 49"),
    ("ep-de-vwgo", "Verwaltungsprozessrecht: Klagearten und Zulaessigkeit", "VwGO 40, 42, 43, 113"),
    ("ep-de-zpo", "Zivilprozessrecht: Grundlagen", "ZPO 253, 261, 322"),
    ("ep-de-gesellschaftsrecht", "Handels- und Gesellschaftsrecht", "HGB; GmbHG; AktG"),
    ("ep-de-europarecht", "Europarecht: Vorrang und Grundfreiheiten", "AEUV Art. 34, 45, 49, 56; Costa/ENEL (1964)"),
]

# --------------------------------------------------------------------------- France

CRFPA = [
    ("ep-fr-obligations", "Droit des obligations: formation et validite du contrat", "Code civil art. 1101-1171"),
    ("ep-fr-responsabilite-civile", "Responsabilite civile delictuelle", "Code civil art. 1240, 1241, 1242"),
    ("ep-fr-execution-contrat", "Execution et inexecution du contrat", "Code civil art. 1217-1231-7"),
    ("ep-fr-biens", "Droit des biens et de la propriete", "Code civil art. 544-577, 2258"),
    ("ep-fr-famille", "Droit de la famille: mariage, filiation, regimes matrimoniaux", "Code civil art. 143, 310, 1387"),
    ("ep-fr-procedure-civile", "Procedure civile", "Code de procedure civile art. 1-100"),
    ("ep-fr-procedure-penale", "Procedure penale", "Code de procedure penale art. preliminaire, 62-2, 63"),
    ("ep-fr-droit-penal", "Droit penal general", "Code penal art. 111-1 et 121-1 a 122-7"),
    ("ep-fr-libertes", "Libertes et droits fondamentaux", "Convention europeenne des droits de l'homme; bloc de constitutionnalite"),
    ("ep-fr-administratif", "Droit administratif: acte administratif et recours pour exces de pouvoir", "Conseil d'Etat; Code de justice administrative"),
    ("ep-fr-responsabilite-admin", "Responsabilite de la puissance publique", "Tribunal des conflits, Blanco (1873)"),
    ("ep-fr-affaires", "Droit des affaires et des societes", "Code de commerce; Code civil art. 1832-1873"),
    ("ep-fr-travail", "Droit du travail", "Code du travail: contrat de travail, licenciement"),
    ("ep-fr-europe", "Droit de l'Union europeenne", "TFUE; primaute; effet direct (Van Gend en Loos, 1963)"),
    ("ep-fr-deontologie", "Deontologie et profession d'avocat", "Loi du 31 decembre 1971; Reglement interieur national"),
]

# --------------------------------------------------------------------------- Spain

ACCESO_ABOGACIA = [
    ("ep-es-deontologia", "Deontologia y estatuto general de la abogacia", "Ley 34/2006; Estatuto General de la Abogacia Espanola"),
    ("ep-es-constitucional", "Derecho constitucional: derechos fundamentales", "Constitucion Espanola arts. 14-38"),
    ("ep-es-tutela-judicial", "Tutela judicial efectiva y garantias procesales", "Constitucion Espanola art. 24"),
    ("ep-es-civil-obligaciones", "Derecho civil: obligaciones y contratos", "Codigo Civil arts. 1088-1314"),
    ("ep-es-civil-derechos-reales", "Derecho civil: derechos reales y propiedad", "Codigo Civil arts. 348-608"),
    ("ep-es-familia-sucesiones", "Derecho de familia y sucesiones", "Codigo Civil arts. 42-107, 657-1087"),
    ("ep-es-lec", "Derecho procesal civil", "Ley de Enjuiciamiento Civil 1/2000"),
    ("ep-es-penal", "Derecho penal: parte general", "Codigo Penal arts. 1-137"),
    ("ep-es-lecrim", "Derecho procesal penal", "Ley de Enjuiciamiento Criminal"),
    ("ep-es-administrativo", "Derecho administrativo: procedimiento y recursos", "Ley 39/2015; Ley 29/1998 (LJCA)"),
    ("ep-es-mercantil", "Derecho mercantil y de sociedades", "Ley de Sociedades de Capital (RDL 1/2010)"),
    ("ep-es-laboral", "Derecho laboral", "Estatuto de los Trabajadores (RDL 2/2015)"),
    ("ep-es-ue", "Derecho de la Union Europea", "TFUE; primacia y efecto directo"),
]

# --------------------------------------------------------------------------- Italy

ESAME_AVVOCATO = [
    ("ep-it-obbligazioni", "Obbligazioni e contratti", "Codice civile art. 1173-1469"),
    ("ep-it-responsabilita", "Responsabilita extracontrattuale", "Codice civile art. 2043-2059"),
    ("ep-it-diritti-reali", "Proprieta e diritti reali", "Codice civile art. 810-1172"),
    ("ep-it-famiglia-successioni", "Famiglia e successioni", "Codice civile art. 74-230, 456-564"),
    ("ep-it-penale-generale", "Diritto penale: parte generale", "Codice penale art. 39-59, 110-114"),
    ("ep-it-reati-patrimonio", "Diritto penale: reati contro il patrimonio e la persona", "Codice penale art. 575, 624, 640"),
    ("ep-it-proc-civile", "Procedura civile", "Codice di procedura civile art. 163-183"),
    ("ep-it-proc-penale", "Procedura penale", "Codice di procedura penale art. 60-70, 273-285"),
    ("ep-it-amministrativo", "Diritto amministrativo: procedimento e giustizia amministrativa", "Legge 241/1990; Codice del processo amministrativo"),
    ("ep-it-costituzionale", "Diritto costituzionale", "Costituzione italiana art. 1-54, 134-137"),
    ("ep-it-deontologia", "Ordinamento forense e deontologia", "Legge 247/2012; Codice deontologico forense"),
    ("ep-it-commerciale", "Diritto commerciale e societario", "Codice civile art. 2247-2510"),
    ("ep-it-lavoro", "Diritto del lavoro", "Statuto dei lavoratori (Legge 300/1970)"),
    ("ep-it-ue", "Diritto dell'Unione europea", "TFUE; primato; effetto diretto"),
]

# --------------------------------------------------------------------------- Turkey

HMGS = [
    ("ep-tr-anayasa-temel", "Anayasa Hukuku: Temel Ilkeler ve Devletin Nitelikleri", "1982 Anayasasi md. 1-9"),
    ("ep-tr-anayasa-haklar", "Anayasa Hukuku: Temel Haklar ve Sinirlanmasi", "1982 Anayasasi md. 12-16"),
    ("ep-tr-anayasa-yargi", "Anayasa Hukuku: Yargi Organlari ve Anayasa Mahkemesi", "1982 Anayasasi md. 138-160"),
    ("ep-tr-idare", "Idare Hukuku: Idari Islem ve Iptal Davasi", "2577 sayili Idari Yargilama Usulu Kanunu"),
    ("ep-tr-medeni-kisiler", "Medeni Hukuk: Kisiler Hukuku ve Ehliyet", "4721 sayili Turk Medeni Kanunu md. 8-60"),
    ("ep-tr-medeni-esya", "Medeni Hukuk: Esya Hukuku, Mulkiyet ve Tapu Sicili", "4721 sayili TMK md. 683-1025"),
    ("ep-tr-miras-aile", "Medeni Hukuk: Aile ve Miras Hukuku", "4721 sayili TMK md. 118-350, 495-682"),
    ("ep-tr-borclar-genel", "Borclar Hukuku: Genel Hukumler ve Sozlesmeler", "6098 sayili Turk Borclar Kanunu md. 1-206"),
    ("ep-tr-borclar-haksiz-fiil", "Borclar Hukuku: Haksiz Fiil ve Sebepsiz Zenginlesme", "6098 sayili TBK md. 49-82"),
    ("ep-tr-borclar-ozel", "Borclar Hukuku: Ozel Borc Iliskileri", "6098 sayili TBK md. 207-511 (satis, kira, eser)"),
    ("ep-tr-ticaret", "Ticaret Hukuku: Ticari Isletme, Sirketler ve Kiymetli Evrak", "6102 sayili Turk Ticaret Kanunu"),
    ("ep-tr-icra-iflas", "Icra ve Iflas Hukuku", "2004 sayili Icra ve Iflas Kanunu"),
    ("ep-tr-hmk", "Medeni Usul Hukuku", "6100 sayili Hukuk Muhakemeleri Kanunu"),
    ("ep-tr-ceza-genel", "Ceza Hukuku: Genel Hukumler", "5237 sayili Turk Ceza Kanunu md. 1-75"),
    ("ep-tr-ceza-ozel", "Ceza Hukuku: Ozel Hukumler", "5237 sayili TCK md. 81-282"),
    ("ep-tr-cmk", "Ceza Muhakemesi Hukuku", "5271 sayili Ceza Muhakemesi Kanunu"),
    ("ep-tr-is", "Is Hukuku", "4857 sayili Is Kanunu; 6098 sayili TBK hizmet sozlesmesi"),
    ("ep-tr-mohuk", "Milletlerarasi Ozel Hukuk", "5718 sayili MOHUK"),
]

HAKIM_SAVCI = [
    ("ep-tr-anayasa-temel", "Anayasa Hukuku: Temel Ilkeler ve Devletin Nitelikleri", "1982 Anayasasi md. 1-9"),
    ("ep-tr-anayasa-yargi", "Anayasa Hukuku: Yargi Organlari ve Anayasa Mahkemesi", "1982 Anayasasi md. 138-160"),
    ("ep-tr-idare", "Idare Hukuku: Idari Islem ve Iptal Davasi", "2577 sayili Idari Yargilama Usulu Kanunu"),
    ("ep-tr-ceza-genel", "Ceza Hukuku: Genel Hukumler", "5237 sayili Turk Ceza Kanunu md. 1-75"),
    ("ep-tr-ceza-ozel", "Ceza Hukuku: Ozel Hukumler", "5237 sayili TCK md. 81-282"),
    ("ep-tr-cmk", "Ceza Muhakemesi Hukuku", "5271 sayili Ceza Muhakemesi Kanunu"),
    ("ep-tr-tedbirler", "Ceza ve Guvenlik Tedbirlerinin Infazi", "5275 sayili Kanun"),
    ("ep-tr-medeni-kisiler", "Medeni Hukuk: Kisiler Hukuku ve Ehliyet", "4721 sayili Turk Medeni Kanunu md. 8-60"),
    ("ep-tr-medeni-esya", "Medeni Hukuk: Esya Hukuku, Mulkiyet ve Tapu Sicili", "4721 sayili TMK md. 683-1025"),
    ("ep-tr-borclar-genel", "Borclar Hukuku: Genel Hukumler ve Sozlesmeler", "6098 sayili Turk Borclar Kanunu md. 1-206"),
    ("ep-tr-hmk", "Medeni Usul Hukuku", "6100 sayili Hukuk Muhakemeleri Kanunu"),
    ("ep-tr-icra-iflas", "Icra ve Iflas Hukuku", "2004 sayili Icra ve Iflas Kanunu"),
    ("ep-tr-ticaret", "Ticaret Hukuku: Ticari Isletme, Sirketler ve Kiymetli Evrak", "6102 sayili Turk Ticaret Kanunu"),
    ("ep-tr-kabahatler", "Kabahatler Hukuku", "5326 sayili Kabahatler Kanunu"),
]

# --------------------------------------------------------------------------- registry

# id -> exam. "country" is the jurisdiction whose Wikipedia editions supply reference notes (and the flag in the
# app); "system" tells the writer what kind of questions to write.
EXAMS = {
    "us-ube": {
        "country": "US", "name": "Uniform Bar Examination (UBE)",
        "system": "the Uniform Bar Examination (MBE, MEE and MPT subjects) used by most U.S. states",
        "sources": "the Restatements, the Federal Rules of Civil Procedure and Evidence, the U.S. Constitution, the UCC, and leading U.S. cases",
        "topics": MBE + MEE_EXTRA,
    },
    "us-mbe": {
        "country": "US", "name": "Multistate Bar Examination (MBE)",
        "system": "the Multistate Bar Examination, the 200-question multiple-choice part of the U.S. bar exam (seven subjects)",
        "sources": "the Restatements, the Federal Rules of Civil Procedure and Evidence, the U.S. Constitution, the UCC, and leading U.S. cases",
        "topics": MBE,
    },
    "us-mpre": {
        "country": "US", "name": "Multistate Professional Responsibility Examination (MPRE)",
        "system": "the MPRE, the U.S. professional-ethics exam for lawyers, based on the ABA Model Rules of Professional Conduct",
        "sources": "the ABA Model Rules of Professional Conduct and the ABA Model Code of Judicial Conduct",
        "topics": MPRE,
    },
    "us-calbar": {
        "country": "US", "name": "California Bar Exam",
        "system": "the California Bar Exam (multiple-choice and essay subjects, including California-specific law)",
        "sources": "California statutes, the Restatements, the Federal Rules of Evidence and Civil Procedure, and leading cases",
        "topics": CONLAW[:4] + CONTRACTS + CRIMINAL + EVIDENCE + PROPERTY + TORTS + CALIFORNIA_EXTRA,
    },
    "uk-sqe1": {
        "country": "UK", "name": "Solicitors Qualifying Examination (SQE1)",
        "system": "SQE1, the functioning legal knowledge assessments (FLK1 and FLK2) for solicitors in England and Wales",
        "sources": "English statutes, the Civil Procedure Rules, the SRA Standards and Regulations, and leading English cases",
        "topics": SQE1,
    },
    "de-staatsexamen": {
        "country": "DE", "name": "Erstes Juristisches Staatsexamen",
        "system": "the German First State Examination in law (civil, criminal and public law case-solving)",
        "sources": "the BGB, StGB, StPO, ZPO, Grundgesetz, VwVfG and VwGO, and Bundesverfassungsgericht / BGH case law",
        "topics": STAATSEXAMEN,
    },
    "fr-crfpa": {
        "country": "FR", "name": "Examen d'entree au CRFPA (avocat)",
        "system": "the French bar school entrance exam (CRFPA) for becoming an avocat",
        "sources": "the Code civil, Code penal, procedure codes, Code de commerce, the Conseil d'Etat / Cour de cassation case law and the ECHR",
        "topics": CRFPA,
    },
    "es-acceso-abogacia": {
        "country": "ES", "name": "Prueba de acceso a la abogacia",
        "system": "the Spanish state exam to qualify as an abogado (Ley 34/2006), multiple-choice format",
        "sources": "the Constitucion Espanola, Codigo Civil, Codigo Penal, LEC, LECrim, LJCA and the Estatuto General de la Abogacia",
        "topics": ACCESO_ABOGACIA,
    },
    "it-esame-avvocato": {
        "country": "IT", "name": "Esame di abilitazione alla professione forense",
        "system": "the Italian bar exam (esame di Stato per avvocato): civil, criminal and procedural law, and professional ethics",
        "sources": "the Costituzione, Codice civile, Codice penale, the procedure codes, Legge 247/2012 and Corte di Cassazione case law",
        "topics": ESAME_AVVOCATO,
    },
    "tr-hmgs": {
        "country": "TR", "name": "HMGS (Hukuk Mesleklerine Giris Sinavi)",
        "system": "the Turkish entrance exam for the legal professions (HMGS), multiple-choice across the core branches of Turkish law",
        "sources": "the 1982 Constitution, TMK, TBK, TCK, CMK, HMK, TTK, IIK, IYUK and Yargitay / Danistay / Anayasa Mahkemesi case law",
        "topics": HMGS,
    },
    "tr-hakim-savci": {
        "country": "TR", "name": "Adli Yargi Hakim ve Savci Adayligi Sinavi",
        "system": "the Turkish written exam for judge and public-prosecutor candidates in the judicial branch",
        "sources": "the 1982 Constitution, TCK, CMK, TMK, TBK, HMK, IIK, IYUK and Yargitay case law",
        "topics": HAKIM_SAVCI,
    },
}

EXAM_ORDER = list(EXAMS)


def register(countries, curriculum):
    """Adds every exam to the country/curriculum tables under its own key, so the generator treats
    it exactly like a jurisdiction (same research, write, check, translate, repair pipeline)."""
    for exam_id, exam in EXAMS.items():
        key = exam_key(exam_id)
        parent = countries[exam["country"]]
        countries[key] = {
            "name": f"{parent['name']} - {exam['name']}",
            "search_name": parent["name"],
            "system": exam["system"] + ". Write questions in the style of this exam: application of rules to short fact patterns",
            "sources": exam["sources"],
            "country": exam["country"],
            "exam": exam_id,
        }
        curriculum[key] = exam["topics"]
