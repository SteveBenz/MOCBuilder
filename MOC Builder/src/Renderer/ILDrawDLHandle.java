package Renderer;

import javax.media.opengl.GL2;

public interface ILDrawDLHandle  {
	public void draw(GL2 gl2, LDrawDLSession session,
			LDrawTextureSpec spec, float cur_color[], float cmp_color[],
			float transform[], boolean draw_now) ;
}
