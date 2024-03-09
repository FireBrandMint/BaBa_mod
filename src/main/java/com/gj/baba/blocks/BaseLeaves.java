package com.gj.baba.blocks;

import com.gj.baba.BaBa;
import com.gj.baba.Items.IHasModel;
import com.gj.baba.init.BlockInit;
import com.gj.baba.init.ItemInit;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import scala.xml.dtd.impl.Base;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public abstract class BaseLeaves extends BlockBase implements IShearable, IHasModel
{
    private GameSettings settings = null;
    private boolean leavesFancy;
    public static PropertyBool CAN_DECAY = PropertyBool.create("placed");
    public static PropertyBool DECAYING = PropertyBool.create("decay");


    public BaseLeaves (String name, Material material, CreativeTabs tab)
    {
        super(name, material, tab);

        this.setTickRandomly(true);
        this.setDefaultState(initializeProperties(this.blockState.getBaseState()));
        this.setSoundType(SoundType.PLANT);
        this.setHardness(0.2f);
    }

    public abstract Block getLog ();

    public abstract Item getSapling();

    public IBlockState getDecayable()
    {
        return this.getDefaultState().withProperty(CAN_DECAY, true);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {CAN_DECAY, DECAYING});
    }

    public IBlockState initializeProperties(IBlockState subject)
    {
        return subject.withProperty(CAN_DECAY, false).withProperty(DECAYING, false);
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {

        if(!worldIn.isBlockLoaded(pos) || !state.getValue(CAN_DECAY).booleanValue()) return;

        if(state.getValue(DECAYING))
        {
            worldIn.destroyBlock(pos, true);

            if(!worldIn.isAreaLoaded(pos, 1)) return;
            for(int i = -1; i < 2; i+=2)
            {
                BlockPos b1 = pos.add(i,0f,0f);
                IBlockState s1;
                if(worldIn.isBlockLoaded(b1))
                {
                    s1 = worldIn.getBlockState(b1);
                    if(s1.getBlock() == this) beginLeavesDecay(s1, worldIn, b1);
                }

                b1 = pos.add(0f, i,0f);
                if(worldIn.isBlockLoaded(b1))
                {
                    s1 = worldIn.getBlockState(b1);
                    if(s1.getBlock() == this) beginLeavesDecay(s1, worldIn, b1);
                }

                b1 = pos.add(0f, 0f, i);
                if(worldIn.isBlockLoaded(b1))
                {
                    s1 = worldIn.getBlockState(b1);
                    if(s1.getBlock() == this) beginLeavesDecay(s1, worldIn, b1);
                }
            }
        }
    }

    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        Random rand = world instanceof World ? ((World)world).rand : new Random();
        Item sapling = getSapling();
        if(sapling != null && rand.nextInt(5) >= 4 - fortune) drops.add(new ItemStack(getSapling(), 1 + fortune));
    }

    @Override
    public boolean isShearable(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos)
    {
        return true;
    }
    @Override
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;

        if(state.getValue(CAN_DECAY).booleanValue())
        {
            i += 4;
        }

        if(state.getValue(DECAYING).booleanValue())
        {
            i += 8;
        }

        return i;
    }

    public IBlockState getStateFromMeta(int meta)
    {
        boolean can = meta == 4 | meta == 12;
        boolean decaying = meta == 8 | meta == 12;

        return this.getDefaultState()
                .withProperty(CAN_DECAY, can)
                .withProperty(DECAYING, decaying);
    }

    @Override
    public void RegisterModels()
    {
        BaBa.Proxy.RegisterItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
    }

    @Override
    public NonNullList<ItemStack> onSheared(ItemStack item, net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune)
    {
        IBlockState state = world.getBlockState(pos);
        return NonNullList.withSize(1, new ItemStack(state.getBlock(), 1, this.getMetaFromState(state.withProperty(CAN_DECAY, false))));
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return this.isLeavesFancy() ? BlockRenderLayer.CUTOUT_MIPPED : BlockRenderLayer.SOLID;
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return !isLeavesFancy() && blockAccess.getBlockState(pos.offset(side)).getBlock() == this ? false : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }

    public boolean isOpaqueCube(IBlockState state)
    {
        return !isLeavesFancy();
    }

    public boolean isLeavesFancy()
    {
        if(settings != null) return settings.fancyGraphics;

        if(FMLCommonHandler.instance().getSide() == Side.SERVER)
            return true;

        settings = Minecraft.getMinecraft().gameSettings;

        return settings.fancyGraphics;
    }

    public void beginLeavesDecay(IBlockState state, World world, BlockPos pos)
    {
        if (state.getValue(CAN_DECAY).booleanValue() && !state.getValue(DECAYING).booleanValue())
        {
            world.setBlockState(pos, state.withProperty(DECAYING, true));
        }


    }
}
