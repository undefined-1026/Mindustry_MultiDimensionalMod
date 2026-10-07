package mDimension.core;

import arc.Core;
import arc.graphics.Texture;
import arc.util.Time;
import mindustry.graphics.Shaders;

import static mindustry.Vars.renderer;

public class InvertColorShader extends Shaders.SurfaceShader {
    public Texture teakSample;
    public InvertColorShader(String frag) {
        super(frag);
    }

    @Override
    public void apply() {
        setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
        setUniformf("u_resolution", Core.camera.width, Core.camera.height);
        setUniformf("u_time", Time.time);

        if(hasUniform("u_teakSample")){
            if(teakSample!=null){
                teakSample.bind(1);
            }
            renderer.effectBuffer.getTexture().bind(0);

            setUniformi("u_teakSample", 1);
        }
    }
}
