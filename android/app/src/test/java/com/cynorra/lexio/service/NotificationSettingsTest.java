package com.cynorra.lexio.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class NotificationSettingsTest {
    @Test
    public void manualHourWinsAndIsClamped() {
        assertEquals(20, NotificationSettings.chooseHour(20, Arrays.asList(9, 9, 9)));
        assertEquals(NotificationSettings.MIN_HOUR, NotificationSettings.chooseHour(3, Collections.emptyList()));
        assertEquals(NotificationSettings.MAX_HOUR, NotificationSettings.chooseHour(23, Collections.emptyList()));
    }

    @Test
    public void automaticNeedsThreeSamplesElseDefault() {
        assertEquals(NotificationSettings.DEFAULT_HOUR, NotificationSettings.chooseHour(-1, Arrays.asList(21, 21)));
        assertEquals(21, NotificationSettings.chooseHour(-1, Arrays.asList(20, 21, 22)));
    }

    @Test
    public void automaticStaysInsideTheAllowedWindow() {
        assertEquals(NotificationSettings.MAX_HOUR, NotificationSettings.chooseHour(-1, Arrays.asList(23, 23, 23)));
        assertEquals(NotificationSettings.MIN_HOUR, NotificationSettings.chooseHour(-1, Arrays.asList(1, 2, 3)));
    }

    @Test
    public void atMostOneReminderPerDay() {
        assertFalse(NotificationSettings.withinCap(Arrays.asList(100L), 100L));
        assertTrue(NotificationSettings.withinCap(Arrays.asList(99L), 100L));
    }

    @Test
    public void weeklyCapIsEnforcedAndOldDaysExpire() {
        // five reminders in the last seven days: no sixth
        assertFalse(NotificationSettings.withinCap(Arrays.asList(94L, 95L, 96L, 97L, 98L), 100L));
        // the same five, but the oldest two are more than a week old: allowed again
        assertTrue(NotificationSettings.withinCap(Arrays.asList(90L, 91L, 96L, 97L, 98L), 100L));
        assertTrue(NotificationSettings.withinCap(Collections.emptyList(), 100L));
    }
}
