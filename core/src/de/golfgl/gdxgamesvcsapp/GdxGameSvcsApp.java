package de.golfgl.gdxgamesvcsapp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import de.golfgl.gdxgamesvcs.GameServiceException;
import de.golfgl.gdxgamesvcs.IGameServiceClient;
import de.golfgl.gdxgamesvcs.IGameServiceListener;
import de.golfgl.gdxgamesvcs.NoGameServiceClient;
import de.golfgl.gdxgamesvcs.leaderboard.IFetchLeaderBoardEntriesResponseListener;
import de.golfgl.gdxgamesvcs.leaderboard.LeaderBoardEntry;

public class GdxGameSvcsApp extends ApplicationAdapter implements IGameServiceListener {
    public static final String LEADERBOARD1 = "BOARD1";
    public static final String ACHIEVEMENT1 = "ACH1";
    public static final String EVENT1 = "EVENT1";

    public IGameServiceClient gsClient;
    Skin skin;
    Stage stage;
    SpriteBatch batch;
    Label gsStatus;
    Label gsUsername;
    private TextButton signInButton;
    private TextureAtlas atlas;
    private TextField scoreFillin;

    @Override
    public void create() {
        stage = new Stage(new ExtendViewport(800, 600));
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
        scoreFillin = new TextField("100", skin);

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
        showLeaderBoards.setVisible(
                gsClient.isFeatureSupported(IGameServiceClient.GameServiceFeature.ShowAllLeaderboardsUI));

        TextButton fetchLeaderboards = new TextButton("Fetch", skin);
        fetchLeaderboards.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gsClient.fetchLeaderboardEntries(LEADERBOARD1, 8, false,
                        new IFetchLeaderBoardEntriesResponseListener() {
                            @Override
                            public void onLeaderBoardResponse(final Array<LeaderBoardEntry> leaderBoard) {
                                if (leaderBoard != null)
                                    Gdx.app.postRunnable(new Runnable() {
                                        @Override
                                        public void run() {
                                            showLeaderBoardEntries(leaderBoard);
                                        }
                                    });
                            }
                        });
            }
        });
        fetchLeaderboards.setVisible(
                gsClient.isFeatureSupported(IGameServiceClient.GameServiceFeature.FetchLeaderBoardEntries));

        TextButton submitToLeaderboard = new TextButton("Submit", skin);
        submitToLeaderboard.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int score;
                try {
                    score = Integer.valueOf(scoreFillin.getText());
                } catch (NumberFormatException nfe) {
                    score = 0;
                }

                if (score > 0)
                    gsClient.submitToLeaderboard(LEADERBOARD1, score, gsClient.getGameServiceId());
            }
        });

        TextButton showAchievements = new TextButton("Show", skin);
        showAchievements.setVisible(
                gsClient.isFeatureSupported(IGameServiceClient.GameServiceFeature.ShowAchievementsUI));
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
                gsClient.unlockAchievement(ACHIEVEMENT1);
            }
        });

        TextButton submitEvent1Btn = new TextButton("submit", skin);
        submitEvent1Btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gsClient.submitEvent(EVENT1, 1);
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

        table.row().padTop(10);
        table.add(new Label("Leaderboard:", skin)).right();
        table.add(new Label(LEADERBOARD1, skin)).left();

        Table leaderBoardButtons = new Table();
        leaderBoardButtons.defaults().uniform().pad(5);
        leaderBoardButtons.add(showLeaderBoards);
        leaderBoardButtons.add(fetchLeaderboards);
        table.add(leaderBoardButtons);

        table.row();
        table.add();
        table.add(scoreFillin);
        table.add(submitToLeaderboard);

        table.row().padTop(10);
        table.add(new Label("Achievements:", skin)).right();
        table.add(new Label(ACHIEVEMENT1, skin)).left();

        Table achievementsButtons = new Table();
        achievementsButtons.defaults().uniform().pad(5);
        achievementsButtons.add(showAchievements);
        achievementsButtons.add(unlockAchievement);
        table.add(achievementsButtons);

        table.row();
        table.add(new Label("Events:", skin)).right();
        table.add(new Label(EVENT1, skin));
        table.add(submitEvent1Btn);

    }

    private void showLeaderBoardEntries(Array<LeaderBoardEntry> leaderBoard) {
        Dialog dialog = new Dialog("Leaderboard", skin);

        if (leaderBoard.size > 0) {
            Table resultTable = new Table();
            resultTable.defaults().pad(3, 5, 3, 5);

            for (int i = 0; i < leaderBoard.size; i++) {
                LeaderBoardEntry le = leaderBoard.get(i);
                resultTable.row();
                resultTable.add(new Label(le.getScoreRank(), skin));
                resultTable.add(new Label(le.getUserDisplayName(), skin));
                resultTable.add(new Label(le.getFormattedValue(), skin));
                resultTable.add(new Label(le.getScoreTag(), skin));
            }

            dialog.getContentTable().add(resultTable);
        } else
            dialog.add(new Label("No leaderboard entries", skin));

        dialog.button("OK");

        dialog.show(stage);
    }

    private void gsSignInOrOut() {
        if (gsClient.isConnected())
            gsClient.logOff();
        else {
            if (!gsClient.connect(false))
                Gdx.app.error("GS_ERROR", "Cannot sign in: No credentials or session id given.");

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
        atlas = new TextureAtlas(Gdx.files.internal("skin/uiskin.atlas"));
        skin.addRegions(atlas);
        skin.load(Gdx.files.internal("skin/uiskin.json"));

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
        atlas.dispose();
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
    public void gsErrorMsg(GsErrorType et, String msg, Throwable t) {
        Gdx.app.error("GS_ERROR", msg);
    }
}
