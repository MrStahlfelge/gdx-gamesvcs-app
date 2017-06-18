package de.golfgl.gdxgamesvcsapp.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

import de.golfgl.gdxgamesvcs.NgioClient;
import de.golfgl.gdxgamesvcsapp.GdxGameSvcsApp;

public class HtmlLauncher extends GwtApplication {

    public static final String NG_APP_ID = "46188:ARRyvuAv";
    public static final String NGIO_SESSIONID_PARAM = "ngio_session_id";
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
                com.google.gwt.user.client.Window.Location.getParameter(NGIO_SESSIONID_PARAM),
                null);

        gdxGameSvcsApp.gsClient = gsClient;

        return gdxGameSvcsApp;
    }

}