package Builder;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;

import Resource.ResourceManager;
import Window.BrickViewer;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class ConnectivityRendererForBrickViewer {
	private BrickViewer brickViewer;
	private GLU glu;
	private Texture mTest;

	public ConnectivityRendererForBrickViewer(BrickViewer brickViewer) {
		this.brickViewer = brickViewer;
		glu = new GLU();
		
		try {
			String path = "/Resource/Image/chain_exclamation.png";
			URL url = ResourceManager.getInstance().getURL(path);
			if (url != null) {
				mTest = TextureIO.newTexture(url, true, null);
			} else {
				mTest = TextureIO.newTexture(new File(
						System.getProperty("user.dir")+ path),	true);
			}
		} catch (GLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void draw(GL2 gl2) {			
		if(brickViewer.visiblePart()==null)return;
		if(brickViewer.visiblePart().isConnectivityInfoExist())return;	
		
		gl2.glDisable(GL2.GL_TEXTURE_GEN_S);
		gl2.glDisable(GL2.GL_TEXTURE_GEN_T);
		
		gl2.glUseProgram(0);
		gl2.glColor3f(1f, 1f, 1f);
		gl2.glEnable(GL2.GL_TEXTURE_2D);
		
		gl2.glMatrixMode(GL2.GL_PROJECTION);		
		gl2.glLoadIdentity();
		glu.gluOrtho2D(0, 2000, 0, 2000);
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();
		
		mTest.enable(gl2);
		mTest.bind(gl2);
		gl2.glBegin(GL2.GL_QUADS);
		gl2.glNormal3f(0, 0, 1);
		gl2.glTexCoord2f(0.0f, 0.0f);
		gl2.glVertex2f(1700, 1700);
		gl2.glTexCoord2f(0, 1);
		gl2.glVertex2f(1700, 2000);
		gl2.glTexCoord2f(1, 1);
		gl2.glVertex2f(2000, 2000);
		gl2.glTexCoord2f(1, 0);
		gl2.glVertex2f(2000, 1700);
		gl2.glEnd();
		gl2.glFlush();
		mTest.disable(gl2);
		
		gl2.glDisable(GL2.GL_TEXTURE_2D);
		gl2.glEnable(GL2.GL_TEXTURE_GEN_S);
		gl2.glEnable(GL2.GL_TEXTURE_GEN_T);
	}
}
