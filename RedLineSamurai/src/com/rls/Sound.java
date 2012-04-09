package com.rls;

import com.badlogic.gdx.Gdx;

public class Sound {

	public static float MUSIC_VOLUME = 0.7f;
	public static float SFX_VOLUME = 0.7f;
	
	public static com.badlogic.gdx.audio.Music introBam = null;
    public static com.badlogic.gdx.audio.Music currentBGM = null;
    public static String currentBGMName = "Babylon";
	
	public static void load() {
		introBam = Gdx.audio.newMusic(Gdx.files.internal("sound/introBam.ogg"));
	}
	
	public static void playSFX(String sfxName)
    {
    	if(introBam != null)
    		introBam.dispose();
    	introBam = Gdx.audio.newMusic(Gdx.files.internal("sound/"+sfxName+".ogg"));
    	introBam.play();
    }
	
	public static void playBGM(String bgmName)
    {	
    	//dispose of last one
    	if(currentBGM != null)
    		currentBGM.dispose();
    	currentBGM = Gdx.audio.newMusic(Gdx.files.internal("sound/"+bgmName+".ogg"));
    	currentBGMName = bgmName;
    	currentBGM.setLooping(true);
    	currentBGM.setVolume(MUSIC_VOLUME);
    	currentBGM.play();
    }
	
	 public static void dispose()
	    {
	    	if(introBam != null)
	    		introBam.dispose();
	    	if(currentBGM != null)
	    		currentBGM.dispose();
	    }
	
}
