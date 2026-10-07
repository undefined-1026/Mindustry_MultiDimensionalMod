package mDimension.core;

import arc.graphics.gl.Shader;
import mindustry.graphics.Shaders;

public class MDShaders {
    public static Shader well;
    public static InvertColorShader invert;
    public static HoleShader hole;
    public static void init(){
        well = new Shaders.SurfaceShader("well");
        invert = new InvertColorShader("invert");
        hole = new HoleShader("gravitation-lens");
    }
}
