package com.rls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class CharacterSprite extends Sprite 
{
	public String name;
	public int inputX=0;
	public int inputY=0;
	public CharacterSprite()
	{
	
	}
	public CharacterSprite(Sprite sp, String _name, int w, int h)
	{
		//this.setPosition(sp.getX(),sp.getY());
		//this.setBounds(sp.getX(), sp.getY(), w, h);
		//this.setTexture(sp.getTexture());
		this.set(sp);
		name = _name;
	}
}
