package com.alrex.parcool.common.registry;

import com.alrex.parcool.api.Attributes;
import com.alrex.parcool.api.Effects;
import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.common.block.Blocks;
import com.alrex.parcool.common.block.TileEntities;
import com.alrex.parcool.common.entity.EntityTypes;
import com.alrex.parcool.common.item.CreativeTabs;
import com.alrex.parcool.common.item.DataComponents;
import com.alrex.parcool.common.item.Items;
import com.alrex.parcool.common.potion.Potions;

public class ModRegistries {
    public static void registerAll() {
        Attributes.registerAll();
        Effects.registerAll();
        SoundEvents.registerAll();
        Blocks.registerAll();
        TileEntities.registerAll();
        Items.registerAll();
        CreativeTabs.registerAll();
        DataComponents.registerAll();
        EntityTypes.registerAll();
        Potions.registerAll();
    }
}
