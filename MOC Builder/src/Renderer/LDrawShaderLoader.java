package Renderer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;

import LDraw.Support.LDrawUtilities;

//========== load_shader ================================================
//
//Purpose:		Load a single shader object for linking later.
//
//Notes:		shader_prefix is a string pre-inserted into the string.
//				This is how we get VSHADER and FSHADER defined.  
//
//=======================================================================

public class LDrawShaderLoader {
	public static int load_shader(GL2 gl2, String file_path, int shader_type,
			String shader_prefix) {

		int shader_obj = gl2.glCreateShader(shader_type);
		String shader_file_text = LDrawUtilities.stringFromFile(file_path);

		String shader_text[] = { shader_prefix, shader_file_text };
		
		gl2.glShaderSource(shader_obj, 2, shader_text, null);
		gl2.glCompileShader(shader_obj);
		IntBuffer result = IntBuffer.allocate(1);
		gl2.glGetShaderiv(shader_obj, GL2.GL_COMPILE_STATUS, result);
		if (result.get(0) == 0) {
			IntBuffer log_len = IntBuffer.allocate(1);
			gl2.glGetShaderiv(shader_obj, GL2.GL_INFO_LOG_LENGTH, log_len);
			ByteBuffer buf = ByteBuffer.allocate(log_len.get(0));

			gl2.glGetShaderInfoLog(shader_obj, log_len.get(0),
					IntBuffer.allocate(0), buf);
			System.out.println(String.format("Shader %s failed.\n%s\n",
					file_path, new String(buf.array())));
			return 0;
		}
		return shader_obj;
	}// end load_shader

	// =========- LDrawLoadShaderFromFile ====================================
	//
	// Purpose: Load a shader from disk.
	//
	// Notes: Automatically adds prefixes to select GLSL 120 and define
	// FSHADER or VSHADER for the two types of shaders.
	//
	// Automatically binds the attribute list consecutively pre-
	// link.
	//
	// =======================================================================
	public static int LDrawLoadShaderFromFile(GL2 gl2, String file_path,
			String attrib_list[]) {

		int vshader = load_shader(gl2, file_path, GL2.GL_VERTEX_SHADER,
				"#version 120\n#define VSHADER 1\r\n#define FSHADER 0\n");
		 int fshader = load_shader(gl2, file_path, GL2.GL_FRAGMENT_SHADER,
		 "#version 120\n#define VSHADER 0\n#define FSHADER 1\n");
		
		 if (vshader == 0 || fshader == 0)
			return 0;
		int prog = gl2.glCreateProgram();
		gl2.glAttachShader(prog, vshader);
		gl2.glAttachShader(prog, fshader);

		if (attrib_list != null) {
			int a = 0;
			while (a < attrib_list.length) {
				gl2.glBindAttribLocation(prog, a, attrib_list[a]);
				++a;
			}
		}

		gl2.glLinkProgram(prog);

		IntBuffer result = IntBuffer.allocate(1);
		gl2.glGetProgramiv(prog, GL2.GL_LINK_STATUS, result);
		if (result.get(0) == 0) {
			IntBuffer log_len = IntBuffer.allocate(1);
			gl2.glGetProgramiv(prog, GL2.GL_INFO_LOG_LENGTH, log_len);
			ByteBuffer buf = ByteBuffer.allocate(log_len.get());
			gl2.glGetProgramInfoLog(prog, log_len.get(), IntBuffer.allocate(0),
					buf);
			System.out.println(String.format("Shader %s failed.\n%s\n",
					file_path, buf));

			gl2.glDeleteShader(vshader);
			gl2.glDeleteShader(fshader);
			gl2.glDeleteProgram(prog);
			return 0;
		}
		return prog;
	}// end LDrawLoadShaderFromFile

	// todo
	// =========- LDrawLoadShaderFromResource ================================
	//
	// Purpose: Load a shader from a resource.
	//
	// Notes: Finds a shader in our app bundle, which is the preferred
	// way to use our shaders.
	//
	// =======================================================================
	// public int LDrawLoadShaderFromResource(String name, String attrib_list[])
	// {
	// NSBundle * mainBundle = [NSBundle mainBundle];
	// String path = [mainBundle pathForResource:name ofType:nil];
	// return LDrawLoadShaderFromFile(path,attrib_list);
	// }//end LDrawLoadShaderFromResource
}
