package dev.tricked.subnauticraft.features

import dev.tricked.subnauticraft.CraftableItem
import dev.tricked.subnauticraft.Item
import dev.tricked.subnauticraft.Items
import dev.tricked.subnauticraft.Shapeless
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.network.packet.server.play.DeclareRecipesPacket
import net.minestom.server.network.packet.server.play.DeclareRecipesPacket.Ingredient
import net.minestom.server.recipe.ShapedRecipe
import net.minestom.server.recipe.ShapelessRecipe
import java.util.List


object Titanium : Item() {
    override val material = Material.IRON_BARS
    override val name = Component.text("Titanium", NamedTextColor.DARK_PURPLE)
    override val id = "titanium"
}

object CraftableEvents {
    var ran = false;
    val events = EventNode.all("craftable").addListener(PlayerSpawnEvent::class.java) { event ->
        if(ran) return@addListener
        ran = true
        MinecraftServer.getRecipeManager().recipes.clear()
        event.player.sendPacket(MinecraftServer.getRecipeManager().declareRecipesPacket)
        for(item in Items.entries) {

            if(item.item is CraftableItem) {
                val craftable = item.item as CraftableItem

                println("${item.item.material.name()} name: ${item.item}")
                MinecraftServer.getRecipeManager().addRecipe(
                    object : ShapelessRecipe(
                        item.item.id,
                        "subnauticraft",
                        listOf( DeclareRecipesPacket.Ingredient(listOf(ItemStack.of(Material.STONE)))),
                        ItemStack.of(item.item.material).withLore{
                            val it = mutableListOf<Component>()
                            it.add(Component.text("Required items:"))
                            for(i in craftable.requiredItems) {
                                it.add(Component.text("- ${i.name}"))
                            };
                            it
                        },


                    ) {
                        override fun shouldShow(player: Player): Boolean {
                            return true
                        }
                    }
                )
            }
        }
        event.player.sendPacket(MinecraftServer.getRecipeManager().declareRecipesPacket)
    }
// class Shapeless  : ShapelessRecipe(
//    "minecraft:sticks",
//    "sticks",
//    List.of<Ingredient>(Ingredient(List.of<ItemStack>(ItemStack.of(Material.STONE)))),
//    ItemStack.of(Material.STICK)
//) {
//    override fun shouldShow(player: Player): Boolean {
//        return true
//    }
//}
//    var ran = false;
//    val events = EventNode.all("craftable").addListener(PlayerSpawnEvent::class.java) { event ->
//        if(ran) return@addListener
//        ran = true
////        MinecraftServer.getRecipeManager().recipes.clear()
////        event.player.sendPacket(MinecraftServer.getRecipeManager().declareRecipesPacket)
////        MinecraftServer.getRecipeManager().addRecipe(Shapeless())
////        for(item in Items.values()) {
////
////            if(item.item is CraftableItem) {
////                val craftable = item.item as CraftableItem
////
////                println("${item.item.material.name()} name: ${item.item}")
////                MinecraftServer.getRecipeManager().addRecipe(
////                    object : ShapedRecipe(
////                        item.item.material.name(),3,3,
////                        "Craft ${item.item.name}",
////                        listOf( DeclareRecipesPacket.Ingredient(listOf(ItemStack.of(Material.STONE)))),
////                        ItemStack.of(item.item.material).withLore{
////                            val it = mutableListOf<Component>()
////                            it.add(Component.text("Required items:"))
////                            for(i in craftable.requiredItems) {
////                                it.add(Component.text("- ${i.name}"))
////                            };
////                            it
////                        },
////
////
////                        ) {
////                        override fun shouldShow(player: Player): Boolean {
////                            return true
////                        }
////                    }
////                )
////            }
////        }
////        event.player.sendPacket(MinecraftServer.getRecipeManager().declareRecipesPacket)
//    }
}