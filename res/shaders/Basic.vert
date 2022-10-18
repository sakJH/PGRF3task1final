#version 330
in vec2 inPosition;

uniform mat4 u_View;
uniform mat4 u_Proj;

out vec2 texCoords;
out vec3 toLightVetor;
out vec3 normalVector;

vec3 lightSource = vec3(0.5,0.5,0.2);

vec3 getNormal(){
    //TODO spravně implementorvatt
    return vec3(0.,0.,1.);
}


void main() {
    texCoords = inPosition;
    // vstup <0;1>
    // chci <-1;1>
    //vec2 pos = inPosition * 2 - 1;
    //float z = 0.5 * cos(sqrt(20 * pow(pos.x, 2) + 20 * pow(pos.y, 2))); //ohnutí

    vec4 objectPosition = u_View * vec4(inPosition, 0.f, 1.f);
    vec3 normal = getNormal();

    //Phong
    vec4 lightPosition = u_View * vec4(lightSource,1.);
    toLightVetor = lightSource.xyz - objectPosition.xyz;
    normalVector = transpose(inverse(mat3(u_View))) * getNormal();


    gl_Position = u_Proj * objectPosition;
}
