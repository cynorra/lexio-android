package com.cynorra.lexio.model;

/**
 * The law exams people prepare for (bar exams etc.), in product-priority order: United States,
 * Europe, then Turkey. The ids match tools/quizgen/exams.py, which publishes one daily quiz per
 * exam to {@code examQuizzes/{id}/days/{date}}. Exam names are proper names and stay untranslated.
 */
public final class Exams {
    private Exams() {}

    public static final class Exam {
        public final String id;
        public final String country;
        public final String name;

        Exam(String id, String country, String name) {
            this.id = id;
            this.country = country;
            this.name = name;
        }
    }

    public static final Exam[] ALL = {
            new Exam("us-ube", "US", "UBE – Uniform Bar Exam"),
            new Exam("us-mbe", "US", "MBE – Multistate Bar Exam"),
            new Exam("us-mpre", "US", "MPRE – Professional Responsibility"),
            new Exam("us-calbar", "US", "California Bar Exam"),
            new Exam("uk-sqe1", "UK", "SQE1 – Solicitors Qualifying Exam"),
            new Exam("de-staatsexamen", "DE", "Erstes Juristisches Staatsexamen"),
            new Exam("fr-crfpa", "FR", "Examen d'entrée au CRFPA"),
            new Exam("es-acceso-abogacia", "ES", "Prueba de acceso a la abogacía"),
            new Exam("it-esame-avvocato", "IT", "Esame di abilitazione forense"),
            new Exam("tr-hmgs", "TR", "HMGS – Hukuk Mesleklerine Giriş Sınavı"),
            new Exam("tr-hakim-savci", "TR", "Hâkim ve Savcı Adaylığı Sınavı"),
    };

    public static final String DEFAULT = ALL[0].id;

    /** The exam with this id, or null if unknown. */
    public static Exam find(String id) {
        for (Exam e : ALL) {
            if (e.id.equals(id)) return e;
        }
        return null;
    }

    public static boolean isSupported(String id) {
        return find(id) != null;
    }

    /** First exam of a country; the sensible default when the user hasn't picked one yet. */
    public static String defaultFor(String country) {
        for (Exam e : ALL) {
            if (e.country.equals(country)) return e.id;
        }
        return DEFAULT;
    }

    public static String flag(String country) {
        switch (country) {
            case "US": return "🇺🇸";
            case "UK": return "🇬🇧";
            case "DE": return "🇩🇪";
            case "FR": return "🇫🇷";
            case "ES": return "🇪🇸";
            case "IT": return "🇮🇹";
            case "TR": return "🇹🇷";
            default: return "";
        }
    }

    /** "🇺🇸 UBE – Uniform Bar Exam" */
    public static String label(Exam e) {
        return flag(e.country) + " " + e.name;
    }
}
