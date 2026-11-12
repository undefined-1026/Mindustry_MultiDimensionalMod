package mDimension.world.type;
/**beam data class*/
public class BeamStack {
    public int length;
    public int wavelengthLevel;
    public float power = 10f;
    public Beam beam;
    public BeamStack(Beam l){
        this.length = l.length;
        this.wavelengthLevel = l.energyLevel;
        this.beam = l;
    }
    public BeamStack(Beam l, float power){
        this.length = l.length;
        this.wavelengthLevel = l.energyLevel;
        this.power = power;
        this.beam = l;
    }

    public void setPower(float power){
        this.power = power;
    }
}
