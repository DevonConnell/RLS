package com.rls;

import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Art {
	
	//VN ART STUFF --------
	public static float assumeX = 512f;
	public static float assumeY = 512f;
	
	public static MyTextureRegion currentBG;
	public static Vector<CharacterSprite> currentImages = new Vector<CharacterSprite>();
	
	public static TextureRegion nvlOverlay;
	public static TextureRegion advOverlay;
	
	public static boolean PUNCHING = false;
	public static boolean PUNCHING_HORIZONTAL = false;
	public static long punchingStartTime = System.currentTimeMillis();
	public float punchingTotalTime = 3.0f;
	public static int punchCounter = 0;
	public static int maxPunchMove = 85;
	public static int PUNCH_GOING_UP = 0;
	// --------------------
	
	public static void load() {
		MyTextureRegion title = load("backgrounds/title.jpg", 512, 512);
		title.name = "title";
		resize(title);
		currentBG = title;
		
		advOverlay = load("backgrounds/bottomTextShadow.png",1024,256);
		nvlOverlay = load("backgrounds/fullTextShadow.png",1024,1024);
	}
	
	private static void resize(TextureRegion image)
	{
		float x = Gdx.graphics.getWidth();
        float y = Gdx.graphics.getHeight();
        //Gdx.app.log("resize()", ""+x+" "+y);
        
        float changeX = x / assumeX;
        float changeY = y / assumeY;
        //Gdx.app.log("change", ""+changeX+" "+changeY);
        
        float width = image.getRegionWidth() * changeX;
	    float height = image.getRegionHeight() * changeY;
	    //Gdx.app.log("image w and h", ""+width+" "+height);
	    
	    //Gdx.app.log("regions X and Y", ""+image.getRegionX()+" "+image.getRegionY());
        Vector2 position = new Vector2(image.getRegionX() * changeX, image.getRegionY() * changeY);
        Vector2 bounds = new Vector2 (position.x, (Gdx.graphics.getHeight() - position.y) - height);
        
        image.setRegion((int)bounds.x, (int)bounds.y, (int)width, (int)height);
        
	}
	
	public static void showBG(String bgName)
	{
		MyTextureRegion tR = load("backgrounds/"+bgName+".jpg", 800, 600);//512, 512);
		tR.name=bgName;
		resize(tR);
		currentBG = tR;
	}
	
	public static void showImage(String imageName, int xPos, int yPos)
	{
		//Gdx.app.log("showImage()", imageName+" "+xPos+" "+yPos);
		if(imageName.contains("Gale_face") || imageName.contains("Sheiletta_face") || imageName.contains("Naomi_face"))
		{
			Texture texture = new Texture(Gdx.files.internal("characters/"+imageName+".png"));
			
			Sprite sP = new Sprite(texture,texture.getWidth(),texture.getHeight());//xPos,yPos,256,256);
			sP.setPosition(xPos, Gdx.graphics.getHeight()-yPos-175);//-sP.getHeight());
			CharacterSprite cSP = new CharacterSprite(sP,imageName,256,256);//(CharacterSprite) sP;//new CharacterSprite(sP,imageName,texture.getWidth(),texture.getHeight());
			cSP.name = imageName;
			cSP.inputX = xPos;
			cSP.inputY = yPos;
			currentImages.add(cSP);
		}
		else
		{
			Texture texture = new Texture(Gdx.files.internal("characters/"+imageName+".png"));			
			Sprite sP = new Sprite(texture,texture.getWidth(),texture.getHeight());
			sP.setPosition(Gdx.graphics.getWidth()-sP.getWidth()-xPos, yPos);
			CharacterSprite cSP = new CharacterSprite(sP,imageName,256,512);//texture.getWidth(),texture.getHeight());
			cSP.inputX = xPos;
			cSP.inputY = yPos;
			currentImages.add(cSP);
		}
	}
	
	public static void hide(String imageName)
	{
		if(imageName.contains(" ")) //we're hiding one image and replacing it with another
		{
			String imageDelete = imageName.substring(0,imageName.indexOf(" "));
			String imageCreate = imageName.substring(imageName.indexOf(" ")+1); 
			int xPos = -1000;
			int yPos = -1000;
			
			for(int i=0; i<currentImages.size(); i++)
			{
				if(imageDelete.contains(currentImages.get(i).name))
				{
					xPos = currentImages.get(i).inputX;
					yPos = currentImages.get(i).inputY;
					currentImages.remove(i);
					break;
				}
			}
			if(xPos != -1000 && yPos != -1000)
			{
				showImage(imageCreate,xPos,yPos);
			}
		}
		else if(currentImages.size() > 0) //we're just hiding an image
		{
			for(int i=0; i<currentImages.size(); i++)
			{
				if(imageName.contains(currentImages.get(i).name))
				{
					currentImages.remove(i);
					return;
				}
			}
		}
	}
	
	public static void hpunch()
	{
		PUNCH_GOING_UP=0;
		punchCounter=0;
		punchingStartTime = System.currentTimeMillis();
		PUNCHING_HORIZONTAL=true;
		PUNCHING=true;
		Gdx.input.vibrate(750);
	}
	public static void vpunch()
	{
		PUNCH_GOING_UP=0;
		punchCounter=0;
		punchingStartTime = System.currentTimeMillis();
		PUNCHING_HORIZONTAL=false;
		PUNCHING=true;
		Gdx.input.vibrate(750);
	}
	
	private static TextureRegion[][] split (String name, int width, int height)
	{
		return split(name, width, height, false);
	}

	private static TextureRegion[][] split (String name, int width, int height, boolean flipX)
	{
		Texture texture = new Texture(Gdx.files.internal(name));
		int xSlices = texture.getWidth() / width;
		int ySlices = texture.getHeight() / height;
		TextureRegion[][] res = new TextureRegion[xSlices][ySlices];
		for (int x = 0; x < xSlices; x++) {
			for (int y = 0; y < ySlices; y++) {
				res[x][y] = new TextureRegion(texture, x * width, y * height, width, height);
				res[x][y].flip(flipX, true);
			}
		}
		return res;
	}
	
	public static MyTextureRegion load (String name, int width, int height) 
	{
		Texture texture = new Texture(Gdx.files.internal(name));
		MyTextureRegion region = new MyTextureRegion(texture, 0, 0, width, height);
		//region.flip(false, true);
		return region;
	}
	
	public static void dispose()
	{
		currentImages.clear();
	}
	
	
}
