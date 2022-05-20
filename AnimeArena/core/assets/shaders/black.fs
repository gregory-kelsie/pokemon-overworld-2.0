varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float u_time;

void main()
{
	vec4 color = texture2D(u_texture, v_texCoords);
	vec4 grayscale = vec4(0, 0, 0, color.a);
	gl_FragColor = grayscale;
}