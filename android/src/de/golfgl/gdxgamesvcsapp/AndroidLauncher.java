package de.golfgl.gdxgamesvcsapp;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import de.golfgl.gdxgamesvcs.GameJoltClient;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        GdxGameSvcsApp game = new GdxGameSvcsApp();
        GameJoltClient gsClient = new GameJoltClient();
        gsClient.initialize(GdxGameSvcsApp.GAMEJOLT_APP_ID, GdxGameSvcsApp.GAMEJOLT_PRIVATE_KEY);

        game.gsClient = gsClient;

        initialize(game, config);
    }
}
