package de.golfgl.gdxgamesvcsapp;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import de.golfgl.gdxgamesvcs.GameServiceException;
import de.golfgl.gdxgamesvcs.GpgsClient;

public class AndroidLauncher extends AndroidApplication {
    private GpgsClient gpgsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        GdxGameSvcsApp game = new GdxGameSvcsApp();
        this.gpgsClient = new GpgsClient() {
            @Override
            public void showLeaderboards(String leaderBoardId) throws GameServiceException {
                super.showLeaderboards(GpgsMappers.mapToGpgsLeaderboard(leaderBoardId));
            }

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
        }.initialize(this, false);
        game.gsClient = this.gpgsClient;
        initialize(game, config);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        gpgsClient.onGpgsActivityResult(requestCode, resultCode, data);
    }
}
