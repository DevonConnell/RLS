package com.rls;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class Record {
	public static boolean SAVED_GAME = false;
	
	public static String imprintBG;
	public static ArrayList<String> imprintImages;
	public static ArrayList<String> imprintImageCoords;
	public static String imprintBGM;
	public static ArrayList<String> imprintChoices;
	public static String imprintTextFile;
	public static int imprintTextFileLine = 0;
	
	public static void loadClass() {
		imprintChoices = new ArrayList<String>();
		imprintImages = new ArrayList<String>();
		imprintImageCoords = new ArrayList<String>();
	}
	
	public static boolean load() {
		FileHandle file = null;
		try
		{
			imprintChoices.clear();
			imprintImages.clear();
			imprintImageCoords.clear();
			
			file = Gdx.files.external("userProgressRLS.dat");
			String dataString = file.readString();
			String[] dataStringSplit = dataString.split(" ");
			Gdx.app.log("LOADING FROM FILE:", dataString);
			
			imprintBG = dataStringSplit[0];
			int numPics = Integer.parseInt(dataStringSplit[1]);
			for(int i=0; i<numPics; i++)
			{
				imprintImages.add(dataStringSplit[2+i]);
			}
			ArrayList<String> xYStrings = new ArrayList<String>();
			for(int i=0; i<numPics; i++)
			{
				String xAndY = dataStringSplit[2+numPics+i];
				imprintImageCoords.add(xAndY);
			}
			imprintBGM = dataStringSplit[2+numPics*2];
			
			int numChoices = Integer.parseInt(dataStringSplit[2+numPics*2+1]);
			for(int i=0; i<numChoices; i++)
			{
				imprintChoices.add(dataStringSplit[2+numPics*2+1+i+1]);
			}
			imprintTextFile = dataStringSplit[2+numPics*2+1+numChoices+1];
			imprintTextFileLine = Integer.parseInt(dataStringSplit[2+numPics*2+1+numChoices+2]);
		}
		catch(Exception ex)
		{
			return false;
		}
		
		return true;
	}
	
	public static void imprint(String bg, String images, String imagesCoords, String bgm, String choices, String textFile)
	{
		imprintBG = Art.currentBG.name;
		imprintImages = new ArrayList<String>();//new String[Art.currentImages.size()];
		imprintImageCoords = new ArrayList<String>();//[Art.currentImages.size()];
		for(int i=0; i<Art.currentImages.size(); i++)
		{
			imprintImages.add(Art.currentImages.get(i).name);
			imprintImageCoords.add(""+Art.currentImages.get(i).getX()+","+Art.currentImages.get(i).getY());
		}
		imprintTextFile = Text.currentTextFile;
		imprintChoices.add(Text.currentTextFile);
	}
	
	public static String convertByteArrayToString(byte[] myBytes)
	{
		String myString = new String((byte[]) myBytes);
		return myString;
	}
	
	public static byte[] convertStringToByteArray(String myString)
	{
		return myString.getBytes();
	}
	
	public static boolean save(String bg, Vector<CharacterSprite> pics, String bgm, int textFileNum)
	{ 
		FileHandle file = Gdx.files.external("userProgressRLS.dat");
		OutputStream out = null;
		try
		{
			out = file.write(false);
			
			out.write(convertStringToByteArray(bg+" "));
			out.write(convertStringToByteArray(pics.size()+" "));
			for(int i=0; i<pics.size(); i++)
			{
				out.write(convertStringToByteArray(pics.get(i).name+" "));
			}
			for(int i=0; i<pics.size(); i++)
			{
				int theX = (int)pics.get(i).inputX;				
				int theY = (int)pics.get(i).inputY;
				out.write(convertStringToByteArray(theX+","+theY+" "));
			}
			out.write(convertStringToByteArray(bgm+" ")); //bgm+" ");
			out.write(convertStringToByteArray(imprintChoices.size()+" "));
			for(int i=0; i<imprintChoices.size(); i++)
			{
				out.write(convertStringToByteArray(imprintChoices.get(i)+" "));
			}
			out.write(convertStringToByteArray(imprintTextFile+" "));
			out.write(convertStringToByteArray(textFileNum+""));
			
			out.close();
		} 
		catch(Exception ex)
		{
			return false;
		} 
		
		return true;
	}
	
	public static boolean didThis(String fileName)
	{
		//Gdx.app.log("DID_THIS", fileName+"..."+imprintChoices.get(0));
		if(imprintChoices == null)
			return false;
		if(imprintChoices.size() == 0)
			return false;
		for(int i=0; i < imprintChoices.size(); i++)
		{
			if(fileName.equalsIgnoreCase(imprintChoices.get(i)))
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean loadFromSave()
	{
		if(load())
		{
			//get images
			Art.currentImages.clear();
			if(imprintImageCoords.size() > 0)
			{
				for(int i=0; i<imprintImageCoords.size(); i++)
				{
					String[] xAndYSplit = imprintImageCoords.get(i).split(",");
					Art.showImage(imprintImages.get(i), Integer.parseInt(xAndYSplit[0]), Integer.parseInt(xAndYSplit[1]));
				}
			}
			Art.showBG(imprintBG);
			//sound
			if(imprintBGM!="")
				Sound.playBGM(imprintBGM);
			//text
			VisualNovel.loadNextFile(imprintTextFile);
			Text.currentTextLineNumber = imprintTextFileLine;
			Text.currentLine = Text.currentTextLines[Text.currentTextLineNumber];
			Text.currentLineMaxNumberCharacters = Text.currentTextLines[Text.currentTextLineNumber].length();
			Text.currentLineCharacterNumber = 0;
			
			Text.shownLines.clear();
			return true;
		}
		else
		{
			Gdx.app.log("loadFromSave", "COULD NOT LOAD .DAT");
		}
		
		return false;
	}
	
	public static void saveGame()
	{
		if(save(Art.currentBG.name,Art.currentImages,Sound.currentBGMName,Text.currentTextLineNumber))
		{
			SAVED_GAME=true;
			//String bg, Vector<CharacterSprite> pics, String bgm, String choices, String textFile, int textFileNum
		}
		else
		{
			Gdx.app.log("saveGame", "COULD NOT SAVE");
		}
	}
	
	public static void imprint()
	{
		imprintBG = Art.currentBG.name;
		imprintImages = new ArrayList<String>();//new String[Art.currentImages.size()];
		imprintImageCoords = new ArrayList<String>();//[Art.currentImages.size()];
		for(int i=0; i<Art.currentImages.size(); i++)
		{
			imprintImages.add(Art.currentImages.get(i).name);
			imprintImageCoords.add(""+Art.currentImages.get(i).getX()+","+Art.currentImages.get(i).getY());
		}
		imprintTextFile = Text.currentTextFile;
		imprintChoices.add(Text.currentTextFile);
		//Record.imprintBGM = Sound.currentBGM.
	}
}
