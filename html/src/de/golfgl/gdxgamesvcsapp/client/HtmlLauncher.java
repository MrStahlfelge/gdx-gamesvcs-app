package de.golfgl.gdxgamesvcsapp.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

import de.golfgl.gdxgamesvcs.GpgsClient;
import de.golfgl.gdxgamesvcs.IGameServiceClient;
import de.golfgl.gdxgamesvcs.IGameServiceIdMapper;
import de.golfgl.gdxgamesvcsapp.GdxGameSvcsApp;
import de.golfgl.gdxgamesvcsapp.GpgsMappers;

public class HtmlLauncher extends GwtApplication {
    private IGameServiceClient gpgsClient;

    @Override
    public GwtApplicationConfiguration getConfig() {
        GwtApplicationConfiguration config = new GwtApplicationConfiguration(800, 600);
        config.preferFlash = false;
        return config;
    }

    @Override
    public ApplicationListener createApplicationListener() {
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
        }).initialize("YOUR_CLIENT_ID_HERE", true);
        game.gsClient = this.gpgsClient;
        return game;
    }
}