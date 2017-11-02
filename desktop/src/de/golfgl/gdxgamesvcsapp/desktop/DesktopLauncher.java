package de.golfgl.gdxgamesvcsapp.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.golfgl.gdxgamesvcs.GpgsClient;
import de.golfgl.gdxgamesvcs.IGameServiceIdMapper;
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
                }).initialize("gdx-gamesvcs-desktop-gpgs",
                        Gdx.files.internal("gpgs-client_secret.json"), true);
                super.create();

            }
        }, config);
    }
}
