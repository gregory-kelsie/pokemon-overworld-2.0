package com.anime.arena.screens;

import com.anime.arena.AnimeArena;
import com.anime.arena.items.Bag;
import com.anime.arena.items.BagItem;
import com.anime.arena.items.Item;
import com.anime.arena.items.ItemFactory;
import com.anime.arena.tools.TextFormater;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BagTestScreen implements Screen {

    private AnimeArena game;
    private Texture black;
    private Texture medicine;
    private Texture holdItemBG;
    private Texture pokeballBG;
    private Texture tmsBG;
    private Texture berryBG;
    private Texture clothesBG;
    private Texture keyItemBG;
    private Texture mailBG;
    private BitmapFont regularFont;
    private BitmapFont descriptionFont;
    private OrthographicCamera gameCam;
    private OrthographicCamera controlsCam;
    private Viewport gamePort;
    private List<Item> items;
    private TextureAtlas itemAtlas;
    private Texture bagSel;
    private Sprite itemIcon;
    private Texture itemBack;
    private ItemFactory itemFactory;
    private Bag bag;
    private int scrollPosition;
    private int bagOffset;
    private List<Texture> backgrounds;
    private int currentBackground;
    private Screen previousScreen;
    public BagTestScreen(AnimeArena game, Screen previousScreen) {
        this.game = game;
        this.black = new Texture("animation/black.png");
        this.medicine = new Texture("bag/bagbg2.png");
        this.holdItemBG = new Texture("bag/bagbg1.png");
        this.pokeballBG = new Texture("bag/bagbg3.png");
        this.tmsBG = new Texture("bag/bagbg4.png");
        this.berryBG = new Texture("bag/bagbg5.png");
        this.mailBG = new Texture("bag/bagbg6.png");
        this.clothesBG = new Texture("bag/bagbg7.png");
        this.keyItemBG = new Texture("bag/bagbg8.png");
        this.itemBack = new Texture("bag/itemBack.png");
        this.previousScreen = previousScreen;
        backgrounds = new ArrayList<Texture>();
        backgrounds.add(holdItemBG);
        backgrounds.add(medicine);
        backgrounds.add(pokeballBG);
        backgrounds.add(tmsBG);
        backgrounds.add(berryBG);
        backgrounds.add(mailBG);
        backgrounds.add(clothesBG);
        backgrounds.add(keyItemBG);
        currentBackground = 0;
        this.bagSel  = new Texture("bag/bagSel.png");
        this.items = new ArrayList<Item>();
        this.bag = new Bag();
        this.scrollPosition = 0;
        this.bagOffset = 0;

        this.itemAtlas = new TextureAtlas("items/Items.atlas");
        //Fonts

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pkmnems.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 60;
        parameter.color = Color.DARK_GRAY;
        parameter.spaceY = 20;
        parameter.spaceX = -3;
        parameter.shadowColor = Color.LIGHT_GRAY;
        parameter.shadowOffsetX = 2;
        parameter.shadowOffsetY = 2;

        FreeTypeFontGenerator.FreeTypeFontParameter parameter2 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter2.size = 54;
        parameter2.color = Color.WHITE;
        parameter2.spaceY = 20;
        parameter2.spaceX = -2;

//        parameter2.borderColor = Color.BLACK;
//        parameter2.borderWidth = 5.0f;
        parameter2.shadowColor = Color.BLACK;
        parameter2.shadowOffsetX = 5;
        parameter2.shadowOffsetY = 5;

        regularFont = generator.generateFont(parameter); // font size 12 pixels
        descriptionFont = generator.generateFont(parameter2);
        initCamera();

        gameCam.position.set((AnimeArena.V_WIDTH / 2) / AnimeArena.PPM, (AnimeArena.V_HEIGHT / 2) / AnimeArena.PPM, 0);
        getItems();
    }

    private List<BagItem> getCurrentBagItems() {
        if (currentBackground == 0) {
            return bag.getItems();
        } else if (currentBackground == 1) {
            return bag.getMedicine();
        } else if (currentBackground == 2) {
            return bag.getPokeballs();
        }
        else {
            return bag.getMail();
        }
    }

    private String getBagName() {
        if (currentBackground == 0) {
            return "Items";
        } else if (currentBackground == 1) {
            return "Medicine";
        } else if (currentBackground == 2) {
            return "Pok\u00e9balls";
        } else if (currentBackground == 3) {
            return "TMs & HMs";
        } else if (currentBackground == 4) {
            return "Berries";
        } else if (currentBackground == 5) {
            return "Mail";
        } else if (currentBackground == 6) {
            return "Clothes";
        } else if (currentBackground == 7) {
            return "Key Items";
        } else {
            return "";
        }
    }

    private void initCamera() {
        gameCam = new OrthographicCamera();
        controlsCam = new OrthographicCamera();
        controlsCam.setToOrtho(false, 1080, 1920);
        gamePort = new FitViewport(AnimeArena.V_WIDTH / AnimeArena.PPM, AnimeArena.V_HEIGHT / AnimeArena.PPM, gameCam);
        gamePort.apply();
    }

    private void getItems() {

        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
        request.setUrl("http://kelsiegr.com/loadItems.php");

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String jsonResponse = httpResponse.getResultAsString();
                Gdx.app.log("MSG", jsonResponse);
                JsonObject jsonObject = new JsonParser().parse(jsonResponse).getAsJsonObject();
                JsonArray itemsArray = jsonObject.get("results").getAsJsonArray();
                Map<Integer, JsonObject> itemDatabase = new HashMap<Integer, JsonObject>();
                for (JsonElement obj : itemsArray) {
                    JsonObject jo = obj.getAsJsonObject();
                    int itemID = jo.get("item_id").getAsInt();
                    itemDatabase.put(itemID, jo);
                }
                itemFactory = new ItemFactory(itemsArray.toString(), itemDatabase);
                items.add(itemFactory.createItem(25));
                bag.addItem(itemFactory, 25);
                bag.addItem(itemFactory, 1, 11);
                bag.addItem(itemFactory, 2);
                bag.addItem(itemFactory, 3);
                bag.addItem(itemFactory, 4);
                bag.addItem(itemFactory, 5);
                bag.addItem(itemFactory, 6);
                bag.addItem(itemFactory, 7);
                bag.addItem(itemFactory, 8);
                bag.addItem(itemFactory, 9, 40);

                bag.addItem(itemFactory, 14, 5);
                bag.addItem(itemFactory, 15);
                bag.addItem(itemFactory, 16);

                bag.addItem(itemFactory, 18, 5);
                bag.addItem(itemFactory, 19, 2);
                bag.addItem(itemFactory, 20, 2);

                if (getCurrentBagItems().size() > 0) {
                    itemIcon = new Sprite(itemAtlas.findRegion(getCurrentBagItems().get(0).getItem().getItemIcon()));
                    itemIcon.setSize(100, 100);
                    itemIcon.setPosition(50, 1030);
                }
                Gdx.app.log("Success", "Populated items from DB");

            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("Failed", t.getMessage());
            }

            @Override
            public void cancelled() {
                Gdx.app.log("Cancelled", "cancelled");
            }
        });
        Gdx.app.log("next", "line");
        //request.setContent();
    }


    @Override
    public void show() {

    }

    public void update(float dt) {
        handleInput(dt);

    }

    private void updateItemSprite() {
        if (scrollPosition + bagOffset < getCurrentBagItems().size()) {
            itemIcon = new Sprite(itemAtlas.findRegion(getCurrentBagItems().get(scrollPosition + bagOffset).getItem().getItemIcon()));

        } else {
            //Display close bag
            itemIcon = new Sprite(itemBack);
        }
        itemIcon.setSize(100, 100);
        itemIcon.setPosition(50, 1030);
    }
    private void handleInput(float dt) {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                if (scrollPosition == 2 && bagOffset > 0) {
                    bagOffset--;
                    updateItemSprite();
                } else if (scrollPosition > 0) {
                    scrollPosition--;
                    updateItemSprite();
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (bagOffset == 0 && scrollPosition < 5 && scrollPosition < getCurrentBagItems().size()) {
                scrollPosition++;
                updateItemSprite();
            } else if (scrollPosition < getCurrentBagItems().size() - 3) {
                if (scrollPosition == 5 && getCurrentBagItems().size() > 7 + bagOffset) {
                    bagOffset++;
                    updateItemSprite();
                } else {
                    scrollPosition++;
                    updateItemSprite();
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            scrollPosition = 0;
            bagOffset = 0;
            if (currentBackground == backgrounds.size() - 1) {
                currentBackground = 0;
            } else {
                currentBackground++;
            }
            updateItemSprite();
            Gdx.app.log("bagname", "" + TextFormater.getWordLengthValue(getBagName()));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            scrollPosition = 0;
            bagOffset = 0;
            if (currentBackground == 0) {
                currentBackground = backgrounds.size() - 1;
            } else {
                currentBackground--;
            }
            updateItemSprite();
        } else if (Gdx.input.isKeyJustPressed((Input.Keys.X))) {
            closeBag();
        } else if (Gdx.input.isKeyJustPressed((Input.Keys.Z))) {
            if (scrollPosition + bagOffset == getCurrentBagItems().size()) {
                closeBag();
            }
        }

    }

    private void closeBag() {
        if (previousScreen != null) {
            game.setScreen(previousScreen);
        }
    }

    @Override
    public void render(float dt) {
        update(dt);
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Game Screen
        Gdx.gl.glViewport( 0,Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2);


        //Controls Screen
        Gdx.gl.glViewport( 0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        game.getBatch().setProjectionMatrix(controlsCam.combined);
        game.getBatch().begin();

        game.getBatch().draw(black,0,960);
        game.getBatch().draw(backgrounds.get(currentBackground), 0, 960, 1080, 960);

        game.getBatch().draw(bagSel, 390, 1750 - scrollPosition * 70, 590, 88);
        for (int i = 0; i <= Math.min(7, getCurrentBagItems().size()); i++) {
            if (getCurrentBagItems().size() == i + bagOffset) {
                regularFont.draw(game.getBatch(), "CLOSE BAG", 450, 1810 - i * 70);
            } else {
                regularFont.draw(game.getBatch(), getCurrentBagItems().get(i + bagOffset).getItem().getName(), 450, 1810 - i * 70);
                regularFont.draw(game.getBatch(), "x", 830, 1810 - i * 70);
                if (getCurrentBagItems().get(i + bagOffset).getAmount() < 10) {
                    regularFont.draw(game.getBatch(), "" + getCurrentBagItems().get(i + bagOffset).getAmount(), 925, 1810 - i * 70);
                } else {
                    regularFont.draw(game.getBatch(), "" + getCurrentBagItems().get(i + bagOffset).getAmount(), 900, 1810 - i * 70);
                }
            }
        }

        regularFont.draw(game.getBatch(), getBagName(), 140 + Math.round((5 - TextFormater.getWordLengthValue(getBagName())) * 12), 1448);
        if (itemIcon != null) {
            itemIcon.draw(game.getBatch());
            if (scrollPosition + bagOffset < getCurrentBagItems().size()) {
                descriptionFont.draw(game.getBatch(), getCurrentBagItems().get(scrollPosition + bagOffset).getItem().getDescription(), 190, 1170);
            } else {
                descriptionFont.draw(game.getBatch(), "Close Bag", 190, 1170);
            }
        }


        game.getBatch().end();
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
