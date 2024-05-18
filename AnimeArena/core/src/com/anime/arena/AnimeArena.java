package com.anime.arena;

import com.anime.arena.screens.*;
import com.anime.arena.test.BattleTest;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class AnimeArena extends Game {
	private SpriteBatch batch;

	public static final int V_WIDTH = 16 * 14;
	public static final int V_HEIGHT = 16 * 14;//16 * 9;
	public static final float PPM = 1f; //32 pixels is a meter

	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private TmxMapLoader loader ;
	private OrthographicCamera camera;


	public SpriteBatch getBatch() {
		return batch;
	}
	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new BattleTestScreen(this));
		//setScreen(new TitleScreen(this));

		//setScreen(new PokemonTestScreen(this));

		//setScreen(new AnimationScreen(this));
		//setScreen(new BagTestScreen(this));
		//setScreen(new PlayScreen(this));

		//setScreen(new WildPokemonTransitionTestScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		screen.dispose();
	}
}
