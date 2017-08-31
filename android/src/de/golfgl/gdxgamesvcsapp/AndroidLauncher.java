package de.golfgl.gdxgamesvcsapp;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import de.golfgl.gdxgamesvcs.GpgsClient;
import de.golfgl.gdxgamesvcs.IGameServiceIdMapper;

public class AndroidLauncher extends AndroidApplication {
    private GpgsClient gpgsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        GdxGameSvcsApp game = new GdxGameSvcsApp();
        this.gpgsClient = new GpgsClient() {
            @Override
            public boolean submitEvent(String eventId, int increment) {
                return super.submitEvent(GpgsMappers.mapToGpgsEvent(eventId), increment);
            }
        }.setGpgsAchievementIdMapper(new IGameServiceIdMapper<String>() {
            @Override
            public String mapToGsId(String independantId) {
                return GpgsMappers.mapToGpgsAchievement(independantId);
            }
        }).setGpgsLeaderboardIdMapper(new IGameServiceIdMapper<String>() {
            @Override
            public String mapToGsId(String independantId) {
                return GpgsMappers.mapToGpgsLeaderboard(independantId);
            }
        }).initialize(this, true);
        game.gsClient = this.gpgsClient;
        initialize(game, config);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        gpgsClient.onGpgsActivityResult(requestCode, resultCode, data);
    }
}
