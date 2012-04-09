package com.rls;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

public class MyButton extends Texture
{
	public int x=0;
	public int y=0;
	public MyButton(FileHandle file) 
	{
		super(file);
		// TODO Auto-generated constructor stub
	}
	public void setXY(int xx, int yy)
	{
		x=xx;
		y=yy;
	}
	/*public int getX()
	{
		return x;
	}
	public int getY()
	{
		return y;
	}*/
	
}

