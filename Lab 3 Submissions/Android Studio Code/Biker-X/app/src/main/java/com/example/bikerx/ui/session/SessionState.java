package com.example.bikerx.ui.session;

/**Enumeration to determine current state app is in during a cycling session.
 */
public enum SessionState {
    /**
     * Indicates that the user is on the CyclingSessionFragment, but has not started a cycling session.
     */
    PRE_START,
    /**
     * Indicates that the user has started a cycling session. The app will actively track the location of the user, distance travelled, and time elapsed.
     */
    STARTED,
    /**
     * Indicates that the user has paused the cycling session. The app will temporarily stop tracking the location of the user, distance travelled, and time elapsed.
     */
    PAUSED,
}
