package com.oyosite.ticon.specutils.item

import com.oyosite.ticon.specutils.component.ScoreboardComponentEntrypoint
import com.oyosite.ticon.specutils.component.StaticEnderInkStorageComponent
import com.oyosite.ticon.specutils.ink.ColorLockedInkStorage
import de.dafuqs.spectrum.api.energy.InkStorage
import de.dafuqs.spectrum.api.energy.color.InkColor
import de.dafuqs.spectrum.api.energy.storage.SingleInkStorage
import de.dafuqs.spectrum.items.energy.InkFlaskItem
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.server.ServerScoreboard
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.ResolvableProfile
import net.minecraft.world.level.Level

class EnderFlask(settings: Properties, protected var inkColor: InkColor?) :
    InkFlaskItem(settings, StaticEnderInkStorageComponent.CAPACITY) {
    override fun getEnergyStorage(itemStack: ItemStack): SingleInkStorage? {
        val owner: ResolvableProfile? = getOwner(itemStack)
        if (level == null || owner == null || owner.id().isEmpty()) {
            return DUMMY_ENERGY_STORAGE
        }

        val storage = ScoreboardComponentEntrypoint.ENDER_FLASK.get(scoreboard)
        return storage!![level!!, getOwner(itemStack)!!.id().get(), inkColor!!]
    }

    override fun setEnergyStorage(itemStack: ItemStack, storage: InkStorage?) {
        val owner: ResolvableProfile? = getOwner(itemStack)
        if (owner == null || owner.id().isEmpty()) {
            return
        }
        val s = storage as ColorLockedInkStorage?
        ScoreboardComponentEntrypoint.ENDER_FLASK.get(scoreboard)!!.set(level!!, owner.id().get(), inkColor!!, s!!)
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack?> {
        val stack = player.getItemInHand(usedHand)
        setOwner(stack, player)

        return InteractionResultHolder.sidedSuccess<ItemStack?>(stack, level.isClientSide)
    }

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        super.inventoryTick(stack, level, entity, slotId, isSelected)

        if (scoreboard != null && entity is Player) {
            entity.getScoreboard()
        }
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltip: MutableList<Component?>,
        type: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltip, type)

        val profile: ResolvableProfile? = getOwner(stack)
        if (profile == null) {
            tooltip.add(Component.translatable("item.specutils.ender_flask.tooltip.unlinked_0"))
            tooltip.add(Component.translatable("item.specutils.ender_flask.tooltip.unlinked_1"))
            return
        } else {
            tooltip.add(
                Component.translatable(
                    "item.specutils.ender_flask.tooltip.owner",
                    profile.name().orElse("???")
                )
            )
        }

        if (getEnergyStorage(stack) !is ColorLockedInkStorage) {
            return
        }

        super.appendHoverText(stack, context, tooltip, type)
        tooltip.removeLast()
    }

    companion object {
        val DUMMY_ENERGY_STORAGE: SingleInkStorage = SingleInkStorage(0)

        var level: Level? = null
        var scoreboard: ServerScoreboard? = null

        fun getOwner(stack: ItemStack): ResolvableProfile? {
            return stack.get<ResolvableProfile?>(DataComponents.PROFILE)
        }

        private fun setOwner(stack: ItemStack, player: Player) {
            setOwner(stack, ResolvableProfile(player.getGameProfile()))
        }

        fun setOwner(stack: ItemStack, profile: ResolvableProfile?) {
            stack.set<ResolvableProfile?>(DataComponents.PROFILE, profile)
        }
    }
}