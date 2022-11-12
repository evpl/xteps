package com.plugatar.xteps.checked.stepobject;

/**
 * Step objects utils.
 */
final class StepObjectsUtils {

    /**
     * Utility class ctor.
     */
    private StepObjectsUtils() {
    }

    static String humanReadableStepNameOfClass(final Class<?> cls) {
        return cls.getSimpleName().replace('_', ' ');
    }
}
