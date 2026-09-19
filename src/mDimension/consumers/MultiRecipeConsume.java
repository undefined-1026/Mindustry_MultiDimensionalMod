package mDimension.consumers;

import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.consumers.Consume;
import mindustry.world.modules.ItemModule;
import mindustry.world.modules.LiquidModule;

public class MultiRecipeConsume extends Consume {
    public Seq<Recipe> consumeRecipe = new Seq<>();
    public Seq<Item> outItem = new Seq<>();
    public Seq<Liquid> outLiquid = new Seq<>();
    public Seq<Liquid> inputLiquid = new Seq<>();
    public static Recipe NullRecipe= new Recipe();

    public static LiquidStack[] NullLiquidStacks = new LiquidStack[0];

    public static ItemStack[] NullItemStacks = new ItemStack[0];

    public Item[] outItemType(){
        return outItem.toArray(Item.class);
    }
    public Liquid[] outLiquidType(){
        return outLiquid.toArray(Liquid.class);
    }
    public Liquid[] inputLiquidType(){
        return inputLiquid.toArray(Liquid.class);
    }

    public MultiRecipeConsume(Recipe... recipe){
        if(recipe.length>0)consumeRecipe.add(recipe);
    }
    public MultiRecipeConsume(){}

    @Override
    public void apply(Block block){
        for(var recipe : consumeRecipe){
            if(recipe.consumeItems!=null) {
                for (var item : recipe.consumeItems) {
                    block.itemFilter[item.item.id] = true;
                }
                block.hasItems = true;
                block.acceptsItems = true;
            }
            if(recipe.consumeLiquids!=null) {
                for (var liquid : recipe.consumeLiquids) {
                    block.liquidFilter[liquid.liquid.id] = true;
                    if(!inputLiquid.contains(liquid.liquid)){
                        inputLiquid.add(liquid.liquid);
                        //block.addLiquidBar(liquid.liquid);
                    }
                }
                block.hasLiquids = true;

            }
            if(recipe.outputLiquids !=null){
                for (var liquid : recipe.outputLiquids) {
                    if(!outLiquid.contains(liquid.liquid)){
                        outLiquid.add(liquid.liquid);
                    }
                }
                block.outputsLiquid = true;
            }
            if(recipe.outputItems !=null){
                for (var item : recipe.outputItems) {
                    if(!outItem.contains(item.item)){
                        outItem.add(item.item);
                    }
                }
            }
        }
    }

    @Override
    public void trigger(Building build){
        Recipe recipe = getConsumeRecipe(build);
        if(recipe == null)return;
        ItemStack[]items = recipe.consumeItems;
        if(items == null)return;
        for(var stack : items){
            build.items.remove(stack.item, Math.round(stack.amount * multiplier.get(build)));
        }
        craft(build,recipe.outputItems);
    }

    @Override
    public void update(Building build) {
        Recipe recipe = getConsumeRecipe(build);
        if(recipe == null)return;
        LiquidStack[]liquid = recipe.consumeLiquids;
        if(liquid == null)return;
        for(var stack:liquid){
            build.liquids.remove(stack.liquid,stack.amount * build.edelta() * multiplier.get(build));
        }
    }

    public void craft(Building b, ItemStack[] it){
        if(it == null)return;
        for(var output : it){
            for(int i = 0; i < output.amount; i++){
                b.offload(output.item);
            }
        }
    }
    @Nullable
    public Recipe getConsumeRecipe(Building b){
        ItemModule it = b.items;
        for(int i = 0; i< consumeRecipe.size; i++){
            Recipe r = consumeRecipe.get(i);
            boolean itemPass = false,liquidPass = false;
            if (r.consumeItems == null || b.items.has(r.consumeItems)) {
                itemPass = true;
            }
            if (r.consumeLiquids == null || hasLiquidStackList(b.liquids,r.consumeLiquids)){
                liquidPass = true;
            }

            if(itemPass&&liquidPass)return r;
        }
        return NullRecipe;
    }

    public boolean shouldUse(ItemStack[] itemStacks,ItemModule it){
        return it.has(itemStacks);
    }

    @Override
    public float efficiency(Building b) {
        for (int i = 0; i < consumeRecipe.size; i++) {
            boolean itemPass = false,liquidPass = false;
            Recipe r = consumeRecipe.get(i);
            if (!b.block.hasItems || r.consumeItems == null || b.items.has(r.consumeItems)) {
                itemPass = true;
            }
            if (!b.block.hasLiquids || r.consumeLiquids == null || hasLiquidStackList(b.liquids,r.consumeLiquids)){
                liquidPass = true;
            }

            if(itemPass&&liquidPass)return 1f;
        }
        return 0f;
    }

    public boolean notFullItem(Building b,ItemStack[] stacks){
        if(stacks == null)return true;
        for(ItemStack stack:stacks){
            if(stack.amount>=b.block.itemCapacity)return false;
        }
        return true;
    }
    public boolean notFullLiquid(Building b,LiquidStack[] stacks){
        if(stacks == null)return true;
        for(LiquidStack stack:stacks){
            if(stack.amount>=b.block.liquidCapacity)return false;
        }
        return true;
    }

    public boolean hasLiquidStackList(LiquidModule module,LiquidStack[] stacks){
        if(module == null)return true;
        for(var stack:stacks){
            if(module.get(stack.liquid) < 0.01f){
                return false;
            }
        }
        return true;
    }

    public boolean outputLiquid(Building b){
        //if(getConsumeRecipe(b) == null)return false;
        return getConsumeRecipe(b).outputLiquids.length>0;
    }

    public static class Recipe {
//        public ItemStack[] consumeItems = NullItemStacks;
//        public ItemStack[] outputItems = NullItemStacks;
//        public LiquidStack[] consumeLiquids = NullLiquidStacks;
//        public LiquidStack[] outputLiquids = NullLiquidStacks;
        public ItemStack[] consumeItems;
        public ItemStack[] outputItems;
        public LiquidStack[] consumeLiquids;
        public LiquidStack[] outputLiquids;
    }
}
