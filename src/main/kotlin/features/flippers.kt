package dev.tricked.subnauticraft.features

import dev.tricked.subnauticraft.CraftableItem
import dev.tricked.subnauticraft.Item
import dev.tricked.subnauticraft.Items
import dev.tricked.subnauticraft.Utils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.attribute.Attribute
import net.minestom.server.event.EventNode
import net.minestom.server.event.inventory.InventoryCloseEvent
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.item.Enchantment
import net.minestom.server.item.ItemHideFlag
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag

abstract class Fin : Item() {
    abstract val speed: Int

    override fun create(): ItemStack {
        return Utils.createItem(
            material,
            name,
            lore(speed).toList(),
            weight
        ).meta { meta ->
            meta.enchantment(Enchantment.DEPTH_STRIDER, 3)
            meta.hideFlag(ItemHideFlag.HIDE_ENCHANTS)
        }.build()
    }

    fun lore(speed: Int): Array<out Component> {
        return arrayOf(*lore, Component.text("Speed: ${100 + speed}%"))
    }
}


object UltraGlideFins : Fin(), CraftableItem {
    override val material = Material.DIAMOND_BOOTS
    override val name = Component.text("Ultra Glide Fins", NamedTextColor.RED)
    override val weight = 1
    override val id = "ultraglidefins"

    override val speed = 30
    override val requiredItems = listOf(Items.FINS, Items.TITANIUM)
}

object Fins : Fin() {
    override val material = Material.IRON_BOOTS
    override val name = Component.text("Fins", NamedTextColor.RED)
    override val weight = 1
    override val id = "fins"

    override val speed = 15
}

object SwimChargeFins : Fin() {
    override val material = Material.GOLDEN_BOOTS
    override val name = Component.text("Swim Charge Fins", NamedTextColor.DARK_PURPLE)
    override val lore = arrayOf(Component.text("Generated energy from swimming"))
    override val weight = 1
    override val id = "swimchargefins"

    override val speed = 15
}

object Flippers {
    val flipperSpeedTag = Tag.Integer("flipperSpeed").defaultValue(0)
    val events = EventNode.all("mushroom")
        .addListener(PlayerMoveEvent::class.java) { event: PlayerMoveEvent ->
            val player = event.player
            val swimming = player.instance.getBlock(player.position).isLiquid
            if (swimming) {
                if (player.hasTag(flipperSpeedTag)) {
                    val speed = player.getTag(flipperSpeedTag)
                    player.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = 0.1f + speed.toFloat() / 1000
                }
            } else {
                player.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = 0.1f
            }

        }
        .addListener(PlayerUseItemEvent::class.java) { event ->
            val boots = event.player.inventory.getItemStack(44)
            val item = Items.fromMaterial(boots.material())
            if (item is Fin) {
                event.player.setTag(flipperSpeedTag, item.speed)
            } else {
                event.player.setTag(flipperSpeedTag, 0)
            }
        }
        .addListener(InventoryCloseEvent::class.java) { event ->
            val boots = event.player.inventory.getItemStack(44)
            val item = Items.fromMaterial(boots.material())
            if (item is Fin) {
                event.player.setTag(flipperSpeedTag, item.speed)
            } else {
                event.player.setTag(flipperSpeedTag, 0)
            }
        }
}