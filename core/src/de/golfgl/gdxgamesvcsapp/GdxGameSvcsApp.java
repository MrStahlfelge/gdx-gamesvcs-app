package de.golfgl.gdxgamesvcsapp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import de.golfgl.gdxgamesvcs.GameServiceException;
import de.golfgl.gdxgamesvcs.IGameServiceClient;
import de.golfgl.gdxgamesvcs.IGameServiceListener;
import de.golfgl.gdxgamesvcs.MockGameServiceClient;
import de.golfgl.gdxgamesvcs.achievement.IAchievement;
import de.golfgl.gdxgamesvcs.achievement.IFetchAchievementsResponseListener;
import de.golfgl.gdxgamesvcs.leaderboard.IFetchLeaderBoardEntriesResponseListener;
import de.golfgl.gdxgamesvcs.leaderboard.ILeaderBoardEntry;

public class GdxGameSvcsApp extends ApplicationAdapter implements IGameServiceListener {
    public static final String LEADERBOARD1 = "BOARD1";
    public static final String ACHIEVEMENT1 = "ACH1";
    public static final String EVENT1 = "EVENT1";
    public static final String REPOLINK = "https://github.com/MrStahlfelge/gdx-gamesvcs";

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
            gsClient = new MockGameServiceClient(1) {
                @Override
                protected Array<ILeaderBoardEntry> getLeaderboardEntries() {
                    return null;
                }

                @Override
                protected Array<String> getGameStates() {
                    return null;
                }

                @Override
                protected byte[] getGameState() {
                    return new byte[0];
                }

                @Override
                protected Array<IAchievement> getAchievements() {
                    return null;
                }

                @Override
                protected String getPlayerName() {
                    return null;
                }
            };

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

        Label repoLink = new Label(REPOLINK, skin);
        repoLink.setColor(.3f, .3f, 1f, 1f);
        repoLink.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.net.openURI(REPOLINK);
            }
        });

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
                fetchLeaderboard(false);
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

        TextButton fetchAchievements = new TextButton("Fetch", skin);
        fetchAchievements.setVisible(
                gsClient.isFeatureSupported(IGameServiceClient.GameServiceFeature.FetchAchievements));
        fetchAchievements.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                final MyDialog dialog = new MyDialog("Achievements");
                boolean fetchingNow = gsClient.fetchAchievements(new IFetchAchievementsResponseListener() {
                    @Override
                    public void onFetchAchievementsResponse(final Array<IAchievement> achievements) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                showAchievementsList(dialog, achievements);
                            }
                        });

                    }
                });

                dialog.text(fetchingNow ? "Fetching..." : "Could not fetch");
                dialog.show(stage);
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

        table.add(new Label("Gdx-GameServices Demo App", skin)).colspan(3).padBottom(0);
        table.row();
        table.add(repoLink).padBottom(20).colspan(3);
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
        achievementsButtons.add(fetchAchievements);
        achievementsButtons.add(unlockAchievement);
        table.add(achievementsButtons);

        table.row();
        table.add(new Label("Events:", skin)).right();
        table.add(new Label(EVENT1, skin));
        table.add(submitEvent1Btn);

    }

    private void fetchLeaderboard(boolean playerRelated) {
        final MyDialog dialog = new MyDialog("Leaderboard");

        if (!playerRelated) {
            TextButton nowPlayer = new TextButton("Player related", skin);
            nowPlayer.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    fetchLeaderboard(true);
                }
            });
            dialog.button(nowPlayer);
        }

        boolean fetchingNow = gsClient.fetchLeaderboardEntries(LEADERBOARD1, 8, playerRelated,
                new IFetchLeaderBoardEntriesResponseListener() {
                    @Override
                    public void onLeaderBoardResponse(final Array<ILeaderBoardEntry> leaderBoard) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                showLeaderBoardEntries(dialog, leaderBoard);
                            }
                        });
                    }
                });

        dialog.text(fetchingNow ? "Fetching..." : "Could not fetch");
        dialog.show(stage);
    }

    private void showAchievementsList(MyDialog dialog, Array<IAchievement> achievements) {
        dialog.getContentTable().clear();

        if (achievements == null) {
            dialog.text("Could not fetch achievements");
        } else if (achievements.size > 0) {
            Table resultTable = new Table();
            resultTable.defaults().pad(3, 5, 3, 5);

            for (int i = 0; i < achievements.size; i++) {
                IAchievement ach = achievements.get(i);
                resultTable.row();
                resultTable.add(new Label(ach.getTitle(), skin));
                resultTable.add(new Label(ach.isUnlocked() ? "unlocked" : "locked", skin));
                resultTable.add(new Label(Integer.toString((int) (ach.getCompletionPercentage() * 100)) + "%", skin));
            }

            dialog.getContentTable().add(resultTable);
        } else
            dialog.text("No achievements");

        dialog.reshow();
    }

    private void showLeaderBoardEntries(MyDialog dialog, Array<ILeaderBoardEntry> leaderBoard) {
        dialog.getContentTable().clear();

        if (leaderBoard == null) {
            dialog.text("Could not fetch leaderboard");
        } else if (leaderBoard.size > 0) {
            Table resultTable = new Table();
            resultTable.defaults().pad(3, 5, 3, 5);

            for (int i = 0; i < leaderBoard.size; i++) {
                ILeaderBoardEntry le = leaderBoard.get(i);
                resultTable.row();
                resultTable.add(new Label(le.getScoreRank(), skin));

                String userDisplayName = le.getUserDisplayName();
                if (le.getUserId() == null)
                    userDisplayName = "(" + userDisplayName + ")";
                else if (le.isCurrentPlayer())
                    userDisplayName = "*" + userDisplayName;
                resultTable.add(new Label(userDisplayName, skin));

                resultTable.add(new Label(le.getFormattedValue(), skin));
                resultTable.add(new Label(le.getScoreTag(), skin));
            }

            dialog.getContentTable().add(resultTable);
        } else
            dialog.text("No leaderboard entries");

        dialog.reshow();
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
            newStatusText = "SESSION ACTIVE";
        else if (gsClient.isConnectionPending())
            newStatusText = "CONNECTING SESSION...";
        else
            newStatusText = "NO SESSION";

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
        Dialog dialog = new MyDialog("Error");
        dialog.text(et.toString() + ": " + msg);
        dialog.show(stage);
    }

    public class MyDialog extends Dialog {
        public MyDialog(String title) {
            super(title, skin);
            super.button("OK");
        }

        public void reshow() {
            this.show(stage, Actions.alpha(1)).setPosition(Math.round((stage.getWidth() - this.getWidth()) / 2),
                    Math.round((stage.getHeight() - this.getHeight()) / 2));

        }
    }
}
