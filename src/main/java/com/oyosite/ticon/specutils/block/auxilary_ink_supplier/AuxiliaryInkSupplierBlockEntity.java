package com.oyosite.ticon.specutils.block.auxilary_ink_supplier;

import com.oyosite.ticon.specutils.*;
import com.oyosite.ticon.specutils.block.*;
import com.oyosite.ticon.specutils.item.*;
import de.dafuqs.spectrum.api.block.*;
import de.dafuqs.spectrum.api.energy.*;
import de.dafuqs.spectrum.api.energy.color.*;
import de.dafuqs.spectrum.blocks.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.*;
import net.minecraft.tags.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;

import java.util.*;

public class AuxiliaryInkSupplierBlockEntity extends InWorldInteractionBlockEntity implements PlayerOwned, LinkableBlockEntity {
	
	public static final TagKey<Block> INK_RECEIVERS = TagKey.create(Registries.BLOCK, SpectralUtilities.id("ink_receivers"));
	public static final TagKey<Block> INK_PROVIDERS = TagKey.create(Registries.BLOCK, SpectralUtilities.id("ink_providers"));
	
	private UUID ownerUUID = null;
	private String ownerName = null;
	private BlockPos targetPos = null;
	
	public AuxiliaryInkSupplierBlockEntity(BlockPos pos, BlockState state) {
		super(BlockRegistry.BlockEntities.AUXILIARY_INK_SUPPLIER_TYPE, pos, state, 1);
	}
	
	@Override
	public BlockPos getTargetPos() {
		return null;
	}
	
	@Override
	public boolean canBind(BlockPos target) {
		return this.getBlockPos().distSqr(target) <= 9;
	}
	
	@Override
	public UUID getOwnerUUID() {
		return this.ownerUUID;
	}
	
	@Override
	public void setOwner(Player player) {
		this.ownerUUID = player.getUUID();
		this.ownerName = player.getDisplayName().getString();
		this.setChanged();
	}
	
	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registryLookup) {
		super.loadAdditional(tag, registryLookup);
		
		this.ownerUUID = PlayerOwned.readOwnerUUID(tag);
		if (tag.contains("ownerName", Tag.TAG_STRING)) {
			ownerName = tag.getString("ownerName");
		}
		if (tag.contains("pos", Tag.TAG_COMPOUND)) {
			CompoundTag p = tag.getCompound("pos");
			targetPos = new BlockPos(p.getInt("x"), p.getInt("y"), p.getInt("z"));
		} else {
			targetPos = null;
		}
	}
	
	@Override
	public void saveAdditional(CompoundTag tag, HolderLookup.Provider registryLookup) {
		super.saveAdditional(tag, registryLookup);
		
		PlayerOwned.writeOwnerUUID(tag, this.ownerUUID);
		PlayerOwned.writeOwnerName(tag, this.ownerName);
		if (this.targetPos != null) {
			CompoundTag p = new CompoundTag();
			p.putInt("x", this.targetPos.getX());
			p.putInt("y", this.targetPos.getY());
			p.putInt("z", this.targetPos.getZ());
			tag.put("pos", p);
		}
	}
	
	public static void serverTick(Level world, BlockPos blockPos, BlockState blockState, AuxiliaryInkSupplierBlockEntity blockEntity) {
		ItemStack heldStack = blockEntity.getItem(0);
		if (!(heldStack.getItem() instanceof InkStorageItem<?> inkStorageItem)) {
			return;
		}
		
		InkStorage inkStorage = inkStorageItem.getEnergyStorage(heldStack);
		
		BlockPos targetPos = getTargetPos(heldStack);
		BlockEntity targetBe = world.getBlockEntity(targetPos);
		if (targetBe == null) {
			return;
		}
		if (!(targetBe instanceof InkStorageBlockEntity<?> inkStorageBlockEntity)) {
			return;
		}
		InkStorage targetStorage = inkStorageBlockEntity.getEnergyStorage();
		
		BlockState targetState = world.getBlockState(targetPos);
		boolean canReceive = targetState.is(INK_RECEIVERS);
		boolean canProvide = targetState.is(INK_PROVIDERS);
		
		if (canReceive && inkStorageItem.getDrainability().canDrain(false)) {
			long transferredAmount = InkStorage.transferInk(inkStorage, targetStorage);
			if (transferredAmount > 0L) {
				inkStorageItem.setEnergyStorage(heldStack, inkStorage);
			}
			
			inkStorageBlockEntity.setInkDirty();
			targetBe.setChanged();
		}
		
		if (canProvide) {
			var transferredAmount = 0L;
			var colorPredicate:(InkColor) -> Boolean = {true}
			if (targetBe is ColorPickerBlockEntity)colorPredicate = {targetBe.selectedColor ?.equals(it) ?:true}
			for (inkColor in InkColors.all().filter(colorPredicate))
				transferredAmount += InkStorage.transferInk(targetStorage, inkStorage, inkColor)
			if (transferredAmount > 0) storageItem.setEnergyStorage(heldStack, inkStorage)
			
			inkStorageBlockEntity.setInkDirty();
			targetBe.setChanged();
		}
		
		if (inkStorageItem instanceof EnderFlask enderFlask && enderFlask.getOwnerUUID() != null){
			heldStack.owner = ownerUUID;
			heldStack.ownerName = ownerName ?:Text.empty();
		}
	}
}