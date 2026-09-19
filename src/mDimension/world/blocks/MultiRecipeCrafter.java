package mDimension.world.blocks;


import arc.math.*;
import arc.util.*;
import mDimension.consumers.MultiRecipeConsume;
import mDimension.meta.MD_Stat;
import mDimension.meta.MD_StatValues;
import mindustry.type.*;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

public class MultiRecipeCrafter extends GenericCrafter {
    /**you should use
    *consumeRecipes(new MultiRecipeConsume(MultiRecipeConsume.Recipe...))
    */
    private MultiRecipeConsume recipes;
    public MultiRecipeCrafter(String name) {
        super(name);
        outputItems = ItemStack.with();//防止空指针
        outputLiquids = LiquidStack.with();
    }

    public void consumeRecipes(MultiRecipeConsume recipes){
        consume(recipes);
        this.recipes = recipes;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void setBars() {
        super.setBars();
        Liquid[] liquids = recipes.outLiquidType();
        Liquid[] liquids2 = recipes.inputLiquidType();
        //set up liquid bars for liquid outputs
        if(liquids.length>0){
            //no need for dynamic liquid bar
            removeBar("liquid");
            for(var liquid : liquids2){
                addLiquidBar(liquid);
            }
            //then display output buffer
            for(var liquid : liquids){
                addLiquidBar(liquid);
            }
        }
    }

    @Override
    public boolean outputsItems() {
        return recipes.outItemType().length>0;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.input);
        stats.remove(Stat.output);
        stats.remove(Stat.productionTime);
        if((hasItems && itemCapacity > 0) || recipes != null){
            stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds);
        }

        if(recipes != null){
            stats.add(MD_Stat.recipes,
                    table -> {
                table.row();
                for(MultiRecipeConsume.Recipe recipe:recipes.consumeRecipe) {
                    if(recipe.consumeItems!=null){
                        for (ItemStack stack : recipe.consumeItems) {
                            table.add(StatValues.displayItem(stack.item, stack.amount, craftTime, false)).padRight(2);
                        }
                    }
                    if(recipe.consumeLiquids!=null){
                        for (LiquidStack stack : recipe.consumeLiquids) {
                            table.add(MD_StatValues.displayLiquid(stack.liquid, stack.amount * (60f), true,false)).padRight(2);
                        }
                    }

                    table.add("\uE829\uE829\uE83A   ");

                    if(recipe.outputItems!=null){
                        for (ItemStack stack : recipe.outputItems) {
                            table.add(StatValues.displayItem(stack.item, stack.amount, craftTime, true)).padRight(2);
                        }
                    }
                    if(recipe.outputLiquids!=null){
                        for (LiquidStack stack : recipe.outputLiquids) {
                            table.add(MD_StatValues.displayLiquid(stack.liquid, stack.amount * (60f), true,false)).padRight(2);
                        }
                    }
                    table.row();

                }
            }
            );
        }
    }

    public class MultiRecipeCrafterBuild extends GenericCrafterBuild{
        public MultiRecipeConsume.Recipe recipe;
        @Override
        public void craft(){
          //  recipes.trigger(this);
            if(recipe.outputItems != null){
                for(var output : recipe.outputItems){
                    for(int i = 0; i < output.amount; i++){
                        offload(output.item);
                    }
                }
            }

            if(wasVisible){
                craftEffect.at(x, y);
            }
            progress %= 1f;
        }

        @Override
        public void consume() {
            ItemStack[]items = recipe.consumeItems;
            if(items == null)return;
            for(var stack : items){
                this.items.remove(stack.item, Math.round(stack.amount * recipes.multiplier.get(this)));
            }

        }

        @Override
        public boolean shouldConsume(){
            if(recipe == null)return enabled;
            ItemStack[]outputItems = recipe.outputItems;
            LiquidStack[]outputLiquids = recipe.outputLiquids;
            if(outputItems != null){
                for(var output : outputItems){
                    if(items.get(output.item) + output.amount > itemCapacity){
                        return false;
                    }
                }
            }

            if(outputLiquids != null && !ignoreLiquidFullness){
                boolean allFull = true;
                for(var output : outputLiquids){
                    if(liquids.get(output.liquid) >= liquidCapacity - 0.001f){
                        if(!dumpExtraLiquid){
                            return false;
                        }
                    }else{
                        //if there's still space left, it's not full for all liquids
                        allFull = false;
                    }
                }

                //if there is no space left for any liquid, it can't reproduce
                if(allFull){
                    return false;
                }
            }

            return enabled;
        }

        @Override
        public void updateTile(){
            recipe = recipes.getConsumeRecipe(this);
            if(efficiency > 0){
                progress += getProgressIncrease(craftTime);
                warmup = Mathf.approachDelta(warmup, warmupTarget(), warmupSpeed);

                if(recipe != null) {
                    //continuously output based on efficiency
                    if (recipe.outputLiquids !=null) {
                        float inc = getProgressIncrease(1f);
                        for (var output : recipe.outputLiquids) {
                            handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
                        }
                    }

                    if (wasVisible && Mathf.chanceDelta(updateEffectChance)) {
                        updateEffect.at(x + Mathf.range(size * updateEffectSpread), y + Mathf.range(size * updateEffectSpread));
                    }
                }
            }else{
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
            }

            //TODO may look bad, revert to edelta() if so
            totalProgress += warmup * Time.delta;

            if(progress >= 1f){
                craft();
                consume();
            }

            dumpOutputs();
        }

        @Override
        public float getProgressIncrease(float baseTime){
            if(recipe == null)return 0;
            if(ignoreLiquidFullness){
                return Increase(baseTime);
            }

            //limit progress increase by maximum amount of liquid it can produce
            float scaling = 1f, max = 1f;
            if(recipes.outLiquidType().length >0 && recipe.outputLiquids!=null){
                max = 0f;
                for(var s : recipe.outputLiquids){
                    float value = (liquidCapacity - liquids.get(s.liquid)) / (s.amount * edelta());
                    scaling = Math.min(scaling, value);
                    max = Math.max(max, value);
                }
            }

            //when dumping excess take the maximum value instead of the minimum.
            return Increase(baseTime) * (dumpExtraLiquid ? Math.min(max, 1f) : scaling);
        }

        public float Increase(float baseTime) {
            return 1.0F / baseTime * this.edelta();
        }

        @Override
        public void dumpOutputs(){
            if(recipes.outItemType().length>0&&timer(timerDump, dumpTime / timeScale)){
                for(Item output : recipes.outItemType()){
                    dump(output);
                }
            }

            if(recipes.outLiquidType().length>0){
                for(int i = 0; i < recipes.outLiquidType().length; i++){
                    int dir = liquidOutputDirections.length > i ? liquidOutputDirections[i] : -1;

                    dumpLiquid(recipes.outLiquidType()[i] ,2f, dir);
                }
            }
        }
    }
}
