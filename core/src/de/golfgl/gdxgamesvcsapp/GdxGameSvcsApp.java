package de.golfgl.gdxgamesvcsapp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import de.golfgl.gdxgamesvcs.GameServiceException;
import de.golfgl.gdxgamesvcs.IGameServiceClient;
import de.golfgl.gdxgamesvcs.IGameServiceListener;
import de.golfgl.gdxgamesvcs.NoGameServiceClient;

public class GdxGameSvcsApp extends ApplicationAdapter implements IGameServiceListener {
    public IGameServiceClient gsClient;
    Skin skin;
    Stage stage;
    SpriteBatch batch;
    Label gsStatus;
    Label gsUsername;
    private TextButton signInButton;

    @Override
    public void create() {
        stage = new Stage(new ExtendViewport(480, 320));
        Gdx.input.setInputProcessor(stage);

        prepareSkin();

        if (gsClient == null)
            gsClient = new NoGameServiceClient();

        gsClient.setListener(this);

        prepareUI();

        gsClient.connect(true);

        // needed in case the connection is pending
        refreshStatusLabel();
    }

    private void prepareUI() {
        gsStatus = new Label("", skin);
        gsUsername = new Label("", skin);

        signInButton = new TextButton("", skin);
        signInButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                gsSignInOrOut();
            }
        });

        TextButton showLeaderBoards = new TextButton("Show", skin);
        showLeaderBoards.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    gsClient.showLeaderboards(null);
                } catch (GameServiceException e) {
                    e.printStackTrace();
                }
            }
        });
        showLeaderBoards.setDisabled(!gsClient.providesLeaderboardUI());

        TextButton submitToLeaderboard = new TextButton("Submit", skin);
        submitToLeaderboard.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    gsClient.submitToLeaderboard("BOARD1", 100, gsClient.getGameServiceId());
                } catch (GameServiceException e) {
                    e.printStackTrace();
                }
            }
        });

        TextButton showAchievements = new TextButton("Show", skin);
        showAchievements.setDisabled(!gsClient.providesAchievementsUI());
        showAchievements.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    gsClient.showAchievements();
                } catch (GameServiceException e) {
                    e.printStackTrace();
                }
            }
        });

        TextButton unlockAchievement = new TextButton("unlock", skin);
        unlockAchievement.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gsClient.unlockAchievement("ACH1");
            }
        });

        // Create a table that fills the screen. Everything else will go inside this table.
        Table table = new Table();
        table.setFillParent(true);
        table.defaults().pad(5);
        stage.addActor(table);

        table.add(new Label("Gdx-GameServices Demo App", skin)).padBottom(20).colspan(3);
        table.row();

        table.add(new Label("Game Service ID:", skin)).right();
        table.add(new Label(gsClient.getGameServiceId(), skin)).left();
        table.add();

        table.row();
        table.add(new Label("Status:", skin)).right();
        table.add(gsStatus).left();
        table.add(signInButton);

        table.row();
        table.add(new Label("User name:", skin)).right();
        table.add(gsUsername).left();
        table.add();

        table.row();
        table.add(new Label("Leaderboard:", skin)).right();
        table.add(new Label("BOARD1", skin)).left();

        Table leaderBoardButtons = new Table();
        leaderBoardButtons.defaults().uniform().pad(5);
        leaderBoardButtons.add(showLeaderBoards);
        leaderBoardButtons.add(submitToLeaderboard);
        table.add(leaderBoardButtons);

        table.row();
        table.add(new Label("Achievements:", skin)).right();
        table.add(new Label("ACH1-ACH3", skin)).left();

        Table achievementsButtons = new Table();
        achievementsButtons.defaults().uniform().pad(5);
        achievementsButtons.add(showAchievements);
        achievementsButtons.add(unlockAchievement);
        table.add(achievementsButtons);

    }

    private void gsSignInOrOut() {
        if (gsClient.isConnected())
            gsClient.logOff();
        else {
            gsClient.connect(false);
            refreshStatusLabel();
        }
    }

    private void refreshStatusLabel() {
        String newStatusText;
        String newUserText;

        if (gsClient.isConnected())
            newStatusText = "CONNECTED";
        else if (gsClient.isConnectionPending())
            newStatusText = "CONNECTING...";
        else
            newStatusText = "NO CONNECTION";

        gsStatus.setText(newStatusText);

        signInButton.setText(gsClient.isConnected() ? "Sign out" : "Sign in");

        newUserText = gsClient.getPlayerDisplayName();
        gsUsername.setText(newUserText != null ? newUserText : "(none)");
    }

    private void prepareSkin() {
        // A skin can be loaded via JSON or defined programmatically, either is fine. Using a skin is optional but
        // strongly
        // recommended solely for the convenience of getting a texture, region, etc as a drawable, tinted drawable, etc.
        skin = new Skin();

        // Generate a 1x1 white texture and store it in the skin named "white".
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));

        // Store the default libgdx font under the name "default".
        skin.add("default", new BitmapFont());

        // Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't
        // overwrite the font.
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);

        Label.LabelStyle lblStyle = new Label.LabelStyle();
        lblStyle.font = skin.getFont("default");
        skin.add("default", lblStyle);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    @Override
    public void pause() {
        super.pause();

        gsClient.disconnect();
    }

    @Override
    public void resume() {
        super.resume();

        gsClient.connect(true);
    }

    @Override
    public void gsConnected() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                refreshStatusLabel();
            }
        });
    }

    @Override
    public void gsDisconnected() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                refreshStatusLabel();
            }
        });
    }

    @Override
    public void gsErrorMsg(String msg) {
        Gdx.app.error("GS_ERROR", msg);
    }

    @Override
    public void gsGameStateLoaded(byte[] gameState) {

    }
}
