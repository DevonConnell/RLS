package com.rls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;

public class Title {
	public static MyButton startButton;
	public static MyButton loadButton;
	public static MyButton quitButton;
	public static MyButton saveButton;
	public static void start() {
		loadTitleButtons();
	}
	
	public static void loadTitleButtons()
	{
		//title = new Stage(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),false);
		startButton = new MyButton(Gdx.files.internal("buttons/startButton.png"));
		//startButton = new TextureRegion(sbT,(Gdx.graphics.getWidth()/2) - (sbT.getWidth()/2),(Gdx.graphics.getHeight()/2) + (sbT.getHeight()), 128,32);
		loadButton = new MyButton(Gdx.files.internal("buttons/loadButton.png"));
		//loadButton = new TextureRegion(lbT,(Gdx.graphics.getWidth()/2) - (sbT.getWidth()/2),(Gdx.graphics.getWidth()/2) - (sbT.getHeight()/2), 128,32);
		quitButton = new MyButton(Gdx.files.internal("buttons/quitButton.png"));
		//quitButton = new TextureRegion(qbT,(Gdx.graphics.getWidth()/2) - (sbT.getWidth()/2),(Gdx.graphics.getWidth()/2) - (sbT.getHeight()) - (sbT.getHeight()), 128,32);
		
		startButton.x = (Gdx.graphics.getWidth()/2) - (startButton.getWidth()/2);
		loadButton.x = (Gdx.graphics.getWidth()/2) - (loadButton.getWidth()/2);
		quitButton.x = (Gdx.graphics.getWidth()/2) - (quitButton.getWidth()/2);
		startButton.y = (Gdx.graphics.getHeight()/2) + (startButton.getHeight());
		loadButton.y = (Gdx.graphics.getHeight()/2) - (loadButton.getHeight()/2);
		quitButton.y = (Gdx.graphics.getHeight()/2) - (quitButton.getHeight()) - (quitButton.getHeight());
		//Gdx.app.getType() == ApplicationType.Android)
	}
	public static void disposeTitleButtons()
	{
		startButton.dispose();
		loadButton.dispose();
		quitButton.dispose();
	}
	
	public static void loadMenuButtons()
	{
		saveButton = new MyButton(Gdx.files.internal("buttons/saveButton.png"));
		loadButton = new MyButton(Gdx.files.internal("buttons/loadButton.png"));
		quitButton = new MyButton(Gdx.files.internal("buttons/quitButton.png"));
		
		saveButton.x = (Gdx.graphics.getWidth()/2) - (saveButton.getWidth()/2);
		loadButton.x = (Gdx.graphics.getWidth()/2) - (loadButton.getWidth()/2);
		quitButton.x = (Gdx.graphics.getWidth()/2) - (quitButton.getWidth()/2);
		saveButton.y = (Gdx.graphics.getHeight()/2) + (saveButton.getHeight());
		loadButton.y = (Gdx.graphics.getHeight()/2) - (loadButton.getHeight()/2);
		quitButton.y = (Gdx.graphics.getHeight()/2) - (quitButton.getHeight()) - (quitButton.getHeight());
	}
	public static void disposeMenuButtons()
	{
		saveButton.dispose();
		loadButton.dispose();
		quitButton.dispose();
	}
	
	
	public static void render()
	{
		if(Gdx.input.justTouched())
		{
			int x = Gdx.input.getX();
			int y = Gdx.graphics.getHeight()-Gdx.input.getY()+startButton.getHeight();
			Gdx.app.log("JUST_TOUCHED", "x:"+x+" y:"+y);
			/*Gdx.app.log(" startButton", "x:"+startButton.x+" y:"+startButton.y);
			Gdx.app.log("  startButton", "y:"+startButton.y+" height:"+startButton.getHeight());
			Gdx.app.log(" loadButton", "x:"+loadButton.x+" y:"+loadButton.y);
			Gdx.app.log(" quitButton", "x:"+quitButton.x+" y:"+quitButton.y);*/
			//Gdx.app.log("Screen-y:", Gdx.graphics.getHeight()-y)
			if(x > startButton.x && x < startButton.x+startButton.getWidth()) //we're within the x-range of all the buttons
			{
				//Gdx.app.log("^__^", "y-height"+(startButton.y-startButton.getHeight()));
				if(y < startButton.y && y > startButton.y-startButton.getHeight())
				{
					//Gdx.app.log("renderTitle()", "pressed start!");
					RedLineSamurai.CURRENT_MODE = RedLineSamurai.VISUAL_NOVEL_MODE;
					RedLineSamurai.SHOW_CONTROLS=false;
					Sound.introBam.dispose(); //Sound.introBam.play(arg0)
					disposeTitleButtons();
					Record.imprintChoices.clear();
					Record.imprintImageCoords.clear();
					Record.imprintImages.clear();
					
					VisualNovel.loadNextFile("start.txt");//startGame(); //////////////////////////////////////////////////////CHANGE FOR TESTINGGGGGGG
					return;
				}
				else if(y < loadButton.y && y > loadButton.y-loadButton.getHeight())
				{
					//Gdx.app.log("renderTitle()", "pressed load!");
					if(Record.loadFromSave())
					{
						//RedLineSamurai.AT_TITLE=false; //TODO: COULD BE GAMEPLAY OR VN
						RedLineSamurai.CURRENT_MODE = RedLineSamurai.VISUAL_NOVEL_MODE;
						Sound.introBam.dispose();
						disposeTitleButtons();
						RedLineSamurai.SHOW_CONTROLS=false;
						return;
					}
				}
				else if(y < quitButton.y && y > quitButton.y-quitButton.getHeight())
				{
					if(RedLineSamurai.SHOW_CONTROLS)
						RedLineSamurai.SHOW_CONTROLS=false;
					else
						RedLineSamurai.SHOW_CONTROLS=true;//quitGame();
				}
			}
		}
		
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		RedLineSamurai.batch.begin();
		//title.draw();
		RedLineSamurai.batch.draw(Art.currentBG,0,0);
		if(RedLineSamurai.SHOW_CONTROLS)
		{
			RedLineSamurai.batch.draw(Art.nvlOverlay,0,-425);
			RedLineSamurai.batch.draw(Art.nvlOverlay,0,-425);
			RedLineSamurai.batch.draw(Art.nvlOverlay,0,-425);
		}
		RedLineSamurai.batch.draw(startButton,startButton.x,startButton.y-startButton.getHeight()*2f);
		RedLineSamurai.batch.draw(loadButton,loadButton.x,loadButton.y-startButton.getHeight()*2f);
		RedLineSamurai.batch.draw(quitButton,quitButton.x,quitButton.y-startButton.getHeight()*2f);
		//batch.draw(startButton, startButton., ADV_X);
		
		if(RedLineSamurai.SHOW_CONTROLS)
		{
			float yInc = Gdx.graphics.getHeight()/20.0f;
			Font.bFontBold.draw(RedLineSamurai.batch, ":::::Controls for RedLineSamurai::::", RedLineSamurai.NVL_X, RedLineSamurai.NVL_Y);
			Font.bFont.draw(RedLineSamurai.batch, "Touch the screen to advance", RedLineSamurai.NVL_X, RedLineSamurai.NVL_Y-yInc*1);
			Font.bFont.draw(RedLineSamurai.batch, " through the story and make decisions", RedLineSamurai.NVL_X, RedLineSamurai.NVL_Y-yInc*2);
			Font.bFont.draw(RedLineSamurai.batch, "The search and menu buttons", RedLineSamurai.NVL_X, RedLineSamurai.NVL_Y-yInc*3);
			Font.bFont.draw(RedLineSamurai.batch, " will bring up the game menu", RedLineSamurai.NVL_X, RedLineSamurai.NVL_Y-yInc*4);
			Font.bFont.draw(RedLineSamurai.batch, "Holding down a touch", RedLineSamurai.NVL_X, RedLineSamurai.NVL_Y-yInc*5);
			Font.bFont.draw(RedLineSamurai.batch, " fast-forwards through the text", RedLineSamurai.NVL_X, RedLineSamurai.NVL_Y-yInc*6);
			Font.bFont.draw(RedLineSamurai.batch, " (run through previously read scenes)", RedLineSamurai.NVL_X, RedLineSamurai.NVL_Y-yInc*7);
		}
		
		RedLineSamurai.batch.end();
	}
}
