package de.golfgl.gdxgamesvcsapp.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

import de.golfgl.gdxgamesvcs.IGameServiceIdMapper;
import de.golfgl.gdxgamesvcs.NgioClient;
import de.golfgl.gdxgamesvcsapp.GdxGameSvcsApp;

public class HtmlLauncher extends GwtApplication {

    public static final String NG_APP_ID = "46188:ARRyvuAv";
    public static final Integer NG_BOARD1_ID = 7684;
    public static final Integer NG_ACH1_ID = 52181;
    //http://www.newgrounds.com/projects/games/1110754/preview

    @Override
    public GwtApplicationConfiguration getConfig() {
        GwtApplicationConfiguration config = new GwtApplicationConfiguration(480, 320);
        config.preferFlash = false;
        return config;
    }

    @Override
    public ApplicationListener createApplicationListener() {
        GdxGameSvcsApp gdxGameSvcsApp = new GdxGameSvcsApp();
        NgioClient gsClient = new NgioClient();

        gsClient.initialize(NG_APP_ID,
                com.google.gwt.user.client.Window.Location.getParameter(NgioClient.NGIO_SESSIONID_PARAM),
                null)
                .setNgScoreboardMapper(new IGameServiceIdMapper<Integer>() {
                    @Override
                    public Integer mapToGsId(String independantId) {
                        Integer retVal = null;

                        if (independantId != null && independantId.equals(GdxGameSvcsApp.LEADERBOARD1))
                            retVal = NG_BOARD1_ID;

                        return retVal;
                    }
                })
                .setNgMedalMapper(new IGameServiceIdMapper<Integer>() {
                    @Override
                    public Integer mapToGsId(String independantId) {
                        Integer retVal = null;

                        if (independantId != null && independantId.equals(GdxGameSvcsApp.ACHIEVEMENT1))
                            retVal = NG_ACH1_ID;

                        return retVal;
                    }
                })
                .setEventHostId(com.google.gwt.user.client.Window.Location.getHostName());

        gdxGameSvcsApp.gsClient = gsClient;

        return gdxGameSvcsApp;
    }

}