package com.anime.arena.tools;

import com.anime.arena.objects.NPCObject;
import com.anime.arena.objects.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gregorykelsie on 2018-11-11.
 */

public class OrthogonalTileSpriteRenderer extends OrthogonalTiledMapRenderer {
    private List<Sprite> sprites;
    private boolean hasDrawnPlayer;
    private PokemonMap pokemonMap;
    private Player player;
    public OrthogonalTileSpriteRenderer(PokemonMap map, float unitScale) {
        super(map.getMap(), unitScale);
        this.pokemonMap = map;
        hasDrawnPlayer = false;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void resetMap() {
        this.map = pokemonMap.getMap();
    }



    @Override
    public void render() {
        beginRender();
        int currentLayer = 0;
        int totalLayers = map.getLayers().size();

        for (MapLayer layer: map.getLayers()) {
            if (layer.getProperties().containsKey("layerType") && (int)layer.getProperties().get("layerType") == 1) {
                pokemonMap.renderTreesAbovePlayer(this.getBatch(), player);
                pokemonMap.renderItems(this.getBatch());
                pokemonMap.renderBerriesAbovePlayer(this.getBatch(), player);
                pokemonMap.renderNPCsAbovePlayer(this.getBatch(), player);
                pokemonMap.renderTrainersAbovePlayer(this.getBatch(), player);
                player.draw(this.getBatch());
                pokemonMap.renderTreesBelowPlayer(this.getBatch(), player);
                pokemonMap.renderBerriesBelowPlayer(this.getBatch(), player);
                pokemonMap.renderNPCsBelowPlayer(this.getBatch(), player);
                pokemonMap.renderTrainersBelowPlayer(this.getBatch(), player);

                hasDrawnPlayer = true;
            }
            if (layer.isVisible()) {
                if (layer instanceof TiledMapTileLayer) {
                    renderTileLayer((TiledMapTileLayer) layer);
                } else {
                }
            } else {
                //Gdx.app.log("invisible", "");
            }
            currentLayer++;
        }
        if (!hasDrawnPlayer) {
            pokemonMap.renderTreesAbovePlayer(this.getBatch(), player);
            pokemonMap.renderItems(this.getBatch());
            pokemonMap.renderBerriesAbovePlayer(this.getBatch(), player);
            pokemonMap.renderNPCsAbovePlayer(this.getBatch(), player);
            pokemonMap.renderTrainersAbovePlayer(this.getBatch(), player);
            player.draw(this.getBatch());
            pokemonMap.renderTreesBelowPlayer(this.getBatch(), player);
            pokemonMap.renderBerriesBelowPlayer(this.getBatch(), player);
            pokemonMap.renderNPCsBelowPlayer(this.getBatch(), player);
            pokemonMap.renderTrainersBelowPlayer(this.getBatch(), player);

            hasDrawnPlayer = true;
        }
        pokemonMap.renderNPCEmojis(this.getBatch());

        hasDrawnPlayer = false;

        endRender();
    }
}