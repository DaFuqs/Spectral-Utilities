package com.oyosite.ticon.specutils.block.auxilary_ink_supplier;

import com.mojang.authlib.properties.*;
import com.oyosite.ticon.specutils.*;
import com.oyosite.ticon.specutils.block.*;
import com.oyosite.ticon.specutils.data_components.*;
import com.oyosite.ticon.specutils.item.*;
import de.dafuqs.spectrum.api.block.*;
import de.dafuqs.spectrum.api.energy.*;
import de.dafuqs.spectrum.api.energy.color.*;
import de.dafuqs.spectrum.blocks.*;
import de.dafuqs.spectrum.blocks.energy.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.*;
import net.minecraft.tags.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

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
		return this.targetPos;
	}

	@Override
	public boolean setTargetPos(BlockPos target) {
		if (this.getBlockPos().distSqr(target) > 9) {
			return false;
		}
		this.targetPos = target;
		return true;
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
		this.ownerName = PlayerOwned.readOwnerName(tag);
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
		BlockEntity targetBe = world.getBlockEntity(blockEntity.targetPos);
		if (targetBe == null) {
			return;
		}
		if (!(targetBe instanceof InkStorageBlockEntity<?> inkStorageBlockEntity)) {
			return;
		}
		InkStorage targetStorage = inkStorageBlockEntity.getEnergyStorage();

		BlockState targetState = world.getBlockState(blockEntity.targetPos);
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
			Predicate<InkColor> colorPredicate = inkColor -> true;
			if (targetBe instanceof ColorPickerBlockEntity colorPicker) {
				colorPredicate = inkColor -> {
					Optional<Holder<InkColor>> selectedColor = colorPicker.getSelectedColor();
					return selectedColor.isEmpty() || selectedColor.get().value().equals(inkColor);
				};
			}
			for (InkColor inkColor : InkColors.all()) {
				if (colorPredicate.test(inkColor)) {
					transferredAmount += InkStorage.transferInk(targetStorage, inkStorage, inkColor);
				}
			}
			if (transferredAmount > 0) {
				inkStorageItem.setEnergyStorage(heldStack, inkStorage);
			}

			inkStorageBlockEntity.setInkDirty();
			targetBe.setChanged();
		}

		if (inkStorageItem instanceof EnderFlask && EnderFlask.getOwner(heldStack) != null) {
			EnderFlask.setOwner(heldStack, new ResolvableProfile(Optional.ofNullable(blockEntity.ownerName), Optional.ofNullable(blockEntity.ownerUUID), new PropertyMap()));
		}
	}
}