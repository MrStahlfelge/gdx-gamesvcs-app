package de.golfgl.gdxgamesvcsapp.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.golfgl.gdxgamesvcs.GpgsClient;
import de.golfgl.gdxgamesvcs.achievement.IFetchAchievementsResponseListener;
import de.golfgl.gdxgamesvcs.leaderboard.IFetchLeaderBoardEntriesResponseListener;
import de.golfgl.gdxgamesvcsapp.GdxGameSvcsApp;
import de.golfgl.gdxgamesvcsapp.GpgsMappers;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        new LwjglApplication(new GdxGameSvcsApp() {
            @Override
            public void create() {
                gsClient = new GpgsClient() {
                    @Override
                    public boolean submitToLeaderboard(String leaderboardId, long score, String tag) {
                        return super.submitToLeaderboard(GpgsMappers.mapToGpgsLeaderboard(leaderboardId), score, tag);
                    }

                    @Override
                    public boolean incrementAchievement(String achievementId, int incNum, final float f) {
                        return super.incrementAchievement(GpgsMappers.mapToGpgsAchievement(achievementId), incNum, f);
                    }

                    @Override
                    public boolean unlockAchievement(String achievementId) {
                        return super.unlockAchievement(GpgsMappers.mapToGpgsAchievement(achievementId));
                    }

                    @Override
                    public boolean submitEvent(String eventId, int increment) {
                        return super.submitEvent(GpgsMappers.mapToGpgsEvent(eventId), increment);
                    }

                    @Override
                    public boolean fetchLeaderboardEntries(String leaderBoardId, int limit, boolean relatedToPlayer,
                                                           IFetchLeaderBoardEntriesResponseListener callback) {
                        return super.fetchLeaderboardEntries(GpgsMappers.mapToGpgsLeaderboard(leaderBoardId),
                                limit, relatedToPlayer, callback);
                    }
                }.initialize("gdx-gamesvcs-desktop-gpgs",
                        Gdx.files.internal("gpgs-client_secret.json"));
                super.create();

            }
        }, config);
    }
}
