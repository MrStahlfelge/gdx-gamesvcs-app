package de.golfgl.gdxgamesvcsapp;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import de.golfgl.gdxgamesvcs.GameCircleClient;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        GdxGameSvcsApp game = new GdxGameSvcsApp();
        GameCircleClient gameCircleClient = new GameCircleClient() {
            @Override
            public boolean submitToLeaderboard(String leaderboardId, long score, String tag) {
                if (leaderboardId != null) {
                    leaderboardId = "LEADERBOARD";
                    return super.submitToLeaderboard(leaderboardId, score, tag);
                }
                return false;
            }
        };
        gameCircleClient.setAchievementsEnabled(true).setLeaderboardsEnabled(true).intialize(this);
        game.gsClient = gameCircleClient;
        initialize(game, config);
    }
}
