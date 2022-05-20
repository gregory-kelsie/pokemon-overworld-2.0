varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float u_time;

void main()
{
	vec4 color = texture2D(u_texture, v_texCoords);
	float alph = 0.0;
	if (color.r >= u_time) {
		alph = 1.0;
	}
	gl_FragColor = vec4(0, 0, 0, alph);
}