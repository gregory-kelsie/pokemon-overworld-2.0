package com.anime.arena.tools;

import com.anime.arena.interactions.NPCFactory;
import com.anime.arena.objects.*;
import com.anime.arena.pokemon.*;
import com.anime.arena.pokemon.wildpokemon.WildPokemon;
import com.anime.arena.pokemon.wildpokemon.WildPokemonMap;
import com.anime.arena.screens.PlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PokemonMap {
    
    private PlayScreen screen;

    //Map Objects
    private TiledMap map;
    private List<TreeObject> trees;
    private List<ItemObject> items;
    private List<BerryObject> berries;
    private List<NPCObject> npcs;
    private List<TrainerObject> trainers;
    private List<WarpObject> warps;
    private List<EventObject> eventObjects;


    //Map Variables
    private int mapWidth;
    private int mapHeight;
    private int tileWidth;
    private int tileHeight;
    private String mapName;
    private String mapBGM;
    private WildPokemonMap wildPokemonInfo;
    
    //Object Types
    public static final int TREE_OBJECT = 12;
    public static final int ITEM_OBJECT = 0;
    public static final int TM_OBJECT = 1;
    public static final int BERRY_OBJECT = 2;
    public static final int NPC_OBJECT = 3;
    public static final int TRAINER_OBJECT = 4;
    public static final int WARP_OBJECT = 5;
    public static final int EVENT_OBJECT = 6;

    private static final String COLLISION_LAYER_NAME = "Collision";
    private static final String OBJECT_LAYER_NAME = "O2";
    
    public PokemonMap(TiledMap map, PlayScreen screen) {
        this.map = map;
        this.screen = screen;
        initMapObjects();
        initMapVariables();
        initAnimatedTiles();
    }


    public void setMap(TiledMap newMap) {
        resetMapObjects();
        map.dispose();
        this.map = newMap;
        initMapVariables();
        if (map.getLayers().get(OBJECT_LAYER_NAME) != null) {
            MapObjects objects = map.getLayers().get(OBJECT_LAYER_NAME).getObjects();
            createMapObjects(objects);
        }
        initAnimatedTiles();
    }

    private void initAnimatedTiles() {
        HashMap<String, List<StaticTiledMapTile>> animatedTileHashMap = new HashMap<String, List<StaticTiledMapTile>>();
        TiledMapTileSets tilesets = map.getTileSets();
        Iterator<TiledMapTileSet> tilesetIterator = tilesets.iterator();
        boolean hasAnimationTileset = false;
        //Check if there's an animation tileset
        while (tilesetIterator.hasNext()) {
            String tileSetName = tilesetIterator.next().getName();
            Gdx.app.log("TileSetName", tileSetName);
            if (tileSetName.equals("animation")) {
                hasAnimationTileset = true;
                break;
            }
        }
        if (hasAnimationTileset) {
            //Get the animated tileset
            Iterator<TiledMapTile> tiles = map.getTileSets().getTileSet("animation").iterator();
            while (tiles.hasNext()) {
                TiledMapTile tile = tiles.next();
                //Sort the animation tiles into animationTypes
                if (tile.getProperties().containsKey("animationType")) {
                    String animationType = (String) tile.getProperties().get("animationType");
                    if (!animatedTileHashMap.containsKey(animationType)) {
                        animatedTileHashMap.put(animationType, new ArrayList<StaticTiledMapTile>());
                    }
                    animatedTileHashMap.get(animationType).add((StaticTiledMapTile) tile);
                }
            }

            //Convert the animation frames for each animationType into an AnimatedTiledMapTile
            HashMap<String, AnimatedTiledMapTile> animatedTiles = new HashMap<String, AnimatedTiledMapTile>();
            for (String key : animatedTileHashMap.keySet()) {
                Array<StaticTiledMapTile> arr = new Array<StaticTiledMapTile>(animatedTileHashMap.get(key).size());
                for (StaticTiledMapTile frame : animatedTileHashMap.get(key)) {
                    arr.add(frame);
                }
                animatedTiles.put(key, new AnimatedTiledMapTile(1 / 3f, arr));
            }

            //Replace the tiles in the TiledMap that match the animationType with the AnimatedTiledMapTile
            for (int l = 0; l < map.getLayers().size(); l++) {
                MapLayer layer = map.getLayers().get(l);
                if (layer instanceof TiledMapTileLayer) {
                    TiledMapTileLayer currentLayer = (TiledMapTileLayer) layer;
                    for (int x = 0; x < currentLayer.getWidth(); x++) {
                        for (int y = 0; y < currentLayer.getHeight(); y++) {
                            TiledMapTileLayer.Cell cell = currentLayer.getCell(x, y);
                            if (cell != null && cell.getTile().getProperties().containsKey("animationType")) {
                                if (animatedTiles.containsKey(cell.getTile().getProperties().get("animationType", String.class))) {
                                    String cellValue = cell.getTile().getProperties().get("animationType", String.class);
                                    AnimatedTiledMapTile at = animatedTiles.get(cellValue);
                                    cell.setTile(at);
                                }
                            }

                        }
                    }
                }

            }
        }

    }

    public String getMapName() {
        return mapName;
    }

    public Pokemon getWildPokemon(BasePokemonFactory factory) {
        WildPokemon wp = wildPokemonInfo.getRandomPokemon();
        if (wp != null) {
            BasePokemon bp = factory.createBasePokemon(wp.getPokemonID());
            UniquePokemon up = new UniquePokemon();
            Pokemon p = new Pokemon(bp, up);
            PokemonUtils.initBlankPokemonData(p, wp.generateLevel());
            Gdx.app.log("WildPokemon Spawn", bp.getName());
            return new Pokemon(bp, up);
        }
        return null;
    }

    public String getBGM() {
        return mapBGM;
    }

    private void initMapVariables() {
        mapWidth = map.getProperties().get("width", Integer.class);
        mapHeight = map.getProperties().get("height", Integer.class);
        tileWidth = map.getProperties().get("tilewidth", Integer.class);
        tileHeight = map.getProperties().get("tileheight", Integer.class);
        mapName = (String)map.getProperties().get("mapName");
        if (mapName == null) {
            mapName = "";
        }
        mapBGM = (String)map.getProperties().get("mapBGM");
        if (mapBGM == null) {
            mapBGM = "";
        }
        loadWildPokemon();

    }

    /**
     * Load the Wild Pokemon Data from the Map Properties in Tiled.
     * Two Strings in map properties make up a wild pokemon's data
     * 1. Chances Variable (dayPokemonChances) The Pokemon ID and their probability of appearing in the wild
     * 2. The Level Range Variable (dayPokemonLevels)
     * The format of dayPokemonChances to spawn a Rattata and Pidgey at 50% each - 19,0.5;16,0.5
     * The format of dayPokemonLevels to spawn Rattata between 3-5 and Pidgey 2,4 - 3-5;2-4
     * Note that order matters. Rattata is first in dayPokemonChances therefore its level range comes first in dayPokemonLevels
     */
    private void loadWildPokemon() {
        wildPokemonInfo = new WildPokemonMap();
        String dayPokemonChances = (String)map.getProperties().get("dayPokemonChances");
        String dayPokemonLevels = (String)map.getProperties().get("dayPokemonLevels");
        if (dayPokemonChances != null && dayPokemonLevels != null) {
            String[] eachPokemon = dayPokemonChances.split(";");
            String[] eachLevelRange = dayPokemonLevels.split(";");
            if (eachPokemon.length == eachLevelRange.length) {
                for (int i = 0; i < eachPokemon.length; i++) {
                    String[] pokemonChances = eachPokemon[i].split(",");
                    int pokemonID = Integer.parseInt(pokemonChances[0]);
                    double probability = Double.parseDouble(pokemonChances[1]);
                    String[] pokemonLevelRange = eachLevelRange[i].split("-");
                    int lowerLevel = Integer.parseInt(pokemonLevelRange[0]);
                    int higherLevel = Integer.parseInt(pokemonLevelRange[1]);
                    wildPokemonInfo.addWildPokemon(pokemonID, probability, lowerLevel, higherLevel);
                }
            } else {
                Gdx.app.log("loadWildPokemon", "dayPokemonChances and dayPokemonLevels aren't equal array sizes: " +
                        eachPokemon.length + ", " + eachLevelRange.length);
            }
        }
    }

    private void resetMapObjects() {
        for (TreeObject t : trees) {
            t.dispose();
        }
        trees.clear();
        warps.clear();
        eventObjects.clear();
        for (ItemObject i : items) {
            i.dispose();
        }
        items.clear();
        for (BerryObject b : berries) {
            b.dispose();
        }
        berries.clear();
        for (NPCObject n : npcs) {
            n.dispose();
        }
        npcs.clear();
        for (TrainerObject t : trainers) {
            t.dispose();
        }
        trainers.clear();
    }



    private void initMapObjects() {
        trees = new ArrayList<TreeObject>();
        items = new ArrayList<ItemObject>();
        berries = new ArrayList<BerryObject>();
        npcs = new ArrayList<NPCObject>();
        trainers = new ArrayList<TrainerObject>();
        warps = new ArrayList<WarpObject>();
        eventObjects = new ArrayList<EventObject>();
        if (map.getLayers().get(OBJECT_LAYER_NAME) != null) {
            MapObjects objects = map.getLayers().get(OBJECT_LAYER_NAME).getObjects();
            createMapObjects(objects);
        }
    }

    private void createMapObjects(MapObjects objects) {
        //Initialize factories used to create the map objects
        BerryFactory bf = new BerryFactory(screen);
        NPCFactory npcFactory = new NPCFactory();

        //Create the map objects from the objectType property
        for (MapObject object : objects) {
            if (object instanceof TextureMapObject) {
                TextureMapObject mapObject = (TextureMapObject) object;
                if (object.getProperties().containsKey("objectType")) {
                    int objectType = (int) object.getProperties().get("objectType");
                    if (objectType == TREE_OBJECT) {
                        addTreeObject(mapObject);
                    } else if (objectType == ITEM_OBJECT || objectType == TM_OBJECT) {
                        addItemObject(objectType, mapObject);
                    } else if (objectType == BERRY_OBJECT) {
                        addBerryObject(bf, mapObject);
                    } else if (objectType == NPC_OBJECT) {
                        addNPCObject(npcFactory, mapObject);
                    } else if (objectType == TRAINER_OBJECT) {
                        addTrainerObject(npcFactory, mapObject);
                    } else if (objectType == WARP_OBJECT) {
                        addWarpObject(mapObject);
                    } else if (objectType == EVENT_OBJECT) {
                        addEventObject(mapObject);
                    }
                }

            }
        }
    }

    public TreeObject getTree(int x, int y) {
        for (TreeObject tree : trees) {
            if (tree.isVisible() && tree.occupiesCell(x, y)) {
                return tree;
            }
        }
        return null;
    }

    public ItemObject getItemObject(int x, int y) {
        for (ItemObject item : items) {
            if (item.isVisible() && item.occupiesCell(x, y)) {
                return item;
            }
        }
        return null;
    }

    public BerryObject getBerryObject(int x, int y) {
        for (BerryObject berry : berries) {
            if (berry.isVisible() && berry.occupiesCell(x, y)) {
                return berry;
            }
        }
        return null;
    }

    public NPCObject getNPCObject(int x, int y) {
        for (NPCObject npc : npcs) {
            if (npc.isVisible() && npc.occupiesCell(x, y)) {
                return npc;
            }
        }
        return null;
    }

    public TrainerObject getTrainerObject(int x, int y) {
        for (TrainerObject npc : trainers) {
            if (npc.isVisible() && npc.occupiesCell(x, y)) {
                return npc;
            }
        }
        return null;
    }

    private void addEventObject(TextureMapObject mapObject) {
        String script = (String) mapObject.getProperties().get("script");
        eventObjects.add(new EventObject(script, convertToTile(mapObject.getX()), convertToTile(mapObject.getY()), screen));
    }

    private void addWarpObject(TextureMapObject mapObject) {
        String mapName = (String) mapObject.getProperties().get("mapName");
        int x = (int) mapObject.getProperties().get("x");
        int y = (int) mapObject.getProperties().get("y");
        warps.add(new WarpObject(mapName, x, y, convertToTile(mapObject.getX()), convertToTile(mapObject.getY()), screen));
    }

    private void addTreeObject(TextureMapObject mapObject)  {
        trees.add(new TreeObject(convertToTile(mapObject.getX()), convertToTile(mapObject.getY()), screen));
    }

    private void addItemObject(int objectType, TextureMapObject mapObject) {
        int itemAmount = (int) mapObject.getProperties().get("itemAmount");
        int itemId = (int) mapObject.getProperties().get("itemID");
        items.add(new ItemObject(convertToTile(mapObject.getX()), convertToTile(mapObject.getY()), screen, itemId, itemAmount, objectType));
    }

    private void addBerryObject(BerryFactory bf, TextureMapObject mapObject) {
        int berryId = (int) mapObject.getProperties().get("berryID");
        int berryType = (int) mapObject.getProperties().get("berryType");
        berries.add(bf.getBerryTree(berryId, berryType, convertToTile(mapObject.getX()), convertToTile(mapObject.getY())));
    }

    private void addNPCObject(NPCFactory npcFactory, TextureMapObject mapObject) {
        String npcId = (String) mapObject.getProperties().get("npcID");
        String npcOverworld = (String) mapObject.getProperties().get("overworld");
        npcs.add(npcFactory.createNPC(npcId, npcOverworld, screen, convertToTile(mapObject.getX()), convertToTile(mapObject.getY())));
    }

    private void addTrainerObject(NPCFactory npcFactory, TextureMapObject mapObject) {
        String trainerID = (String) mapObject.getProperties().get("npcID");
        String npcOverworld = (String) mapObject.getProperties().get("overworld");
        trainers.add(npcFactory.createTrainer(trainerID, npcOverworld, screen, convertToTile(mapObject.getX()), convertToTile(mapObject.getY())));
    }
    
    private int convertToTile(float pos) {
        return (int) pos / 16;
    }

    public TiledMapTileLayer getCollisionLayer() {
        return (TiledMapTileLayer) map.getLayers().get(COLLISION_LAYER_NAME);
    }

    public TiledMap getMap() {
        return map;
    }

    public int getMapWidth() {
        return mapWidth * tileWidth;
    }

    public int getMapHeight() {
        return mapHeight * tileHeight;
    }

    public int getMapWidthInTiles() {
        return mapWidth;
    }

    public int getMapHeightInTiles() {
        return mapHeight;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public List<TreeObject> getTrees() {
        return trees;
    }

    public List<ItemObject> getItems() {
        return items;
    }

    public List<WarpObject> getWarps() {
        return warps;
    }

    public List<EventObject> getEventObjects() {
        return eventObjects;
    }

    public List<NPCObject> getNPCs() { return npcs; }

    public List<TrainerObject> getTrainers() { return trainers; }

    public List<BerryObject> getBerryTrees() {
        return berries;
    }

    public void update(float dt) {
        for (BerryObject berryObject : berries) {
            berryObject.update(dt);
        }
        for (NPCObject npc : npcs) {
            npc.update(dt);
        }
        for (TrainerObject trainer : trainers) {
            trainer.update(dt);
        }
    }

    public void renderTreesBelowPlayer(Batch batch, Player player) {
        for (TreeObject tree : trees) {
            if (tree.isVisible() && tree.getY() < player.getYTile()) {
                tree.render(batch);
            }
        }
    }

    public void renderTreesAbovePlayer(Batch batch, Player player) {
        for (TreeObject tree : trees) {
            if (tree.isVisible() && tree.getY() >= player.getYTile()) {
                tree.render(batch);
            }
        }
    }

    public void renderBerriesBelowPlayer(Batch batch, Player player) {
        for (BerryObject berryObject : berries) {
            if (berryObject.isVisible() && berryObject.getY() < player.getYTile()) {
                berryObject.getSprite().draw(batch);
            }
        }
    }

    public void renderBerriesAbovePlayer(Batch batch, Player player) {
        for (BerryObject berryObject : berries) {
            if (berryObject.isVisible() && berryObject.getY() >= player.getYTile()) {
                berryObject.getSprite().draw(batch);
            }
        }
    }

    public void renderNPCsAbovePlayer(Batch batch, Player player) {
        for (NPCObject npc : npcs) {
            if (npc.isVisible() && npc.getY() >= player.getYTile()) {
                npc.draw(batch);
            }
        }
    }

    public void renderTrainersAbovePlayer(Batch batch, Player player) {
        for (TrainerObject npc : trainers) {
            if (npc.isVisible() && npc.getY() >= player.getYTile()) {
                npc.draw(batch);
            }
        }
    }

    public void renderNPCsBelowPlayer(Batch batch, Player player) {
        for (NPCObject npc : npcs) {
            if (npc.isVisible() && npc.getY() < player.getYTile()) {
                npc.draw(batch);
            }
        }
    }

    public void renderTrainersBelowPlayer(Batch batch, Player player) {
        for (TrainerObject npc : trainers) {
            if (npc.isVisible() && npc.getY() < player.getYTile()) {
                npc.draw(batch);
            }
        }
    }


    public void renderItems(Batch batch) {
        for (ItemObject item : items) {
            if (item.isVisible()) {
                item.render(batch);
            }
        }
    }

    public void renderNPCEmojis(Batch batch) {
        for (NPCObject npc : npcs) {
            npc.drawEmoji(batch, screen.getPlayer().getSwitches());
        }
    }

    public void dispose() {
        map.dispose();
        for (TreeObject treeObject : trees) {
            treeObject.dispose();
        }
        for (ItemObject itemObject : items) {
            itemObject.dispose();
        }
        for (BerryObject berryObject : berries) {
            berryObject.dispose();
        }
    }
}
