package vaskii.ambience.objects.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vaskii.ambience.network4.MyMessage4;
import vaskii.ambience.network4.NetworkHandler4;

public class Speaker extends BlockBase implements ITileEntityProvider {

	private boolean red = false;
	public static String selectedSound = null;
	public static int delaySound = 0;
	public static boolean loop = true;
	public static float Distance = 1;
	public static final PropertyDirection FACING = BlockDirectional.FACING;


	public Speaker(String name, Material material) {
		super(name, material);

		setSoundType(SoundType.WOOD);
		setHarvestLevel("pickaxe", 1);
		setHardness(2F);
		setResistance(10F);
		setLightLevel(0F);
		setLightOpacity(0);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));

		if (!FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			I18n.format("Speaker.Desc");
		}
	}

	@Override
	public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
		super.addInformation(itemstack, world, list, flag);
		list.add(I18n.format("Speaker.Desc"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.SOLID;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch ((EnumFacing) state.getValue(BlockDirectional.FACING)) {
			case SOUTH :
			default :
				return new AxisAlignedBB(0.88D, 0.12D, 0D, 0.12D, 0.88D, 0.12D);
			case NORTH :
				return new AxisAlignedBB(0.12D, 0.12D, 1D, 0.88D, 0.88D, 0.88D);
			case WEST :
				return new AxisAlignedBB(1D, 0.12D, 0.88D, 0.88D, 0.88D, 0.12D);
			case EAST :
				return new AxisAlignedBB(0D, 0.12D, 0.12D, 0.12D, 0.88D, 0.88D);
			case UP :
				return new AxisAlignedBB(0.12D, 0D, 0.12D, 0.88D, 0.12D, 0.88D);
			case DOWN :
				return new AxisAlignedBB(0.12D, 1D, 0.88D, 0.88D, 0.88D, 0.12D);
		}
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess baccess, BlockPos pos, EnumFacing side) {
		return red ? 15 : 0;
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess baccess, BlockPos pos, EnumFacing side) {
		return red ? 15 : 0;
	}
	
	@Override
	protected net.minecraft.block.state.BlockStateContainer createBlockState() {
		return new net.minecraft.block.state.BlockStateContainer(this, new IProperty[]{FACING});
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumFacing) state.getValue(FACING)).getIndex();
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, facing);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos fromPos) {
		super.neighborChanged(state, world, pos, neighborBlock, fromPos);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		Block block = this;

		if (world.isBlockIndirectlyGettingPowered(new BlockPos(x, y, z)) > 0) {
			{
				((SpeakerTileEntity) world.getTileEntity(pos)).isPowered = true;
				((SpeakerTileEntity) world.getTileEntity(pos)).countPlay = 0;
			}
		} else {
			{
				((SpeakerTileEntity) world.getTileEntity(pos)).isPowered = false;				
			}
		}
	}

	// Abre a tela de sele��o do som no bloco do speaker
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entity, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		
		if (FMLCommonHandler.instance().getSide().isServer() | !world.isRemote) {
		
			NBTTagCompound tagCompound = new NBTTagCompound();
			
			tagCompound.setInteger("delay",((SpeakerTileEntity) world.getTileEntity(pos)).delay); 
			tagCompound.setString("selectedSound",((SpeakerTileEntity) world.getTileEntity(pos)).selectedSound); 
			tagCompound.setTag("pos",getPosListTag(pos));
			tagCompound.setBoolean("loop",((SpeakerTileEntity) world.getTileEntity(pos)).loop);
			tagCompound.setFloat("distance",((SpeakerTileEntity) world.getTileEntity(pos)).distance); 
			
			
			NetworkHandler4.sendToClient(new MyMessage4(tagCompound), (EntityPlayerMP) entity);	
		}		
		
		return true;
	}
	
	public NBTTagList getPosListTag(BlockPos pos) {
		NBTTagList tagList = new NBTTagList();

		NBTTagCompound posCompound = new NBTTagCompound();
		posCompound.setInteger("x", pos.getX());
		posCompound.setInteger("y", pos.getY());
		posCompound.setInteger("z", pos.getZ());
		
		tagList.appendTag(posCompound);

		return tagList;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new SpeakerTileEntity();
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new SpeakerTileEntity();
	}

}
