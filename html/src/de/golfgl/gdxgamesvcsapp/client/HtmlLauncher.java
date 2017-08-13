package de.golfgl.gdxgamesvcsapp.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

import de.golfgl.gdxgamesvcs.GameJoltClient;
import de.golfgl.gdxgamesvcs.IGameServiceIdMapper;
import de.golfgl.gdxgamesvcsapp.GdxGameSvcsApp;

public class HtmlLauncher extends GwtApplication {

    @Override
    public GwtApplicationConfiguration getConfig() {
        GwtApplicationConfiguration config = new GwtApplicationConfiguration(800, 600);
        config.preferFlash = false;
        return config;
    }

    @Override
    public ApplicationListener createApplicationListener() {
        GdxGameSvcsApp game = new GdxGameSvcsApp();

        GameJoltClient gsClient = new GameJoltClient();
        gsClient.initialize(GdxGameSvcsApp.GAMEJOLT_APP_ID, GdxGameSvcsApp.GAMEJOLT_PRIVATE_KEY);

        gsClient.setUserName(com.google.gwt.user.client.Window.Location.getParameter(GameJoltClient.GJ_USERNAME_PARAM));
        gsClient.setUserToken(com.google.gwt.user.client.Window.Location.
                getParameter(GameJoltClient.GJ_USERTOKEN_PARAM));

        game.gsClient = gsClient;

        return game;
    }
}