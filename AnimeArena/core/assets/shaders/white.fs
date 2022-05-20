varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float u_time;

void main()
{
	vec4 color = texture2D(u_texture, v_texCoords);
	gl_FragColor = vec4(color.r, color.g, color.b, u_time);
}