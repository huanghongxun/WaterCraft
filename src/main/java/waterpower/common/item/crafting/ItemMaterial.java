package waterpower.common.item.crafting;

import static net.minecraftforge.fml.common.registry.GameRegistry.addShapedRecipe;
import static waterpower.common.item.crafting.MaterialForms.dust;
import static waterpower.common.item.crafting.MaterialForms.dustSmall;
import static waterpower.common.item.crafting.MaterialForms.dustTiny;
import static waterpower.common.item.crafting.MaterialForms.gear;
import static waterpower.common.item.crafting.MaterialForms.ingot;
import static waterpower.common.item.crafting.MaterialForms.nugget;
import static waterpower.common.item.crafting.MaterialForms.plate;
import static waterpower.common.item.crafting.MaterialForms.plateDense;
import static waterpower.common.item.crafting.MaterialForms.ring;
import static waterpower.common.item.crafting.MaterialForms.screw;
import static waterpower.common.item.crafting.MaterialForms.stick;
import static waterpower.common.item.crafting.MaterialTypes.IndustrialSteel;
import static waterpower.common.item.crafting.MaterialTypes.ManganeseSteel;
import static waterpower.common.item.crafting.MaterialTypes.NeodymiumMagnet;
import static waterpower.common.item.crafting.MaterialTypes.Steel;
import static waterpower.common.item.crafting.MaterialTypes.VanadiumSteel;
import static waterpower.common.item.crafting.MaterialTypes.ZincAlloy;
import static waterpower.common.item.crafting.MaterialTypes.space;
import static waterpower.common.recipe.IRecipeRegistrar.addShapelessRecipeByOreDictionary;

import java.awt.Color;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import waterpower.client.render.IIconContainer;
import waterpower.client.render.RecolorableTextures;
import waterpower.common.block.GlobalBlocks;
import waterpower.common.item.ItemRecolorable;
import waterpower.common.item.other.ItemType;
import waterpower.common.recipe.IRecipeRegistrar;
import waterpower.common.recipe.MyRecipeInputOreDictionary;
import waterpower.common.recipe.RecipeAdder;
import waterpower.integration.EnderIOModule;
import waterpower.util.Mods;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemMaterial extends ItemRecolorable {

    public static ItemMaterial instance;

    public ItemMaterial() {
        super("material");
        setHasSubtypes(true);

        instance = this;

        registerAllRecipes();
        registerOreDict();
    }

    @Override
    public String getTextureFolder() {
        return "material";
    }

    @Override
    public String getItemStackDisplayName(ItemStack par1ItemStack) {
        int meta = par1ItemStack.getItemDamage();
        int craftingType = meta / space;
        int levelType = meta % space;
        return MaterialTypes.values()[craftingType].getShowedName() + " " + MaterialForms.values()[levelType].getShowedName();
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        try {
            int meta = itemstack.getItemDamage();
            int meterialType = meta / space;
            int meterialForm = meta % space;
            return "item." + "watermill." + MaterialTypes.values()[meterialType].name() + "." + MaterialForms.values()[meterialForm].name();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean stopScanning(ItemStack stack) {
        return false;
    }

    public static ItemStack get(MaterialTypes craftingTypes, MaterialForms levelTypes) {
        return get(craftingTypes, levelTypes, 1);
    }

    public static ItemStack get(MaterialTypes craftingTypes, MaterialForms levelTypes, int mount) {
        return new ItemStack(instance, mount, craftingTypes.ind() + levelTypes.ordinal());
    }

    public void registerOreDict() {
        for (MaterialForms forms : MaterialForms.values()) {
            for (MaterialTypes types : MaterialTypes.values()) {
                IRecipeRegistrar.registerOreDict(forms.name() + types.getName(), get(types, forms));
            }
        }
    }

    public void registerAllRecipes() {
        if (Mods.EnderIO.isAvailable) {
            EnderIOModule.alloySmelter("Zinc Alloy Dust", get(VanadiumSteel, ingot, 3), new MyRecipeInputOreDictionary("ingotVanadium"),
                    new MyRecipeInputOreDictionary("ingotSteel"), new MyRecipeInputOreDictionary("ingotSteel"));
            EnderIOModule.alloySmelter("Neodymium Magnet Dust", get(NeodymiumMagnet, ingot, 2), new MyRecipeInputOreDictionary("ingotNeodymium"),
                    new MyRecipeInputOreDictionary("ingotMagnetite"));
            EnderIOModule.alloySmelter("Vanadium Steel Dust", get(VanadiumSteel, ingot, 3), new MyRecipeInputOreDictionary("ingotVanadium"),
                    new MyRecipeInputOreDictionary("ingotSteel"), new MyRecipeInputOreDictionary("ingotSteel"));
            EnderIOModule.alloySmelter("Manganese Steel Dust 3", get(ManganeseSteel, ingot, 3), new MyRecipeInputOreDictionary("ingotManganese"),
                    new MyRecipeInputOreDictionary("ingotSteel"), new MyRecipeInputOreDictionary("ingotSteel"));
            EnderIOModule.alloySmelter("Manganese Steel Dust 4", get(ManganeseSteel, ingot, 4), new MyRecipeInputOreDictionary("ingotManganese"),
                    new MyRecipeInputOreDictionary("ingotSteel"), new MyRecipeInputOreDictionary("ingotSteel"), new MyRecipeInputOreDictionary("ingotCoal"));
        }
        addShapelessRecipeByOreDictionary(get(ZincAlloy, dust, 5), "dustZinc", "dustZinc", "dustZinc", "dustZinc", "dustCopper");
        addShapelessRecipeByOreDictionary(get(VanadiumSteel, dust, 3), "dustVanadium", "dustSteel", "dustSteel");
        addShapelessRecipeByOreDictionary(get(NeodymiumMagnet, dust, 2), "dustNeodymium", "dustMagnetite");
        addShapelessRecipeByOreDictionary(get(ManganeseSteel, dust, 4), "dustManganese", "dustSteel", "dustSteel", "dustCoal");
        addShapelessRecipeByOreDictionary(get(ManganeseSteel, dust, 3), "dustManganese", "dustSteel", "dustSteel");
        List<ItemStack> steelIngots = OreDictionary.getOres("ingotSteel");
        boolean flag = false;
        for (ItemStack is : steelIngots)
            flag |= RecipeAdder.blastFurnace(is, get(IndustrialSteel, ingot), 1000);
        if (!flag) {
            GameRegistry.addSmelting(new ItemStack(Items.IRON_INGOT), get(Steel, ingot), 0);
        }

        for (MaterialTypes types : MaterialTypes.values()) {
            addShapelessRecipeByOreDictionary(get(types, dust), get(types, dustSmall), get(types, dustSmall), get(types, dustSmall), get(types, dustSmall));

            addShapelessRecipeByOreDictionary(get(types, dustSmall, 4), get(types, dust));

            addShapedRecipe(get(types, dust), "AAA", "AAA", "AAA", 'A', get(types, dustTiny));

            addShapedRecipe(get(types, dustTiny, 9), "A", 'A', get(types, dust));

            addShapedRecipe(new ItemStack(GlobalBlocks.material, 1, types.ordinal()), "AAA", "AAA", "AAA", 'A', get(types, ingot));
            RecipeAdder.compressor(get(types, ingot, 9), new ItemStack(GlobalBlocks.material, 1, types.ordinal()));

            addShapedRecipe(get(types, ingot), "AAA", "AAA", "AAA", 'A', get(types, nugget));
            RecipeAdder.compressor(get(types, nugget, 9), get(types, ingot));

            addShapelessRecipeByOreDictionary(get(types, nugget, 9), get(types, ingot));

            addShapedRecipe(get(types, stick, 4), "A", "A", 'A', get(types, ingot));

            addShapedRecipe(get(types, gear), // 4 sticks & 4 plates -> 1 gear
                    "SPS", "P P", "SPS", 'S', get(types, stick), 'P', get(types, plate));

            addShapedRecipe(get(types, ring), // 4 sticks -> 1 ring
                    " S ", "S S", " S ", 'S', get(types, stick));

            addShapelessRecipeByOreDictionary(get(types, ingot, 9), new ItemStack(GlobalBlocks.material, 1, types.ordinal()));

            GameRegistry.addSmelting(get(types, dust), get(types, ingot), 0);
            GameRegistry.addSmelting(get(types, dustTiny), get(types, nugget), 0);
            GameRegistry.addSmelting(new ItemStack(GlobalBlocks.material, 1, types.ordinal()), get(types, ingot, 9), 0);

            RecipeAdder.bender(get(types, ingot), get(types, plate));
            RecipeAdder.bender(get(types, plate, 9), get(types, plateDense));
            RecipeAdder.macerator(get(types, ingot), get(types, dust));
            RecipeAdder.macerator(get(types, plate), get(types, dust));
            RecipeAdder.macerator(new ItemStack(GlobalBlocks.material, 1, types.ordinal()), get(types, dust, 9));
            RecipeAdder.macerator(get(types, plateDense), get(types, dust, 9));
            RecipeAdder.macerator(get(types, screw), get(types, dustSmall));
            RecipeAdder.lathe(get(types, stick), get(types, screw, 4));

            addShapelessRecipeByOreDictionary(get(types, plateDense), ItemType.WoodenHammer.item(), new ItemStack(GlobalBlocks.material, 1, types.ordinal()));
        }

    }

    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        for (MaterialTypes c : MaterialTypes.values())
            for (MaterialForms l : MaterialForms.values())
                par3List.add(get(c, l));
    }

    public IIconContainer getIconContainer(int meta, MaterialTypes type) {
        return getIconContainers()[meta % space];
    }

    @Override
    public IIconContainer getIconContainer(ItemStack stk) {
    	int meta = stk.getItemDamage();
        return getIconContainer(meta, MaterialTypes.values()[meta / space]);
    }

    @Override
	public int getColorFromItemstack(ItemStack stack, int tintIndex) {
        int meta = stack.getItemDamage();
        int craftingType = meta / space;
        MaterialTypes type = MaterialTypes.values()[craftingType];
        return new Color(type.R, type.G, type.B, type.A).getRGB();
    }

    @Override
    public IIconContainer[] getIconContainers() {
        return RecolorableTextures.METAL;
    }

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (stack.stackSize > 0) {
            int meta = stack.getItemDamage();
            MaterialTypes t = MaterialTypes.values()[meta / space];
            MaterialForms f = MaterialForms.values()[meta % space];
            if (t == MaterialTypes.Magnet && f == MaterialForms.ingot) {
                player.inventory.addItemStackToInventory(new ItemStack(Items.IRON_INGOT));
                stack.stackSize--;
            }
        }
        return EnumActionResult.PASS;
    }
}