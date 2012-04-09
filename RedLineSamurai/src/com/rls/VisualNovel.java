package com.rls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;

public class VisualNovel {
	public static boolean AT_DECISION = false;
	public static float CHARACTER_WAIT_TIME_ORIGINAL = 15.0f;
	public static float CHARACTER_WAIT_TIME = 15.0f;
	public static boolean READING_LINE = false;
	public static boolean WAITING_FOR_CLICK = false;
	public static boolean SKIPPING = false;
	
	public static boolean WAITING = false;
	public static float WAITING_TIME = 650f;
	public static int dialogueCutOff = 50;
	public static long newCharStartTime = System.currentTimeMillis();
	
	public static float fastForwardWaitTime=2250f;
	public static long blinkCounter = System.currentTimeMillis();
	public static long blinkIncrement = 650;
	
	public static void render() {
		if(AT_DECISION)
			renderDecision();
		else
			renderVN();
	}
	
	public static void renderVN() {
		//Implement the line-skip functionality
		if(RedLineSamurai.pressingTimeTotal > fastForwardWaitTime)
		{
			SKIPPING=true;
			CHARACTER_WAIT_TIME=0f;//3f;
			//Gdx.app.log("skipping","I'm skipping!");
		}
		else
		{
			SKIPPING=false;
			CHARACTER_WAIT_TIME=CHARACTER_WAIT_TIME_ORIGINAL;
			//Gdx.app.log("skipping","I'm no longer skipping!");
		}
		
		if(!READING_LINE && ( (WAITING_FOR_CLICK && !WAITING) || SKIPPING) )
		{
			if(Gdx.input.isKeyPressed(Keys.HOME) || Gdx.input.isKeyPressed(Keys.ESCAPE) || Gdx.input.isKeyPressed(Keys.MENU) || Gdx.input.isKeyPressed(Keys.SEARCH))
			{
				if(!SKIPPING)
				{
					RedLineSamurai.IS_PAUSED=true;
					Title.loadMenuButtons();
					return;
				}
			}
			
			if(Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.DPAD_LEFT) || Gdx.input.isKeyPressed(Keys.DPAD_RIGHT) 
					|| Gdx.input.isKeyPressed(Keys.DPAD_UP) || Gdx.input.isKeyPressed(Keys.DPAD_DOWN) || Gdx.input.isKeyPressed(Keys.ENTER))
			{
				//Gdx.app.log("render()", "User wants next line!");
				goToNextLine();
				blinkCounter=RedLineSamurai.currentTime;
			}
		}
		
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);	
		
		RedLineSamurai.batch.begin();
		if(Art.PUNCHING)
		{
			if(Art.PUNCH_GOING_UP==0)
			{
				Art.punchCounter+=2;
				if(Art.punchingStartTime+200.0 <= RedLineSamurai.currentTime) //Art.punchCounter >= Art.maxPunchMove)
					Art.PUNCH_GOING_UP=1;
			}
			else if(Art.PUNCH_GOING_UP==1)
			{
				Art.punchCounter-=2;
				if(Art.punchingStartTime+600.0 <= RedLineSamurai.currentTime)//Art.punchCounter <= -Art.maxPunchMove)
					Art.PUNCH_GOING_UP=2;
			}
			else if(Art.PUNCH_GOING_UP==2)
			{
				Art.punchCounter+=2;
				if(Art.punchingStartTime+800.0 <= RedLineSamurai.currentTime)//Art.punchCounter >= 0)
				{
					Art.PUNCHING=false;
					Art.punchCounter=0;
				}
			}
				
			if(Art.PUNCHING_HORIZONTAL)
				RedLineSamurai.batch.draw(Art.currentBG,Art.punchCounter,0);
			else
				RedLineSamurai.batch.draw(Art.currentBG,0,Art.punchCounter);
		}
		else
			RedLineSamurai.batch.draw(Art.currentBG,0,0);
		
		
		for(int i = 0; i < Art.currentImages.size(); i++)
		{
			Art.currentImages.get(i).draw(RedLineSamurai.batch);
		}
		
		if(Text.lastLineWasNVL)
			RedLineSamurai.batch.draw(Art.nvlOverlay,0,-425);
		else
			RedLineSamurai.batch.draw(Art.advOverlay,0,-(RedLineSamurai.ADV_Y)/2f);
		
		if(WAITING && !Art.PUNCHING)
		{
			if(WAITING_TIME < RedLineSamurai.currentTime - newCharStartTime)//System.currentTimeMillis() - newCharStartTime)
			{
				WAITING=false;
			}
			showLine();
		}
		else
		{
			displayLine();
		}
		
		RedLineSamurai.batch.end();
	}

	public static void displayLine()
	{
		if(READING_LINE && !AT_DECISION)
		{	
			if(Text.currentLineCharacterNumber < Text.currentLineMaxNumberCharacters)
			{
				if(Text.currentLineCharacterNumber == 0)
				{
					Gdx.app.log("displayLine() checking new line:", Text.currentLine);
					if(Text.parseLine(Text.currentLine)) //somebody is talking
					{
						if(!Text.lastLineWasNVL)
						{
							Text.shownLines.clear();//lineReadout = "";
						}
						
						if(Text.currentDialogue.charAt(Text.currentLineCharacterNumber) == '\\')
						{
							Text.shownLines.add("");
							Text.currentLineCharacterNumber += 2;
						}
						else if(Text.currentDialogue.charAt(Text.currentLineCharacterNumber) == (char)39)
						{
							Text.shownLines.add(""+(char)39);
							Text.currentLineCharacterNumber++;
							newCharStartTime = RedLineSamurai.currentTime;//System.currentTimeMillis();
							showLine();
						}
						else
						{
							Text.shownLines.add(""+Text.currentDialogue.charAt(Text.currentLineCharacterNumber));
							//lineReadout = lineReadout+Text.currentDialogue.charAt(Text.currentLineCharacterNumber);
							Text.currentLineCharacterNumber++;
							newCharStartTime = RedLineSamurai.currentTime;//System.currentTimeMillis();
							showLine();
						}
					}
					else //something else is happening
					{
						//if menu, don't do this
						if(Text.currentLine.length() >5 && Text.currentLine.charAt(0) == 'm' && Text.currentLine.charAt(1) == 'e' 
								&& Text.currentLine.charAt(2) == 'n' && Text.currentLine.charAt(3) == 'u' && Text.currentLine.charAt(4) == ' ')///Make a decision menu
						{
							AT_DECISION=true;
							return;
						}
						else if(Text.currentLine.length() > 5 && Text.currentLine.contentEquals("TheEnd"))
						{
							RedLineSamurai.goBackToTitle();
							return;
						}
						else if(Text.currentLine.length() > 3 && Text.currentLine.charAt(0) == 'i' && Text.currentLine.charAt(1) == 'f' 
								&& Text.currentLine.charAt(2) == ' ')//Text.currentLine.contains("if ")) // IF
						{
							String sString = Text.currentLine.substring(3);
							String[] sSplit = sString.split(" ");
							if(Record.didThis(sSplit[0]))//+".txt"))
							{
								//Gdx.app.log("SstringBEFORE:",sString);
								String afterIfFile = sString.substring(sSplit[0].length()+1);
								//sString.replace(sSplit[0]+" ", "");
								//Gdx.app.log("SstringAFTER:",afterIfFile);
								if(Text.currentDialogue.length() == 0 && Text.parseLine(afterIfFile)) //dialogue, assuming it's not a 'Name'
								{
									Text.shownLines.add(""+Text.currentDialogue.charAt(Text.currentLineCharacterNumber));
									//lineReadout = lineReadout+Text.currentDialogue.charAt(Text.currentLineCharacterNumber);
									Text.currentLineCharacterNumber++;
									newCharStartTime = RedLineSamurai.currentTime;//System.currentTimeMillis();
									showLine();
								}
								else
								{
									if(sString.length() > 5 && afterIfFile.contains("jump "))
									{
										String jumpFile = afterIfFile.substring(5);//sString.substring(5);
										loadNextFile(jumpFile);
										return;
									}
								}
							}
							else
							{
								Text.currentTextLineNumber++;
								Text.currentLine = Text.currentTextLines[Text.currentTextLineNumber];
							}

						}
						else if(Text.currentLine.length() > 5 && Text.currentLine.charAt(0) == 'j' && Text.currentLine.charAt(1) == 'u' 
								&& Text.currentLine.charAt(2) == 'm' && Text.currentLine.charAt(3) == 'p' && Text.currentLine.charAt(4) == ' ')// && !Text.currentLine.contains("if "))
						{
							String jumpFile = Text.currentLine.substring(5);
							loadNextFile(jumpFile);
							return;
						}
						else
						{
							Text.currentTextLineNumber++;
							Text.currentLine = Text.currentTextLines[Text.currentTextLineNumber];
						}
					}
					//lineReadout = "";
					//lineReadout = lineReadout+Text.currentLine.charAt(Text.currentLineCharacterNumber);
				}
				else //not at the start of a line
				{
					if(SKIPPING)
					{
						String sLine = Text.shownLines.get(Text.shownLines.size()-1);
						String addition = Text.currentDialogue.substring(Text.currentLineCharacterNumber, Text.currentDialogue.length());
						boolean foundW=true;
						while(foundW)
						{
							for(int i=0; i<addition.length(); i++)
							{
								if(addition.charAt(i) == '{')
								{
									String newAddition = addition.substring(0,i);
									newAddition += addition.substring(i+5);
									addition = newAddition;
									foundW=true;
									break;
								}
								else if(addition.charAt(i) == '\\')
								{
									String newAddition = addition.substring(0,i);
									newAddition += addition.substring(i+2);
									addition = newAddition;
									foundW=true;
									break;
								}
								foundW=false;
							}
						}
						//addition.c
						sLine += addition;
						Text.shownLines.remove(Text.shownLines.size()-1);
						Text.shownLines.add(sLine);
						
						Text.currentLineCharacterNumber = Text.currentLineMaxNumberCharacters;//Text.currentLineCharacterNumber++;
						newCharStartTime = RedLineSamurai.currentTime;
					}
					
					else if(CHARACTER_WAIT_TIME < RedLineSamurai.currentTime-newCharStartTime)//System.currentTimeMillis()-newCharStartTime)
					{
						if(Text.currentDialogue.charAt(Text.currentLineCharacterNumber) == '{')
						{
							WAITING_TIME = 650f * Integer.parseInt(""+Text.currentDialogue.charAt(Text.currentLineCharacterNumber+3));
							if(!SKIPPING)
								WAITING = true;
							Text.currentLineCharacterNumber+=5;
							newCharStartTime = RedLineSamurai.currentTime;//System.currentTimeMillis();
						}
						else if(Text.currentDialogue.charAt(Text.currentLineCharacterNumber) == '\\')
						{
							/*String sLine = Text.shownLines.get(Text.shownLines.size()-1);
							sLine += '\n';
							Text.shownLines.remove(Text.shownLines.size()-1);
							Text.shownLines.add(sLine);*/
							//Text.shownLines.add(""+'\n');//lineReadout = lineReadout+ '\r';//'\n';
							Text.currentLineCharacterNumber += 2;
						}
						else if(Text.currentDialogue.charAt(Text.currentLineCharacterNumber) == '\'')//(char)39)
						{
							String sLine = Text.shownLines.get(Text.shownLines.size()-1);
							sLine += '\''; //(char)39; //Text.currentDialogue.charAt(Text.currentLineCharacterNumber);
							//Text.shownLines.get(Text.shownLines.size()-1).concat(""+(char)39);
							Text.shownLines.remove(Text.shownLines.size()-1);
							Text.shownLines.add(sLine);
							Text.currentLineCharacterNumber++;
							newCharStartTime = RedLineSamurai.currentTime;//System.currentTimeMillis();
						}
						else
						{
							String sLine = Text.shownLines.get(Text.shownLines.size()-1);
							sLine += Text.currentDialogue.charAt(Text.currentLineCharacterNumber);
							Text.shownLines.remove(Text.shownLines.size()-1);
							Text.shownLines.add(sLine);
							//lineReadout = lineReadout+Text.currentDialogue.charAt(Text.currentLineCharacterNumber);
							Text.currentLineCharacterNumber++;
							newCharStartTime = RedLineSamurai.currentTime;//System.currentTimeMillis();
						}
					}
					showLine();
				}
				
				WAITING_FOR_CLICK = false;
			}
			else //dialog is done outputting, wait for player
			{
				showLine();
				READING_LINE = false;
				WAITING_FOR_CLICK = true;
			}
		}
		else if(!READING_LINE && WAITING_FOR_CLICK)
		{
			showLine();
		}
	}

	public static void showLine()
	{
		//if(SKIPPING)
		//	return;
		checkIfLineIsTooLong();
		if(Text.SPEAKER == "n")
		{
			float yInc = Gdx.graphics.getHeight()/20.0f;
			for(int i=0; i< Text.shownLines.size(); i++)
			{
				if(i == Text.shownLines.size()-1 && WAITING_FOR_CLICK)
				{
					if(RedLineSamurai.currentTime<blinkCounter+blinkIncrement)
						Font.bFont.draw(RedLineSamurai.batch, Text.shownLines.get(i)+" >", RedLineSamurai.NVL_X, RedLineSamurai.NVL_Y - i*yInc);
					else
						Font.bFont.draw(RedLineSamurai.batch, Text.shownLines.get(i), RedLineSamurai.NVL_X, RedLineSamurai.NVL_Y - i*yInc);
					//blinkCounter++;
					if(RedLineSamurai.currentTime-blinkCounter >= blinkIncrement*2)
						blinkCounter=RedLineSamurai.currentTime;
				}
				else
					Font.bFont.draw(RedLineSamurai.batch, Text.shownLines.get(i), RedLineSamurai.NVL_X, RedLineSamurai.NVL_Y - i*yInc);
				//bFont.
			}
		}
		else
		{
			float yInc = Gdx.graphics.getHeight()/20.0f;
			if(Text.SPEAKER != "")
				Font.bFontBold.draw(RedLineSamurai.batch,Text.SPEAKER+": ",RedLineSamurai.ADV_X, RedLineSamurai.ADV_Y);
			for(int i=0; i< Text.shownLines.size(); i++)
			{
				if(i == Text.shownLines.size()-1 && WAITING_FOR_CLICK)
				{
					if(RedLineSamurai.currentTime<blinkCounter+blinkIncrement)
						Font.bFont.draw(RedLineSamurai.batch, Text.shownLines.get(i)+" >", RedLineSamurai.ADV_X, RedLineSamurai.ADV_Y - (i+1)*yInc);
					else
						Font.bFont.draw(RedLineSamurai.batch, Text.shownLines.get(i), RedLineSamurai.ADV_X, RedLineSamurai.ADV_Y - (i+1)*yInc);
					//blinkCounter++;
					if(RedLineSamurai.currentTime-blinkCounter >= blinkIncrement*2)
						blinkCounter=RedLineSamurai.currentTime;
				}
				else
					Font.bFont.draw(RedLineSamurai.batch, Text.shownLines.get(i), RedLineSamurai.ADV_X, RedLineSamurai.ADV_Y - (i+1)*yInc);
			}
			//bFont.draw(batch, Text.SPEAKER+": "+Text.shownLines.get(0), ADV_X, ADV_Y);
		}
		//}
		//Gdx.app.log(""+newCharStartTime, CHARACTER_WAIT_TIME+" "+(System.currentTimeMillis()-newCharStartTime));
		
	}
	
	public static void checkIfLineIsTooLong()
	{
		String theLine = Text.shownLines.get(Text.shownLines.size()-1); 
		if(theLine.length() > dialogueCutOff)
		{
			int spot = dialogueCutOff-1;
			boolean moreThenAWord = true;
			while(theLine.charAt(spot)!=' ')
			{
				spot++;
				if(spot >= theLine.length())
				{
					moreThenAWord = false;
					break;
				}
			}
			if(moreThenAWord) //&& theLine.substring(spot).length() > 4)
			{
				String firstBit = theLine.substring(0,spot);
				String restOfText = theLine.substring(spot);
				Text.shownLines.remove(Text.shownLines.size()-1);
				Text.shownLines.add(firstBit);
				Text.shownLines.add(restOfText);
			}	
		}
		
	}

	public static void goToNextLine()
	{
		if(Text.currentTextLineNumber < Text.currentTextLines.length)
		{
			if(!Text.lastLineWasNVL)
				Text.shownLines.clear();//lineReadout = "";
			Text.currentTextLineNumber++;
			Text.currentLine = Text.currentTextLines[Text.currentTextLineNumber];
			Text.currentLineCharacterNumber = 0;
			Text.currentLineMaxNumberCharacters = Text.currentLine.length();	
		}
		else
		{
			
		}
		WAITING_FOR_CLICK=false;
		READING_LINE=true;
		
	}
	
	
	
	public static void loadNextFile(String fileName)
	{
		Gdx.app.log("RLS", "loadNextFile() "+fileName);
		if(Text.loadFile(fileName))
		{
			READING_LINE=true;
			WAITING_FOR_CLICK=false;
			Text.currentTextFile = fileName;
			Record.imprint();
		}
		else
		{
			Gdx.app.log("RLS", "startGame() couldn't load "+fileName);//op1.txt");
		}
	}
	
	public static void renderDecision()
	{
		if(Gdx.input.isKeyPressed(Keys.HOME) || Gdx.input.isKeyPressed(Keys.ESCAPE) || Gdx.input.isKeyPressed(Keys.SEARCH) || Gdx.input.isKeyPressed(Keys.MENU))
		{
			if(!SKIPPING)
			{
				if(Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.DPAD_LEFT) || Gdx.input.isKeyPressed(Keys.DPAD_RIGHT) 
						|| Gdx.input.isKeyPressed(Keys.DPAD_UP) || Gdx.input.isKeyPressed(Keys.DPAD_DOWN) || Gdx.input.isKeyPressed(Keys.ENTER) || Gdx.input.isKeyPressed(Keys.BACK))
				{
				
				}
				else
				{
					RedLineSamurai.IS_PAUSED=true;
					Title.loadMenuButtons();
					return;
				}
			}
			else
			{
				SKIPPING=false;
				CHARACTER_WAIT_TIME=CHARACTER_WAIT_TIME_ORIGINAL;
			}
		}
		
		int x = Gdx.input.getX();
		int y = Gdx.input.getY();
		
		if(Gdx.input.justTouched())
		{
			float yInc = Gdx.graphics.getHeight()/8.0f;
			for(int i=0; i<Text.decisionTexts.length; i++)
			{
				if(y<Gdx.graphics.getHeight()-(RedLineSamurai.NVL_Y-i*yInc-10) && y>Gdx.graphics.getHeight()-(RedLineSamurai.NVL_Y-i*yInc+10))
				{
					//y < loadButton.y && y > loadButton.y-loadButton.getHeight()
					Gdx.app.log("renderDecision", Text.decisionTextFiles[i]);
					loadNextFile(Text.decisionTextFiles[i]);
					AT_DECISION=false;
					return;
				}
			}
		}
		
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		RedLineSamurai.batch.begin();
		//title.draw();
		RedLineSamurai.batch.draw(Art.currentBG,0,0);
		RedLineSamurai.batch.draw(Art.advOverlay,0,-(RedLineSamurai.ADV_Y)/2f);
		
		if(Text.decisionPrompt.length() > dialogueCutOff)
		{
			int spot = dialogueCutOff-1;
			boolean moreThenAWord = true;
			while(Text.decisionPrompt.charAt(spot)!=' ')
			{
				spot++;
				if(spot >= Text.decisionPrompt.length())
				{
					moreThenAWord = false;
					break;
				}
			}
			if(moreThenAWord) //&& theLine.substring(spot).length() > 4)
			{
				float yInc = Gdx.graphics.getHeight()/20.0f;
				String firstBit = Text.decisionPrompt.substring(0,spot);
				String restOfText = Text.decisionPrompt.substring(spot);
				Font.bFont.draw(RedLineSamurai.batch, firstBit, RedLineSamurai.ADV_X, RedLineSamurai.ADV_Y);
				Font.bFont.draw(RedLineSamurai.batch, restOfText, RedLineSamurai.ADV_X, RedLineSamurai.ADV_Y - yInc);
			}
		}
		Font.bFont.draw(RedLineSamurai.batch, Text.decisionPrompt, RedLineSamurai.ADV_X, RedLineSamurai.ADV_Y);
		
		float yInc = Gdx.graphics.getHeight()/8.0f;
		for(int i=0; i<Text.decisionTexts.length; i++)
		{
			if(y<Gdx.graphics.getHeight()-(RedLineSamurai.NVL_Y-i*yInc-10) && y>Gdx.graphics.getHeight()-(RedLineSamurai.NVL_Y-i*yInc+10))         //might need to do this for title buttons as well?
			{
				Font.bFontBold.draw(RedLineSamurai.batch, Text.decisionTexts[i]+" >>>", RedLineSamurai.NVL_X, RedLineSamurai.NVL_Y - i*yInc);
			}
			else
				Font.bFontBold.draw(RedLineSamurai.batch, Text.decisionTexts[i], RedLineSamurai.NVL_X, RedLineSamurai.NVL_Y - i*yInc);
		}
		RedLineSamurai.batch.end();
	}
	
	
	
}
