package com.rls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Font {
	public static BitmapFont bFont;
	public static BitmapFont bFontDecision;
	public static BitmapFont bFontBold;
	
	public static void load() {
		FileHandle f = Gdx.files.internal("font/myriad.fnt");
		FileHandle f2 = Gdx.files.internal("font/prestige.fnt");
		FileHandle f3 = Gdx.files.internal("font/myriadBold.fnt");
		
		bFont = new BitmapFont(f,false);
		bFontDecision = new BitmapFont(f2,false);
		bFontBold = new BitmapFont(f3,false);
	}
}
