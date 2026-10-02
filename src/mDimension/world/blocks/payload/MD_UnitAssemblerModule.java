package mDimension.world.blocks.payload;

import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.world.Block;
import mindustry.world.blocks.units.UnitAssembler;
import mindustry.world.blocks.units.UnitAssemblerModule;
import mindustry.world.meta.BlockFlag;

import static mindustry.Vars.tilesize;

public class MD_UnitAssemblerModule extends UnitAssemblerModule {
    public Seq<Block> fitAssembler = new Seq<>();
    public MD_UnitAssemblerModule(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        for(var b:fitAssembler){
            if(b instanceof MD_UnitAssembler mda){
                mda.fitModules.addUnique(this);
            }
        }
    }

    public @Nullable UnitAssembler.UnitAssemblerBuild getLink(Team team, int x, int y, int rotation){
        var results = Vars.indexer.getFlagged(team, BlockFlag.unitAssembler).<UnitAssembler.UnitAssemblerBuild>as();

        return results.find(b -> b instanceof MD_UnitAssembler.MD_UnitAssemblerBuild ass && ass.block instanceof MD_UnitAssembler type && type.fitModules.contains(this) && b.moduleFits(this, x * tilesize + offset, y * tilesize + offset, rotation));
    }
}
