package com.oyosite.ticon.specutils.block
		;

import com.oyosite.ticon.specutils.*;
import de.dafuqs.spectrum.blocks.decoration.*;
import de.dafuqs.spectrum.registries.*;
import de.dafuqs.spectrum.registries.SpectrumItems.*;
import net.minecraft.core.registries.*;

import java.util.*;

public class NoxwoodDeco {
	
	public enum NoxwoodType {
		CHESTNUT,
		SLATE,
		EBONY,
		IVORY;
		
		fun forEachCombination(block:(Pair<NoxwoodType, NoxwoodType>)->Unit)
		
		{
			for (type1 in entries) for (type2 in entries) block(type1 to type2)
		}
		
		val combinations = mutableListOf < Pair < NoxwoodType, NoxwoodType>>().
		
		also {
			forEachCombination(it::add)
		}
		
		val nonDuplicateCombinations = combinations.filter(nonDuplicate)
		
		fun<T> mapCombinations(transform:(Pair<NoxwoodType, NoxwoodType>)->T)=combinations.map(transform)
	}
	
	@Override
	public String toString () {
		return name.toLowercase();
		
	}

private val nonDuplicate:(Pair<*,*>)->Boolean ={it.first!=it.second}


public static LAMPS =NoxwoodType.nonDuplicateCombinations.map {
	idFor(it, "lamp") to noxwoodLampBlock ()
}.toTypedArray()

public static LANTERNS =NoxwoodType.nonDuplicateCombinations.map {
	idFor(it, "lantern") to noxwoodLanternBlock ()
}.toTypedArray()

public static LIGHTS =NoxwoodType.nonDuplicateCombinations.map {
	idFor(it, "light") to noxwoodLightBlock ()
}.toTypedArray()

public static List ALL_DECO = mutableListOf( * LIGHTS, *LAMPS,*LANTERNS)

static {
	ALL_DECO.forEach {
		(id, block) ->
				net.minecraft.core.Registry.register(net.minecraft.core.registries.Registries.BLOCK, id, block)
		net.minecraft.core.Registry.register(Registries.ITEM, id, BlockItem(block, IS.of()))
	}
	
	fun idFor (pair:Pair<NoxwoodType, NoxwoodType>,blockName:
	String) =SpectralUtilities.id("${pair.first.name.lowercase()}_${pair.second.name.lowercase()}_noxwood_$blockName")
	
	//Why are their names swapped? I have no idea.
	fun noxwoodLanternBlock
	() = RedstoneLampBlock(SpectrumBlocks.noxcap(MapColor.DULL_RED).luminance(SpectrumBlocks.LANTERN_LIGHT_PROVIDER))
	fun noxwoodLampBlock
	() = FlexLanternBlock(FabricBlockSettings.copyOf(Blocks.LANTERN).luminance(13).pistonBehavior(PistonBehavior.DESTROY))
	fun noxwoodLightBlock () = PillarBlock(SpectrumBlocks.noxcap(MapColor.DULL_RED).luminance {
		15
	})
	
}