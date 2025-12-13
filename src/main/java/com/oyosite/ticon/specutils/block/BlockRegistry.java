package com.oyosite.ticon.specutils.block;

import com.oyosite.ticon.specutils.*;
import com.oyosite.ticon.specutils.block.auxilary_ink_supplier.*;
import de.dafuqs.spectrum.registries.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.blockrenderlayer.v1.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;

public class BlockRegistry {

	public static Block CALCITE_AUXILIARY_INK_SUPPLIER = register("calcite_auxiliary_ink_supplier", new AuxiliaryInkSupplierBlock(BlockBehaviour.Properties.of()), new Item.Properties());
	public static Block BASALT_AUXILIARY_INK_SUPPLIER = register("basalt_auxiliary_ink_supplier", new AuxiliaryInkSupplierBlock(BlockBehaviour.Properties.of()), new Item.Properties());
	public static Block MOONSTONE_GROW_LAMP = register("moonstone_grow_lamp", new com.oyosite.ticon.specutils.block.MoonstoneGrowLampBlock(BlockBehaviour.Properties.of().strength(0.6f)), new Item.Properties());

	public static CrystallarieumMaterial DRAGONBONE = new CrystallarieumMaterial.Builder()
			.createBuds(SpectrumBlocks.CRACKED_DRAGONBONE)
			.build();

	public static class BlockEntities {
		public static BlockEntityType<AuxiliaryInkSupplierBlockEntity> AUXILIARY_INK_SUPPLIER_TYPE = registerBlockEntity("auxiliary_ink_supplier", AuxiliaryInkSupplierBlockEntity::new, CALCITE_AUXILIARY_INK_SUPPLIER, BASALT_AUXILIARY_INK_SUPPLIER);
	}

	private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String name, BlockEntityType.BlockEntitySupplier<T> factory, Block... blocks) {
		return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, SpectralUtilities.id(name), BlockEntityType.Builder.of(factory, blocks).build());
	}

	public static void register() {
		CrystallarieumMaterial.register(SpectralUtilities.id("dragonbone"), DRAGONBONE);
		NoxwoodDeco.registerAll();
	}

	private static Block register(String name, Block block, Item.Properties properties) {
		ResourceLocation id = SpectralUtilities.id(name);
		Registry.register(BuiltInRegistries.BLOCK, id, block);
		Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block, properties));
		return block;
	}

	@Environment(EnvType.CLIENT)
	public static void registerClient() {
		BlockEntityRenderers.register(BlockEntities.AUXILIARY_INK_SUPPLIER_TYPE, AuxiliaryInkSupplierBlockEntityRenderer::new);

		for (Block block : NoxwoodDeco.LAMPS.values()) {
			BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutout());
		}
	}

}