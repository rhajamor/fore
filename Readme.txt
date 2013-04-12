
FORE : Free OpenGL ES Rendering Engine

	// @See vectMat
	// @see
	// http://http.developer.nvidia.com/CgTutorial/cg_tutorial_chapter05.html
	// @see http://glprogramming.com/red/chapter05.html


for the data object here is the trick : 
always use ByteBuffer to create the dataBuffer (size = data * numbytes)
and get it with the method getDataBuffer with 
the desired BT; e.g when you get it as a float_buffer and modify it the original byte_buffer will modified too
since the buffers share the same data pointer. 