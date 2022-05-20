package com.anime.arena.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.anime.arena.AnimeArena;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Anime-Arena";
		config.resizable = false;
		config.height = 960;
		config.width = 540;
		new LwjglApplication(new AnimeArena(), config);
	}
}
