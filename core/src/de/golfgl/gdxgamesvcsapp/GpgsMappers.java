package de.golfgl.gdxgamesvcsapp;

/**
 * Created by Benjamin Schulte on 03.07.2017.
 */

public class GpgsMappers {
    public static String mapToGpgsLeaderboard(String leaderboardId) {
        String gpgsId = null;

        if (leaderboardId != null) {
            if (leaderboardId.equals(GdxGameSvcsApp.LEADERBOARD1))
                gpgsId = "CgkIu46Sr-8fEAIQAw";
        }

        return gpgsId;
    }

    public static String mapToGpgsAchievement(String achievementId) {
        String gpgsId = null;

        if (achievementId != null) {
            if (achievementId.equals(GdxGameSvcsApp.ACHIEVEMENT1))
                gpgsId = "CgkIu46Sr-8fEAIQAg";
        }

        return gpgsId;
    }

    public static String mapToGpgsEvent(String eventId) {
        String gpgsId = null;

        if (eventId != null) {
            if (eventId.equals(GdxGameSvcsApp.EVENT1))
                gpgsId = "CgkIu46Sr-8fEAIQAQ";
        }

        return gpgsId;
    }
}
