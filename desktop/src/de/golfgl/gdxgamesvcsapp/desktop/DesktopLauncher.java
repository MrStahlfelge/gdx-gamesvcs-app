package de.golfgl.gdxgamesvcsapp.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.golfgl.gdxgamesvcs.GameJoltClient;
import de.golfgl.gdxgamesvcs.IGameServiceIdMapper;
import de.golfgl.gdxgamesvcsapp.GdxGameSvcsApp;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        GdxGameSvcsApp game = new GdxGameSvcsApp();

        GameJoltClient gsClient = new GameJoltClient();
        gsClient.initialize(GdxGameSvcsApp.GAMEJOLT_APP_ID, GdxGameSvcsApp.GAMEJOLT_PRIVATE_KEY);

        // According to GJ API Doc:
        // Downloadables that use Game Jolt's "Quick Play" system are passed the username and token in both command
        // line arguments and in an automatically generated file "gjapi-credentials.txt"
        // so a TODO for a contributor :-)
        //gsClient.setUserName(arg[somewhat]).setUserToken(...);

        game.gsClient = gsClient;

        new LwjglApplication(game, config);
    }
}
