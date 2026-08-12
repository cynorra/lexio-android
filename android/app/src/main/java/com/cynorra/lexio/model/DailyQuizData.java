package com.cynorra.lexio.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DailyQuizData {
    public static List<DailyQuizQuestion> getQuestionsForJurisdiction(String jurisdiction) {
        List<DailyQuizQuestion> list = new ArrayList<>();
        String code = jurisdiction != null ? jurisdiction.toUpperCase() : "TR";

        if ("US".equals(code)) {
            list.add(new DailyQuizQuestion(
                "q_us_1",
                "Which Constitutional Amendment guarantees the right against self-incrimination?",
                Arrays.asList("First Amendment", "Fourth Amendment", "Fifth Amendment", "Eighth Amendment"),
                2,
                "The Fifth Amendment protects individuals from being compelled to be witnesses against themselves in legal proceedings.",
                "US"
            ));
            list.add(new DailyQuizQuestion(
                "q_us_2",
                "Under the Exclusionary Rule, what happens to evidence obtained via an illegal search?",
                Arrays.asList("It is admissible with court permission", "It is inadmissible in criminal court", "It reduces sentence length", "It requires civil fine"),
                1,
                "The Exclusionary Rule prevents evidence collected in violation of the defendant's Fourth Amendment rights from being used in trial.",
                "US"
            ));
            list.add(new DailyQuizQuestion(
                "q_us_3",
                "What legal standard must prosecution satisfy to obtain a criminal conviction in US court?",
                Arrays.asList("Preponderance of evidence", "Clear and convincing evidence", "Beyond a reasonable doubt", "Substantial certainty"),
                2,
                "In US criminal prosecutions, guilt must be proven 'Beyond a reasonable doubt'.",
                "US"
            ));
        } else if ("UK".equals(code)) {
            list.add(new DailyQuizQuestion(
                "q_uk_1",
                "What is the principle of 'Stare Decisis' in English Common Law?",
                Arrays.asList("Freedom of speech", "Binding judicial precedent", "Right to trial by jury", "Presumption of innocence"),
                1,
                "Stare Decisis binds lower courts to follow precedents set by higher courts in similar legal cases.",
                "UK"
            ));
            list.add(new DailyQuizQuestion(
                "q_uk_2",
                "Which standard of proof applies in UK civil tort cases?",
                Arrays.asList("Balance of probabilities", "Beyond reasonable doubt", "Prima facie proof", "Absolute certainty"),
                0,
                "Civil claims in English law require proof on the 'Balance of probabilities' (more probable than not).",
                "UK"
            ));
            list.add(new DailyQuizQuestion(
                "q_uk_3",
                "In English Criminal Law, what elements constitute a crime?",
                Arrays.asList("Actus Reus and Mens Rea", "Habeas Corpus and Tort", "Obiter Dicta and Ratio Decidendi", "Caveat Emptor"),
                0,
                "A crime generally requires both Actus Reus (guilty act) and Mens Rea (guilty mind).",
                "UK"
            ));
        } else if ("DE".equals(code)) {
            list.add(new DailyQuizQuestion(
                "q_de_1",
                "Was ist das Grundgesetz (GG) in Deutschland?",
                Arrays.asList("Das Zivilgesetzbuch", "Die Verfassung der Bundesrepublik Deutschland", "Das Strafgesetzbuch", "Ein EU-Richtlinie"),
                1,
                "Das Grundgesetz ist die Verfassung und die rechtliche Grundordnung der Bundesrepublik Deutschland.",
                "DE"
            ));
            list.add(new DailyQuizQuestion(
                "q_de_2",
                "Welcher Grundsatz gilt im deutschen Strafrecht bezüglich Schuld?",
                Arrays.asList("Keine Strafe ohne Schuld (Nulla poena sine culpa)", "Vermutung der Schuld", "Automatische Haft", "Zivilrechtliche Haftung"),
                0,
                "Im deutschen Strafrecht gilt das Schuldprinzip: Keine Strafe ohne nachgewiesene Schuld.",
                "DE"
            ));
            list.add(new DailyQuizQuestion(
                "q_de_3",
                "Was bedeutet der Begriff 'BGB'?",
                Arrays.asList("Bundesgerichtshof", "Bürgerliches Gesetzbuch", "Bundesgesetzblatt", "Behördliches Gutachten"),
                1,
                "BGB steht für Bürgerliches Gesetzbuch, die zentrale Kodifikation des deutschen Privatrechts.",
                "DE"
            ));
        } else {
            // TR (Default - Türk Hukuku & Yargıtay)
            list.add(new DailyQuizQuestion(
                "q_tr_1",
                "Ceza Hukukunda 'Kanunsuz Suç ve Ceza Olmaz' ilkesi hangi kavramı ifade eder?",
                Arrays.asList("Masumiyet Karinesi", "Kanunilik İlkesi", "Şüpheden Sanık Yararlanır", "Silahların Eşitliği"),
                1,
                "TCK m.2 uyarınca, kanunun açıkça suç saymadığı bir fiil için kimseye ceza verilemez (Kanunilik İlkesi).",
                "TR"
            ));
            list.add(new DailyQuizQuestion(
                "q_tr_2",
                "Hukuk yargılamasında emsal kararları ile içtihat birliğini sağlayan en yüksek yargı organı hangisidir?",
                Arrays.asList("Danıştay", "Anayasa Mahkemesi", "Yargıtay", "Uyuşmazlık Mahkemesi"),
                2,
                "Yargıtay, adliye mahkemelerince verilen karar ve hükümlerin son inceleme merciidir ve içtihat birliğini sağlar.",
                "TR"
            ));
            list.add(new DailyQuizQuestion(
                "q_tr_3",
                "Ceza Muhakemesinde şüpheli veya sanığın beyanı veya ifadesi alınırken müdafi yardımından yararlanması hangi hakkın gereğidir?",
                Arrays.asList("Adil Yargılanma ve Savunma Hakkı", "Özel Hayatın Gizliliği", "Mülkiyet Hakkı", "Dilekçe Hakkı"),
                0,
                "CMK m.149 uyarınca şüpheli veya sanık, soruşturma ve kovuşturmanın her aşamasında bir müdafi yardımından yararlanabilir.",
                "TR"
            ));
        }
        return list;
    }
}
