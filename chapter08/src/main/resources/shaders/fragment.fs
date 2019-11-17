#version 330

in vec2 outTexCoord;
in vec3 surfaceNormal;
in vec3 toLightVector;
in vec3 toCameraVector;

out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform vec3 lightColor;
uniform float shineDamper;
uniform float reflectivity;

void main()
{
    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitLightVector = normalize(toLightVector);

    float nDotl = dot(unitNormal,unitLightVector);
    float brightness = max(nDotl,0.0);
    vec3 diffuse = brightness * lightColor;

    vec3 unitVectorToCamera = normalize(toCameraVector);
    vec3 lightDirection = -unitLightVector;
    vec3 reflectedLightDirection = reflect(lightDirection,unitNormal);

    float specularFactor = dot(reflectedLightDirection,unitVectorToCamera);
    specularFactor = max(specularFactor,0.0);
    float dampedFactor = pow(specularFactor,shineDamper);
    vec3 finalSpecular = dampedFactor * reflectivity * lightColor;

    fragColor = vec4(diffuse,1.0) * texture(texture_sampler, outTexCoord) + vec4(finalSpecular,1.0);
}