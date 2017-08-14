package de.golfgl.gdxgamesvcsapp.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import de.golfgl.gdxgamesvcsapp.GdxGameSvcsApp;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                GwtApplicationConfiguration config = new GwtApplicationConfiguration(800, 600);
                config.preferFlash = false;
                return config;
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new GdxGameSvcsApp();
        }
}