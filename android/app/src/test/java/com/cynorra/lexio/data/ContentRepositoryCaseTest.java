package com.cynorra.lexio.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.cynorra.lexio.model.Case;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentRepositoryCaseTest {
    private static Map<String, Object> choice(String text, long score) {
        Map<String, Object> c = new HashMap<>();
        c.put("text", text);
        c.put("aiResponse", "Reply to " + text);
        c.put("scoreEffect", score);
        return c;
    }

    private static Map<String, Object> validCase() {
        Map<String, Object> step = new HashMap<>();
        step.put("stepName", "Opening");
        step.put("prompt", "Open your case");
        step.put("defenseChoices", Arrays.asList(choice("d1", 25), choice("d2", 15), choice("d3", 5)));
        step.put("prosecutionChoices", Arrays.asList(choice("p1", 25), choice("p2", 15), choice("p3", 5)));
        Map<String, Object> d = new HashMap<>();
        d.put("id", 1234567L);
        d.put("title", "A Fictional Dispute");
        d.put("full", "Facts of the case");
        d.put("tagLabel", "Criminal Law");
        d.put("evidence", Arrays.asList("Exhibit A", "Witness"));
        d.put("trialSteps", new ArrayList<Object>(Arrays.asList(step)));
        return d;
    }

    @Test
    public void validCaseIsParsedAsEnglish() {
        Case c = ContentRepository.parseCase(validCase(), "TR");
        assertNotNull(c);
        assertEquals(1234567, c.getId());
        assertTrue(c.isEnglish());
        // A generated case for Turkey must not get the Turkish UI labels.
        assertFalse(c.usesTurkishUi());
        assertEquals(1, c.getTrialSteps().size());
        assertEquals(3, c.getTrialSteps().get(0).getDefenseChoices().size());
        assertEquals(25, c.getTrialSteps().get(0).getDefenseChoices().get(0).getScoreEffect());
    }

    @Test
    public void bundledTurkishCaseKeepsTurkishLabels() {
        Case bundled = new Case(1, "t", "T", "Title", "p", "d", "~5", "ref", "full",
                new ArrayList<>(), new ArrayList<>(), "w", "l", "TR");
        assertTrue(bundled.usesTurkishUi());
    }

    @Test
    public void malformedCasesAreSkippedNotThrown() {
        assertNull(ContentRepository.parseCase(null, "US"));

        Map<String, Object> noId = validCase();
        noId.remove("id");
        assertNull(ContentRepository.parseCase(noId, "US"));

        Map<String, Object> noSteps = validCase();
        noSteps.put("trialSteps", new ArrayList<>());
        assertNull(ContentRepository.parseCase(noSteps, "US"));

        Map<String, Object> badStep = validCase();
        badStep.put("trialSteps", Arrays.asList("not a map"));
        assertNull(ContentRepository.parseCase(badStep, "US"));

        Map<String, Object> noChoices = validCase();
        List<?> steps = (List<?>) noChoices.get("trialSteps");
        ((Map<String, Object>) steps.get(0)).put("defenseChoices", new ArrayList<>());
        assertNull(ContentRepository.parseCase(noChoices, "US"));
    }
}
