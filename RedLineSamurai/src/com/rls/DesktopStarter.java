package com.rls;

import com.badlogic.gdx.backends.jogl.JoglApplication;

public class DesktopStarter {
	public static void main(String[] args) {
		//new JoglApplication(new RedLineSamurai(),"Red Line Samurai",480,320,false);
		new JoglApplication(new RedLineSamurai(), "Azorus IV", 320, 480, false);
	}
}
