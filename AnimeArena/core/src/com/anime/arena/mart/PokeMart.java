package com.anime.arena.mart;

import com.anime.arena.dto.PlayerProfile;
import com.anime.arena.interactions.Event;
import com.anime.arena.items.Item;
import com.anime.arena.items.ItemFactory;
import com.anime.arena.screens.PlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.ArrayList;
import java.util.List;

public class PokeMart extends Event {
    private final int SELECT_ITEM = 0;
    private final int SELECT_QUANTITY = 1;
    private final int CONFIRM_PURCHASE = 2;
    private final int INSUFFICIENT_MONEY = 3;
    private final int FINISHED_PURCHASE = 4;

    private List<MartItem> martItems;
    private ItemFactory itemFactory;
    private PlayerProfile playerProfile;
    private Texture pokemartHUD;
    private Texture windowTexture;
    private Texture martWindowTexture;

    private TextureRegion topLeft;
    private TextureRegion topMid;
    private TextureRegion topRight;
    private TextureRegion midLeft;
    private TextureRegion midMid;
    private TextureRegion midRight;
    private TextureRegion botLeft;
    private TextureRegion botMid;
    private TextureRegion botRight;
    private TextureRegion martTopLeft;
    private TextureRegion martTopMid;
    private TextureRegion martTopRight;
    private TextureRegion martMidLeft;
    private TextureRegion martMidMid;
    private TextureRegion martMidRight;
    private TextureRegion martBotLeft;
    private TextureRegion martBotMid;
    private TextureRegion martBotRight;

    private TextureAtlas itemAtlas;
    private Texture martSel;
    private Sprite itemIcon;
    private Texture closeBagTexture;
    private Texture selectArrowTexture;

    private BitmapFont regularFont;
    private BitmapFont descriptionFont;

    private boolean isMovingCamera;
    private boolean isMovingCameraBack;
    private float cameraOffset;
    private float cameraOriginalX;

    private int itemIndex;
    private int scrollPosition;
    private int martOffset;
    private PlayScreen screen;

    private int martState;
    private MartItem selectedItem;
    private int bagQuantity;
    private int currentAmount;
    private boolean onYes;

    public PokeMart(PlayScreen screen) {
        super(screen);
        this.itemAtlas = new TextureAtlas("items/Items.atlas");
        martItems = new ArrayList<MartItem>();
        this.pokemartHUD = new Texture("hud/martScreen.png");
        this.martSel = new Texture("hud/martSel.png"); //658 width, 72 height
        this.isMovingCamera = true;
        this.isMovingCameraBack = false;
        this.screen = screen;
        this.cameraOffset = 0.0f;
        this.cameraOriginalX = screen.getGameCam().position.x;
        this.isCameraControllingEvent = true;
        this.windowTexture = new Texture("hud/window-skins/goldskin.png");
        this.martWindowTexture = new Texture("hud/window-skins/skin1.png");
        this.selectArrowTexture = new Texture("hud/selarrow.png");
        this.closeBagTexture = new Texture("bag/itemBack.png");
        playerProfile = screen.getPlayer().getPlayerProfile();

        this.scrollPosition = 0;
        this.martOffset = 0;
        martItems.add(new MartItem(screen.getItemFactory().createItem(1), 5));
        martItems.add(new MartItem(screen.getItemFactory().createItem(2), 240));
        martItems.add(new MartItem(screen.getItemFactory().createItem(3), 2500));
        martItems.add(new MartItem(screen.getItemFactory().createItem(4), 3500));
        martItems.add(new MartItem(screen.getItemFactory().createItem(5), 5500));
        martItems.add(new MartItem(screen.getItemFactory().createItem(6), 5060));
        martItems.add(new MartItem(screen.getItemFactory().createItem(7), 17500));
        martItems.add(new MartItem(screen.getItemFactory().createItem(8), 500));
        martItems.add(new MartItem(screen.getItemFactory().createItem(9), 25000));

        this.martState = SELECT_ITEM;
        this.onYes = true;

        updateItemSprite();
        initWindowTexture();
        initFont();
    }
    public PokeMart(PlayScreen screen, PlayerProfile playerProfile, ItemFactory itemFactory) {
        super(screen);
        martItems = new ArrayList<MartItem>();
        this.itemFactory = itemFactory;
        this.playerProfile = playerProfile;
        this.itemIndex = 0;
        this.pokemartHUD = new Texture("hud/martScreen.png");
        this.cameraOffset = 0.0f;
        this.cameraOriginalX = screen.getGameCam().position.x;
        this.isCameraControllingEvent = true;
        this.isMovingCamera = true;
        this.isMovingCameraBack = false;
        this.scrollPosition = 0;
        this.windowTexture = new Texture("window-skins/goldskin.png");
        initWindowTexture();
        initFont();
    }
    
    private void initWindowTexture() {
        topLeft = new TextureRegion(windowTexture, 0, 0, 16, 16);
        topMid = new TextureRegion(windowTexture, 16, 0, 16, 16);
        topRight = new TextureRegion(windowTexture, 32, 0, 16, 16);
        midLeft = new TextureRegion(windowTexture, 0, 16, 16, 16);
        midMid = new TextureRegion(windowTexture, 16, 16, 16, 16);
        midRight = new TextureRegion(windowTexture, 32, 16, 16, 16);
        botLeft = new TextureRegion(windowTexture, 0, 32, 16, 16);
        botMid = new TextureRegion(windowTexture, 16, 32, 16, 16);
        botRight = new TextureRegion(windowTexture, 32, 32, 16, 16);

        martTopLeft = new TextureRegion(martWindowTexture, 0, 0, 16, 16);
        martTopMid = new TextureRegion(martWindowTexture, 16, 0, 16, 16);
        martTopRight = new TextureRegion(martWindowTexture, 32, 0, 16, 16);
        martMidLeft = new TextureRegion(martWindowTexture, 0, 16, 16, 16);
        martMidMid = new TextureRegion(martWindowTexture, 16, 16, 16, 16);
        martMidRight = new TextureRegion(martWindowTexture, 32, 16, 16, 16);
        martBotLeft = new TextureRegion(martWindowTexture, 0, 32, 16, 16);
        martBotMid = new TextureRegion(martWindowTexture, 16, 32, 16, 16);
        martBotRight = new TextureRegion(martWindowTexture, 32, 32, 16, 16);
    }


    private void initFont() {
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

        parameter2.shadowColor = Color.BLACK;
        parameter2.shadowOffsetX = 5;
        parameter2.shadowOffsetY = 5;

        regularFont = generator.generateFont(parameter); // font size 12 pixels
        descriptionFont = generator.generateFont(parameter2);
    }

    public void addItem(int itemID, int price) {
        MartItem martItem = new MartItem();
        martItem.setItem(itemFactory.createItem(itemID));
        martItem.setPrice(price);
        martItems.add(martItem);
    }

    private boolean canAffordItem() {
        if (playerProfile.getMoney() < selectedItem.getPrice()) {
            return false;
        }
        return true;
    }

    private boolean canAffordItem(int amount) {
        if (playerProfile.getMoney() >= selectedItem.getPrice() * currentAmount) {
            return true;
        }
        return false;
    }

    private void updateItemSprite() {
        if (scrollPosition + martOffset < martItems.size()) {
            itemIcon = new Sprite(itemAtlas.findRegion(martItems.get(scrollPosition + martOffset).getItem().getItemIcon()));

        } else {
            //Display close bag
            itemIcon = new Sprite(closeBagTexture);
        }
        itemIcon.setSize(100, 100);
        itemIcon.setPosition(28, 1015);
    }

    public void update(float dt) {
        if (isMovingCamera) {
            screen.getGameCam().position.x += 150 * dt;
            Gdx.app.log("PokeMart:Update", "Original X: " + cameraOriginalX + ", X:" + screen.getGameCam().position.x);
            if (screen.getGameCam().position.x >= cameraOriginalX + 70) {
                Gdx.app.log("PokeMart:Update", "Original X: " + cameraOriginalX + ", X:" + screen.getGameCam().position.x);
                screen.getGameCam().position.x = cameraOriginalX + 70;
                isMovingCamera = false;
            }
        } else if (isMovingCameraBack) {
            screen.getGameCam().position.x -= 150 * dt;
            Gdx.app.log("PokeMart:Update", "Original X: " + cameraOriginalX + ", X:" + screen.getGameCam().position.x);
            if (screen.getGameCam().position.x <= cameraOriginalX) {
                Gdx.app.log("PokeMart:Update", "Original X: " + cameraOriginalX + ", X:" + screen.getGameCam().position.x);
                screen.getGameCam().position.x = cameraOriginalX;
                screen.setEvent(null);
            }
        }
    }

    @Override
    public void interact() {
        if (martState == SELECT_ITEM) {
            if (scrollPosition + martOffset < martItems.size()) {
                selectedItem = martItems.get(scrollPosition + martOffset);
                if (canAffordItem()) {
                    bagQuantity = player.getBag().countItems(selectedItem.getItem());
                    currentAmount = 1;
                    martState = SELECT_QUANTITY;
                } else {
                    martState = INSUFFICIENT_MONEY;
                }
            } else {
                if (!isMovingCamera) {
                    isMovingCameraBack = true;
                }
            }
        } else if (martState == INSUFFICIENT_MONEY) {
            martState = SELECT_ITEM;
        } else if (martState == SELECT_QUANTITY) {
            if (canAffordItem(currentAmount)) {
                martState = CONFIRM_PURCHASE;
            } else {
                martState = INSUFFICIENT_MONEY;
            }
        } else if (martState == CONFIRM_PURCHASE) {
            if (!onYes) {
                martState = SELECT_ITEM;
                onYes = true;
            } else {
                player.getBag().addItem(screen.getItemFactory(), selectedItem.getItem().getItemID(), currentAmount);
                int currentMoney = playerProfile.getMoney();
                playerProfile.setMoney(currentMoney - (selectedItem.getPrice() * currentAmount));
                martState = FINISHED_PURCHASE;
            }
        } else if (martState == FINISHED_PURCHASE) {
            martState = SELECT_ITEM;
        }
    }

    @Override
    public boolean isFinishedEvent() {
        return false;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void clickedUp() {
        if (martState == SELECT_ITEM) {
            if (scrollPosition == 2 && martOffset > 0) {
                martOffset--;
                updateItemSprite();
            } else if (scrollPosition > 0) {
                scrollPosition--;
                updateItemSprite();
            }
        } else if (martState == SELECT_QUANTITY) {
            currentAmount++;
        } else if (martState == CONFIRM_PURCHASE) {
            onYes = true;
        }
    }

    @Override
    public void clickedDown() {
        if (martState == SELECT_ITEM) {
            if (martOffset == 0 && scrollPosition < 5 && scrollPosition < martItems.size()) {
                scrollPosition++;
                updateItemSprite();
            } else if (scrollPosition < martItems.size() - 3) {
                if (scrollPosition == 5 && martItems.size() > 6 + martOffset) {
                    martOffset++;
                    updateItemSprite();
                } else if (scrollPosition < 7) {
                    scrollPosition++;
                    updateItemSprite();
                }
            }
        } else if (martState == SELECT_QUANTITY) {
            currentAmount = Math.max(0, currentAmount - 1);
        } else if (martState == CONFIRM_PURCHASE) {
            onYes = false;
        }
    }

    @Override
    public void clickedLeft() {

    }

    @Override
    public void clickedRight() {

    }

    public void clickedX() {
        if (martState == SELECT_ITEM) {
            if (!isMovingCamera) {
                isMovingCameraBack = true;
            }
        } else if (martState == SELECT_QUANTITY) {
            martState = SELECT_ITEM;
        } else if (martState == INSUFFICIENT_MONEY) {
            martState = SELECT_ITEM;
        } else if (martState == CONFIRM_PURCHASE) {
            martState = SELECT_ITEM;
            onYes = true;
        }
    }

    public void render(SpriteBatch batch) {
        if (!isMovingCamera && !isMovingCameraBack) {
            batch.draw(topLeft, 0, 1840, 48, 48);
            batch.draw(topMid, 48, 1840, 288, 48);
            batch.draw(topRight, 336, 1840, 48, 48);
            batch.draw(midLeft, 0, 1730, 48, 110);
            batch.draw(midMid, 48, 1730, 288, 110);
            batch.draw(midRight, 336, 1730, 48, 110);
            batch.draw(botLeft, 0, 1682, 48, 48);
            batch.draw(botMid, 48, 1682, 288, 48);
            batch.draw(botRight, 336, 1682, 48, 48);


            batch.draw(pokemartHUD, 0, 960, 1080, 810);

            regularFont.draw(batch, "Money:", 44, 1825);
            int moneyDigits = Integer.toString(playerProfile.getMoney()).length();
            regularFont.draw(batch, "$ " + playerProfile.getMoney(), 290 - (25 * moneyDigits), 1765);
            batch.draw(martSel, 420, 1650 - (scrollPosition * 70), 658, 72);
            for (int i = 0; i <= Math.min(6, martItems.size()); i++) {
                if (martItems.size() == i + martOffset) {
                    regularFont.draw(batch, "CANCEL", 445, 1700 - i * 70);
                } else {
                    regularFont.draw(batch, martItems.get(i + martOffset).getItem().getName(), 445, 1700 - i * 70);
                    int digits = Integer.toString(martItems.get(i + martOffset).getPrice()).length();
                    regularFont.draw(batch, "$ " + Integer.toString(martItems.get(i + martOffset).getPrice()), 1020 - (25 * digits), 1700 - i * 70);
                }
            }
            itemIcon.draw(batch);
            if (scrollPosition + martOffset < martItems.size()) {

                descriptionFont.draw(batch, martItems.get(scrollPosition + martOffset).getItem().getDescription(), 190, 1150);
            } else {
                descriptionFont.draw(batch, "Quit Shopping", 190, 1150);
            }

            if (martState != SELECT_ITEM) {
                renderMartTextBox(batch);
            }
            if (martState == SELECT_QUANTITY) {
                renderAmountTotalCostTextBox(batch);
                renderInBagTextBox(batch);
            }
            if (martState == CONFIRM_PURCHASE) {
                renderYesNoTextBox(batch);
            }





        }
    }

    private void renderInBagTextBox(SpriteBatch batch) {
        batch.draw(martTopLeft, 5, 1300, 32, 32);
        batch.draw(martTopMid, 37, 1300, 320, 32);
        batch.draw(martTopRight, 357, 1300, 32, 32);
        batch.draw(martMidLeft, 5, 1230, 32, 70);
        batch.draw(martMidMid, 37, 1230, 320, 70);
        batch.draw(martMidRight, 357, 1230, 32, 70);
        batch.draw(martBotLeft, 5, 1198, 32, 32);
        batch.draw(martBotMid, 37, 1198, 320, 32);
        batch.draw(martBotRight, 357, 1198, 32, 32);
        regularFont.draw(batch, "In Bag: " + bagQuantity, 44, 1278);
    }

    private void renderAmountTotalCostTextBox(SpriteBatch batch) {
        batch.draw(martTopLeft, 590, 1300, 32, 32);
        batch.draw(martTopMid, 622, 1300, 426, 32);
        batch.draw(martTopRight, 1048, 1300, 32, 32);
        batch.draw(martMidLeft, 590, 1230, 32, 70);
        batch.draw(martMidMid, 622, 1230, 426, 70);
        batch.draw(martMidRight, 1048, 1230, 32, 70);
        batch.draw(martBotLeft, 590, 1198, 32, 32);
        batch.draw(martBotMid, 622, 1198, 426, 32);
        batch.draw(martBotRight, 1048, 1198, 32, 32);
        regularFont.draw(batch, "x" + currentAmount, 629, 1278);
        int totalPrice = currentAmount * selectedItem.getPrice();
        regularFont.draw(batch, "$ " + totalPrice, 1020 - (25 * Integer.toString(totalPrice).length()), 1278);
    }

    private void renderYesNoTextBox(SpriteBatch batch) {
        batch.draw(martTopLeft, 861, 1370, 32, 32);
        batch.draw(martTopMid, 893, 1370, 155, 32);
        batch.draw(martTopRight, 1048, 1370, 32, 32);
        batch.draw(martMidLeft, 861, 1230, 32, 140);
        batch.draw(martMidMid, 893, 1230, 155, 140);
        batch.draw(martMidRight, 1048, 1230, 32, 140);
        batch.draw(martBotLeft, 861, 1198, 32, 32);
        batch.draw(martBotMid, 893, 1198, 155, 32);
        batch.draw(martBotRight, 1048, 1198, 32, 32);
        regularFont.draw(batch, "Yes\nNo", 958, 1348);
        int y = 1312;
        if (!onYes) {
            y = 1250;
        }
        batch.draw(selectArrowTexture, 900, y, 24, 56);
    }

    private void renderMartTextBox(SpriteBatch batch) {

        batch.draw(martTopLeft, 0, 1150, 32, 32);
        batch.draw(martTopMid, 32, 1150, 1016, 32);
        batch.draw(martTopRight, 1048, 1150, 32, 32);
        batch.draw(martMidLeft, 0, 990, 32, 160);
        batch.draw(martMidMid, 32, 990, 1016, 160);
        batch.draw(martMidRight, 1048, 990, 32, 160);
        batch.draw(martBotLeft, 0, 958, 32, 32);
        batch.draw(martBotMid, 32, 958, 1016, 32);
        batch.draw(martBotRight, 1048, 958, 32, 32);

        if (martState == SELECT_QUANTITY) {
            regularFont.draw(batch, selectedItem.getItem().getName() + "? Certainly.\nHow many would you like?", 44, 1120);
        } else if (martState == INSUFFICIENT_MONEY) {
            regularFont.draw(batch, "You don't have enough money.", 44, 1120);
        } else if (martState == CONFIRM_PURCHASE) {
            int totalPrice = currentAmount * selectedItem.getPrice();
            regularFont.draw(batch, selectedItem.getItem().getName() + ", and you want " + currentAmount + ".\nThat will be $" + totalPrice + ". OK?", 44, 1120);
        } else if (martState == FINISHED_PURCHASE) {
            regularFont.draw(batch, "Here you are!\nThank you!",44, 1120);
        }
    }
}
