package com.oyosite.ticon.specutils.block.auxilary_ink_supplier;

import com.mojang.serialization.*;
import com.oyosite.ticon.specutils.block.*;
import de.dafuqs.spectrum.api.energy.*;
import de.dafuqs.spectrum.blocks.*;
import de.dafuqs.spectrum.registries.*;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class AuxiliaryInkSupplierBlock extends InWorldInteractionBlock {
    
    protected static final VoxelShape BASE_SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 10.0, 12.0);
    protected static final VoxelShape TOP_SHAPE = Block.box(3.0, 10.0, 3.0, 13.0, 14.0, 13.0);
    protected static final VoxelShape SHAPE = Shapes.or(BASE_SHAPE, TOP_SHAPE);
    
	public AuxiliaryInkSupplierBlock(Properties settings) {
		super(settings.lightLevel(state -> Math.max(5, state.getValue(FluidLogging.ANY_INCLUDING_NONE).getLuminance())));
        registerDefaultState(getStateDefinition().any().setValue(FluidLogging.ANY_INCLUDING_NONE, FluidLogging.State.NOT_LOGGED));
	}
    
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }
    
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    
    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AuxiliaryInkSupplierBlockEntity(pos, state);
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FluidLogging.ANY_INCLUDING_NONE);
    }
    
    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(FluidLogging.ANY_INCLUDING_NONE).getFluidState();
    }
    
    @Override
    protected boolean isCollisionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }
    
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(level.isClientSide()) return ItemInteractionResult.SUCCESS;
        
        Optional<AuxiliaryInkSupplierBlockEntity> blockEntity = level.getBlockEntity(pos, BlockRegistry.BlockEntities.AUXILIARY_INK_SUPPLIER_TYPE);
        if(blockEntity.isEmpty()) return ItemInteractionResult.CONSUME;
        
        AuxiliaryInkSupplierBlockEntity auxiliaryInkSupplierBlockEntity = blockEntity.get();
        if(stack.isEmpty() || stack.getItem() instanceof InkStorageItem<?> && this.exchangeStack(level, pos, player, hand, stack, auxiliaryInkSupplierBlockEntity, 0)){
            auxiliaryInkSupplierBlockEntity.inventoryChanged();
            auxiliaryInkSupplierBlockEntity.setOwner(player);
        }
        
        return ItemInteractionResult.CONSUME;
    }
    
    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }
    
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, SpectrumBlockEntities.AUXILIARY_INK_SUPPLIER, world.isClientSide ? null : AuxiliaryInkSupplierBlockEntity::serverTick);
    }

}