package com.rls;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RedLineSamurai implements ApplicationListener{
	public static int TITLE_SCREEN_MODE = 1;
	public static int VISUAL_NOVEL_MODE = 2;
	public static int GAMEPLAY_MODE = 3;
	public static int CURRENT_MODE = 1;
	
	public static boolean IS_PAUSED = false; 
	public static boolean SAVED_GAME = false;
	public static boolean SHOW_CONTROLS = false;
	//public static boolean AT_TITLE = true; //TODO: don't use this, use modes
	
	public static float ADV_X = 480f;
	public static float ADV_Y = 480f;
	public static float NVL_X = 480f;
	public static float NVL_Y = 480f;
	
	public static long currentTime = System.currentTimeMillis();
	public static boolean PRESSING=false;
	public static long pressingStartTime=System.currentTimeMillis();
	public static long pressingTimeTotal=0;
	
	public static SpriteBatch batch;
	
	@Override
	public void create() {
		Gdx.app.log("Red Line Samurai", "create()");
		batch = new SpriteBatch();
			
		Font.load();
		Art.load();
		Sound.load();
		Text.load();
		Record.loadClass();
		
		Sound.playSFX("introBam");
		Title.start();
	}

	@Override
	public void dispose() {
		Gdx.app.log("RLS", "dispose()");
		//Art.dispose();
		//Sound.dispose();
	}

	@Override
	public void pause() {
		Gdx.app.log("RLS", "pause()");
		IS_PAUSED = true;
	}

	@Override
	public void render() {
		if(!IS_PAUSED) {
			currentTime = System.currentTimeMillis();
			
			if(Gdx.input.justTouched())
			{
				PRESSING = true;
				pressingStartTime = currentTime;
				pressingTimeTotal = 0;
			}
			if(PRESSING && Gdx.input.isTouched())
			{
				pressingTimeTotal = currentTime - pressingStartTime;
			}
			else if(PRESSING && !Gdx.input.isTouched())
			{
				PRESSING = false;
				pressingTimeTotal = 0;
			}
			
			if(CURRENT_MODE == TITLE_SCREEN_MODE)
				Title.render();
			else if(CURRENT_MODE == VISUAL_NOVEL_MODE)
				VisualNovel.render();
			else if(CURRENT_MODE == GAMEPLAY_MODE)
				Gameplay.render();
		} else {
			if(CURRENT_MODE == VISUAL_NOVEL_MODE)
				renderVNPaused();
			else if(CURRENT_MODE == GAMEPLAY_MODE)
				renderGameplayPaused();
		}
		
		//Gdx.gl.glClearColor(1, 0, 0, 1);
		//Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	}
	
	public void renderVNPaused()
	{
		if(Gdx.input.isKeyPressed(Keys.HOME) || Gdx.input.isKeyPressed(Keys.ESCAPE) || Gdx.input.isKeyPressed(Keys.MENU) || Gdx.input.isKeyPressed(Keys.SEARCH))
		{
			SAVED_GAME=false;
			IS_PAUSED=false;
			SHOW_CONTROLS=false;
			Title.disposeMenuButtons();
			return;
		}
		if(Gdx.input.justTouched())
		{
			int x = Gdx.input.getX();
			int y = Gdx.graphics.getHeight()-Gdx.input.getY()+Title.saveButton.getHeight();

			if(x > Title.saveButton.x && x < Title.saveButton.x+Title.saveButton.getWidth()) //we're within the x-range of all the buttons
			{
				if(y < Title.saveButton.y && y > Title.saveButton.y-Title.saveButton.getHeight())
				{
					Gdx.app.log("renderTitle()", "pressed save!");
					Record.saveGame();
				}
				else if(y < Title.loadButton.y && y > Title.loadButton.y-Title.loadButton.getHeight())
				{
					if(Record.loadFromSave())
					{
						IS_PAUSED=false;
						VisualNovel.AT_DECISION=false;
						Title.disposeMenuButtons();
						SAVED_GAME=false;
						SHOW_CONTROLS=false;
						return;
					}
				}
				else if(y < Title.quitButton.y && y > Title.quitButton.y-Title.quitButton.getHeight())
				{
					if(SHOW_CONTROLS)
						SHOW_CONTROLS=false;
					else
						SHOW_CONTROLS=true;//quitGame();
				}
			}
			else
			{
				SAVED_GAME=false;
				IS_PAUSED=false;
				Title.disposeMenuButtons();
				SHOW_CONTROLS=false;
				return;
			}
		}
		
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.begin();
		//title.draw();
		batch.draw(Art.currentBG,0,0);
		batch.draw(Title.saveButton,Title.saveButton.x,Title.saveButton.y-Title.saveButton.getHeight()*2f);
		batch.draw(Title.loadButton,Title.loadButton.x,Title.loadButton.y-Title.saveButton.getHeight()*2f);
		batch.draw(Title.quitButton,Title.quitButton.x,Title.quitButton.y-Title.saveButton.getHeight()*2f);
		//batch.draw(startButton, startButton., ADV_X);
		if(SAVED_GAME)
		{
			Font.bFontDecision.draw(batch, "Player has saved the game!", NVL_X, NVL_Y);
		}
		if(SHOW_CONTROLS)
		{
			float yInc = Gdx.graphics.getHeight()/20.0f;
			Font.bFontBold.draw(batch, ":::::Controls for AzorusIV::::", NVL_X, NVL_Y-yInc);
			Font.bFont.draw(batch, "Touch the screen to advance", NVL_X, NVL_Y-yInc*2);
			Font.bFont.draw(batch, " through the story and make decisions", NVL_X, NVL_Y-yInc*3);
			Font.bFont.draw(batch, "The search and menu buttons", NVL_X, NVL_Y-yInc*4);
			Font.bFont.draw(batch, " will bring up the game menu", NVL_X, NVL_Y-yInc*5);
			Font.bFont.draw(batch, "Holding down a touch", NVL_X, NVL_Y-yInc*6);
			Font.bFont.draw(batch, " fast-forwards through the text", NVL_X, NVL_Y-yInc*7);
			Font.bFont.draw(batch, " (run through previously read scenes)", NVL_X, NVL_Y-yInc*8);
		}
		
		batch.end();
	}

	public void renderGameplayPaused() {
		if(Gdx.input.isKeyPressed(Keys.HOME) || Gdx.input.isKeyPressed(Keys.ESCAPE) || Gdx.input.isKeyPressed(Keys.MENU) || Gdx.input.isKeyPressed(Keys.SEARCH))
		{
			SAVED_GAME=false;
			IS_PAUSED=false;
			SHOW_CONTROLS=false;
			Title.disposeMenuButtons();
			return;
		}
		
		Gdx.gl.glClearColor(0,0,1,1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	}
	
	@Override
	public void resize(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		IS_PAUSED = false;
	}

	public static void goBackToTitle()
	{
		Art.showBG("title");
		Art.currentImages.clear();
		Title.loadTitleButtons();
		
		CURRENT_MODE = TITLE_SCREEN_MODE;
		VisualNovel.AT_DECISION = false;
		IS_PAUSED = false;
		Sound.playSFX("introBam");
		
		Record.imprintChoices.clear();
		Record.imprintImages.clear();
		Record.imprintImageCoords.clear();
		//Record.
	}
	
}
