package com.oyosite.ticon.specutils.block.auxilary_ink_supplier

import com.oyosite.ticon.specutils.SpectralUtilities.Companion.id
import com.oyosite.ticon.specutils.block.BlockRegistry
import com.oyosite.ticon.specutils.block.LinkableBlockEntity
import com.oyosite.ticon.specutils.config.CommonConfig
import de.dafuqs.spectrum.api.block.PlayerOwned
import de.dafuqs.spectrum.api.energy.InkStorage
import de.dafuqs.spectrum.api.energy.InkStorageBlockEntity
import de.dafuqs.spectrum.api.energy.InkStorageItem
import de.dafuqs.spectrum.api.energy.color.InkColor
import de.dafuqs.spectrum.api.energy.color.InkColors
import de.dafuqs.spectrum.blocks.InWorldInteractionBlockEntity
import de.dafuqs.spectrum.blocks.energy.ColorPickerBlockEntity
import me.shedaniel.autoconfig.AutoConfig
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import java.util.*
import java.util.function.Predicate

class AuxiliaryInkSupplierBlockEntity(pos: BlockPos, state: BlockState?) :
    InWorldInteractionBlockEntity(BlockRegistry.BlockEntities.AUXILIARY_INK_SUPPLIER_TYPE, pos, state, 1), PlayerOwned,
    LinkableBlockEntity {
    private var ownerUUID: UUID? = null
    private var ownerName: String? = null
    override var targetPos: BlockPos? = null
        private set

    override fun setTargetPos(target: BlockPos?): Boolean {
        val range =
            AutoConfig.getConfigHolder<CommonConfig?>(CommonConfig::class.java).getConfig()!!.AuxiliaryInkSupplierRange
        if (this.getBlockPos().distSqr(target) > range * range) {
            return false
        }
        this.targetPos = target
        this.setChanged()
        return true
    }

    override fun getOwnerUUID(): UUID? {
        return this.ownerUUID
    }

    override fun setOwner(player: Player) {
        this.ownerUUID = player.getUUID()
        this.ownerName = player.getDisplayName()!!.getString()
        this.setChanged()
    }

    override fun loadAdditional(tag: CompoundTag, registryLookup: HolderLookup.Provider) {
        super.loadAdditional(tag, registryLookup)

        this.ownerUUID = PlayerOwned.readOwnerUUID(tag)
        this.ownerName = PlayerOwned.readOwnerName(tag)
        if (tag.contains("pos", Tag.TAG_COMPOUND.toInt())) {
            val p = tag.getCompound("pos")
            targetPos = BlockPos(p.getInt("x"), p.getInt("y"), p.getInt("z"))
        } else {
            targetPos = null
        }
    }

    override fun saveAdditional(tag: CompoundTag, registryLookup: HolderLookup.Provider) {
        super.saveAdditional(tag, registryLookup)

        PlayerOwned.writeOwnerUUID(tag, this.ownerUUID)
        PlayerOwned.writeOwnerName(tag, this.ownerName)
        if (this.targetPos != null) {
            val p = CompoundTag()
            p.putInt("x", this.targetPos!!.getX())
            p.putInt("y", this.targetPos!!.getY())
            p.putInt("z", this.targetPos!!.getZ())
            tag.put("pos", p)
        }
    }

    companion object {
        val INK_RECEIVERS: TagKey<Block> = TagKey.create(Registries.BLOCK, id("ink_receivers"))
        val INK_PROVIDERS: TagKey<Block> = TagKey.create(Registries.BLOCK, id("ink_providers"))

        @JvmStatic
        fun serverTick(
            world: Level,
            blockPos: BlockPos?,
            blockState: BlockState?,
            blockEntity: AuxiliaryInkSupplierBlockEntity
        ) {
            if (blockEntity.targetPos == null) {
                return
            }

            val heldStack = blockEntity.getItem(0)
            val inkStorageItem = heldStack.item as? InkStorageItem<*>?:return

            val inkStorage: InkStorage = inkStorageItem.getEnergyStorage(heldStack)
            val targetBe = world.getBlockEntity(blockEntity.targetPos) ?: return
            if (targetBe !is InkStorageBlockEntity<*>) return

            val targetStorage: InkStorage = targetBe.getEnergyStorage()

            val targetState = world.getBlockState(blockEntity.targetPos)
            val canReceive: Boolean = targetState.`is`(INK_RECEIVERS)
            val canProvide: Boolean = targetState.`is`(INK_PROVIDERS)

            if (canReceive && inkStorageItem.getDrainability().canDrain(false)) {
                val transferredAmount = InkStorage.transferInk(inkStorage, targetStorage)
                if (transferredAmount > 0L) {
                    inkStorageItem.setEnergyStorage(heldStack, inkStorage)
                }

                targetBe.setInkDirty()
                targetBe.setChanged()
                blockEntity.setChanged()
            }

            if (canProvide) {
                var transferredAmount = 0L
                var colorPredicate = Predicate { inkColor: InkColor? -> true }
                if (targetBe is ColorPickerBlockEntity) {
                    colorPredicate = Predicate { inkColor: InkColor? ->
                        val selectedColor = targetBe.getSelectedColor()
                        selectedColor.isEmpty() || selectedColor.get().value() == inkColor
                    }
                }
                for (inkColor in InkColors.all()) {
                    if (colorPredicate.test(inkColor)) {
                        transferredAmount += InkStorage.transferInk(targetStorage, inkStorage, inkColor)
                    }
                }
                if (transferredAmount > 0) {
                    inkStorageItem.setEnergyStorage(heldStack, inkStorage)
                }

                targetBe.setInkDirty()
                targetBe.setChanged()
                blockEntity.setChanged()
            }
        }
    }
}