package com.oyosite.ticon.specutils.block;

import com.oyosite.ticon.specutils.*;
import com.oyosite.ticon.specutils.block.auxilary_ink_supplier.*;
import de.dafuqs.spectrum.*;
import de.dafuqs.spectrum.registries.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.blockrenderlayer.v1.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;

public class BlockRegistry {

    public static Block CALCITE_AUXILIARY_INK_SUPPLIER = new AuxiliaryInkSupplierBlock(BlockBehaviour.Properties.of());
    public static Block BASALT_AUXILIARY_INK_SUPPLIER = new AuxiliaryInkSupplierBlock(BlockBehaviour.Properties.of());
    public static Block MOONSTONE_GROW_LAMP = new com.oyosite.ticon.specutils.block.MoonstoneGrowLampBlock(BlockBehaviour.Properties.of().strength(0.6f));
    
    public static CrystallarieumMaterial DRAGONBONE = new CrystallarieumMaterial.Builder().input(SpectrumItems.DRAGONBONE_CHUNK).createBuds(SpectrumBlocks.CRACKED_DRAGONBONE).createPureItem();


    public class BlockEntities {
        public static BlockEntityType<AuxiliaryInkSupplierBlockEntity> AUXILIARY_INK_SUPPLIER_TYPE = registerBlockEntity("auxiliary_ink_supplier", AuxiliaryInkSupplierBlockEntity::new, CALCITE_AUXILIARY_INK_SUPPLIER, BASALT_AUXILIARY_INK_SUPPLIER);
    }
    
    private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String name, BlockEntityType.BlockEntitySupplier<T> factory, Block... blocks) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, SpectralUtilities.id(name), BlockEntityType.Builder.of(factory, blocks).build());
    }
    
    public static void register(){
        val dmp = BlockRegistry::class.declaredMemberProperties
        dmp.filter { it.get(BlockRegistry) is Block }.forEach {
            val id = SpectralUtilities.id(it.name.lowercase())
            val block = it.get(BlockRegistry) as Block
            Registry.register(Registries.BLOCK, id, block)
            Registry.register(Registries.ITEM, id, BlockItem(block, block.itemSettings))
        }
        
        dmp.filter { it.get(BlockRegistry) is CrystallarieumMaterial }.let{it as List<KProperty1<BlockRegistry,CrystallarieumMaterial>>}.forEach{it.register()}
        
        NoxwoodDeco()
    }
    
    @Environment(EnvType.CLIENT)
    public static void registerClient(){
        NoxwoodDeco.LAMPS.forEach {
            BlockRenderLayerMap.INSTANCE.putBlock(it.second, RenderType.cutout());
        }
    }

}