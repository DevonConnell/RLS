package com.rls;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class Text {

	public static String[] fileNames = { "after_fight.txt", "did_fight_gales.txt", "did_not_fight_gales.txt", "ending.txt", "fight.txt",
		"first_op_done.txt", "funeral.txt", "not_shooting_anybody.txt", "op1_1.txt", "op1_2.txt", "op1.txt",
		"op2.txt", "planet_branch.txt", "planet_op1.txt", "planet_op1Continuation.txt", "planet_op2.txt",
		"planet_op2Continuation.txt", "pussy_out.txt", "space_branch.txt", "start.txt" };
	public static String[] texts = new String[fileNames.length];
	public static String[] currentTextLines;
	public static int currentTextLineNumber = 0;
	public static String currentDialogue = "";
	
	//public static boolean nextLineTrue = true;
	public static String SPEAKER = "n";
	
	public static String currentLine;
	public static int currentLineMaxNumberCharacters;
	public static int currentLineCharacterNumber;
	public static String currentTextFile;
	
	public static boolean lastLineWasNVL = false;
	public static int lastLinesWasNVLCounter = 0;
	
	public static ArrayList<String> shownLines = new ArrayList<String>();
	
	public static String decisionPrompt;
	public static String[] decisionTexts;
	public static String[] decisionTextFiles;
	
	public static void load() {
		FileHandle file = null;
		
		for(int i = 0; i < fileNames.length; i++)
		{
			file = Gdx.files.internal("text/"+fileNames[i]);
			texts[i] = file.readString();
		}
	}
	
	public static boolean loadFile(String fileName)
	{
		Gdx.app.log("Text.loadFile() ", fileName);
		FileHandle file = null;
		try
		{
			file = Gdx.files.internal("text/"+fileName);
			currentTextFile=fileName;
			
			String text = file.readString();
			currentTextLineNumber = 0;
			currentTextLines = text.split("\\r?\\n");
			
			currentLine = currentTextLines[0];
			currentLineMaxNumberCharacters = currentTextLines[0].length();
			currentLineCharacterNumber = 0;
		}
		catch(Exception ex)
		{
			Gdx.app.log("EXCEPTION:", "could not load file");
			return false;
		}
		
		return true;
	}
	
	public static String getNextLine()
	{
		currentTextLineNumber++;
		return currentTextLines[currentTextLineNumber-1];
	}
	
	public static void parseDialogue()
	{
		
	}
	
	public static boolean parseLine(String line)
	{
		currentDialogue = "";
		if(line.length() > 2 && line.charAt(0) == 'n' && line.charAt(1) == ' ') //Narrator
		{
			SPEAKER = "n";
			currentDialogue = line.substring(2);
			currentLineMaxNumberCharacters = currentDialogue.length();
			lastLineWasNVL = true;
			return true;
		}
		else if(line.length() > 7 && line.contentEquals("nvl clear"))
		{
			shownLines.clear();
			return false;
		}
		else if(Text.currentLine.length() > 9 && Text.currentLine.contentEquals("stop music"))
		{
			Sound.currentBGM.stop();
			return false;
		}
		else if(line.length() > 5 && line.charAt(0) == 's' && line.charAt(1) == 'c' && line.charAt(2) == 'e' && line.charAt(3) == 'n' && line.charAt(4) == 'e')
		{
			String sString = line.substring(6);
			Art.showBG(sString);
			return false;
		}
		else if(line.length() > 5 && line.charAt(0) == 's' && line.charAt(1) == 'h' && line.charAt(2) == 'o' && line.charAt(3) == 'w')
		{
			String sString = line.substring(5);
			String[] nameXY = sString.split(" ");
			
			int x = Integer.parseInt(nameXY[1].substring(2));
			int y = Integer.parseInt(nameXY[2].substring(2));
			
			Art.showImage(nameXY[0],x,y);
			return false;
		}
		/*else if(line.length() > 9 && line.contentEquals("stop music"))
		{
			//handled in main file
			return false;
		}*/
		else if(line.length() >5 && line.charAt(0) == 'p' && line.charAt(1) == 'l' && line.charAt(2) == 'a' && line.charAt(3) == 'y')
		{
			String sString = line.substring(5);
			Sound.playBGM(sString);
			return false;
		}
		else if(line.length() > 5 && line.charAt(0) == 'h' && line.charAt(1) == 'i' && line.charAt(2) == 'd' && line.charAt(3) == 'e')
		{
			String sString = line.substring(5);
			Art.hide(sString);
			return false;
		}
		else if(line.length() > 3 && line.charAt(0) == 's' && line.charAt(1) == 'f' && line.charAt(2) == 'x')
		{
			String sString = line.substring(4);
			Sound.playSFX(sString);
			return false;
		}
		else if(line.length() > 5 && line.contentEquals("hpunch"))
		{
			//hPunch
			Art.hpunch();
			return false;
		}
		else if(line.length() > 5 && line.contentEquals("vpunch"))
		{
			//vPunch
			Art.vpunch();
			return false;
		}
		else if(line.length() > 4 && line.charAt(0) == 'm' && line.charAt(1) == 'e' && line.charAt(2) == 'n' && line.charAt(3) == 'u')
		{
			String sNum = ""+line.charAt(5);
			int num = Integer.parseInt(sNum);
			//Gdx.app.log("::parseLine", "MENU "+num);
			String restOfPrompt = line.substring(7);
			makeMenu(num,restOfPrompt);
			return false;
		}
		else if(line.length() > 2 && line.charAt(0) == (char)39) //'name'
		{
			String sString = line.substring(1);
			int spot = sString.indexOf(39);
			String cName = line.substring(1,spot+1);
			String cTalk = sString.substring(spot+2);
			SPEAKER = cName;
			currentDialogue = cTalk;
			currentLineMaxNumberCharacters = currentDialogue.length();
			shownLines.clear();
			Text.lastLineWasNVL=false;
			return true;
		}
		else if(line.length() > 2 && line.charAt(0) == 'c' && line.charAt(1) == 'R') //Captain Rosseau
		{
			SPEAKER = "Captain Rousseau";
			currentDialogue = line.substring(3);
			currentLineMaxNumberCharacters = currentDialogue.length();
			shownLines.clear();
			Text.lastLineWasNVL=false;
			return true;
		}
		else if(line.length() > 2 && line.charAt(0) == 'c' && line.charAt(1) == '2') //Gales
		{
			SPEAKER = "Gales";
			currentDialogue = line.substring(3);
			currentLineMaxNumberCharacters = currentDialogue.length();
			shownLines.clear();
			Text.lastLineWasNVL=false;
			return true;
		}
		else if(line.length() > 2 && line.charAt(0) == 'S' && line.charAt(1) == 'c') //Sheiletta
		{
			SPEAKER = "Sheiletta";
			currentDialogue = line.substring(3);
			currentLineMaxNumberCharacters = currentDialogue.length();
			shownLines.clear();
			Text.lastLineWasNVL=false;
			return true;
		}
		else if(line.length() > 2 && line.charAt(0) == 'N' && line.charAt(1) == ' ') //Naomi
		{
			SPEAKER = "Naomi";
			currentDialogue = line.substring(2);
			currentLineMaxNumberCharacters = currentDialogue.length();
			shownLines.clear();
			Text.lastLineWasNVL=false;
			return true;
		}
		else if(line.length() > 2 && line.charAt(0) == 'K' && line.charAt(1) == ' ') //Kujero
		{
			SPEAKER = "Kujero";
			currentDialogue = line.substring(2);
			currentLineMaxNumberCharacters = currentDialogue.length();
			shownLines.clear();
			Text.lastLineWasNVL=false;
			return true;
		}
		else if(line.length() > 2 && line.charAt(0) == 't' && line.charAt(1) == ' ') //rousseau thinking
		{
			SPEAKER = "";
			currentDialogue = line.substring(2);
			currentLineMaxNumberCharacters = currentDialogue.length();
			shownLines.clear();
			Text.lastLineWasNVL=false;
			return true;
		}
		else if(line.length() > 2 && line.charAt(0) == 'v' && line.charAt(1) == ' ') //Voice
		{
			SPEAKER = "Voice";
			currentDialogue = line.substring(2);
			currentLineMaxNumberCharacters = currentDialogue.length();
			shownLines.clear();
			Text.lastLineWasNVL=false;
			return true;
		}
		else if(line.length() > 2 && line.charAt(0) == 'c' && line.charAt(1) == ' ') //Captain
		{
			SPEAKER = "Captain";
			currentDialogue = line.substring(2);
			currentLineMaxNumberCharacters = currentDialogue.length();
			shownLines.clear();
			Text.lastLineWasNVL=false;
			return true;
		}
		else if(line.length() > 2 && line.charAt(0) == 'e' && line.charAt(1) == ' ') //Enemy
		{
			SPEAKER = "Enemy";
			currentDialogue = line.substring(2);
			currentLineMaxNumberCharacters = currentDialogue.length();
			shownLines.clear();
			Text.lastLineWasNVL=false;
			return true;
		}
		
		return false;
	}
	
	public static void makeMenu(int num, String prompt)
	{
		decisionPrompt=prompt;
		decisionTexts=new String[num];
		decisionTextFiles=new String[num];
		
		for(int i=0; i<num; i++)
		{
			String next = currentTextLines[currentTextLineNumber+1+i];
			String[] nextSplit = next.split(" ");
			decisionTextFiles[i]=nextSplit[0];
			decisionTexts[i]="";
			for(int j=1; j<nextSplit.length; j++)
			{
				decisionTexts[i]+=nextSplit[j];
				if(j<nextSplit.length-1)
					decisionTexts[i]+=" ";
			}
		}
	}
	
	
	public static void dispose()
    {
		
    }
	
	public static boolean DoWeHaveThisTxt(String imprintTextFile) 
	{
		for(int i=0; i<fileNames.length; i++)
		{
			if(fileNames[i].equalsIgnoreCase(imprintTextFile))
			{
				return true;
			}
		}
		
		return false;
	}
	
}
