package dev.tricked.subnauticraft

import dev.tricked.subnauticraft.features.*
import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import net.minestom.server.event.item.ItemDropEvent
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.minestom.server.event.player.PlayerSwapItemEvent
import net.minestom.server.item.ItemMeta
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

enum class Items(val item:Item) {
    FINS(Fins),
    ULTRA_GLIDE_FINS(UltraGlideFins),
    SWIM_CHARGE_FINS(SwimChargeFins),

    LASER_CUTTER(LaserCutter),
    REPAIR_TOOL(RepairTool),
    SCANNER(Scanner),

    LANTERN_FRUIT(LanternFruit),

    TANK(StandardTank),
    DOUBLE_TANK(HighCapacityTank),
    PLASTIC_TANK(LightWeightHighCapacityTank),
    HIGH_CAPACITY_TANK(UltraHighCapacityTank);
    companion object {
        @JvmStatic
        fun fromMaterial(material: Material) :Item?{
            for(item in Items.values()) {
                if(item.item.material == material) {
                    return  item.item
                }
            }
            return null
        }
    }

}

abstract class Item {
    abstract val material: Material
    abstract val name: Component
    open val lore: Array<out Component> = arrayOf()
    open val weight: Int = 1
    abstract val id: String
    open fun create(): ItemStack {
        return Utils.createItem(
            material,
            name,
            lore,
            weight
        ).meta(this::meta).build()
    }

    open fun meta(builder: ItemMeta.Builder) {

    }

    fun detect(player: Player): Boolean {
        return detect(player.inventory.itemInMainHand)
    }

    fun detect(item: ItemStack): Boolean {
        return detect(item.material())
    }

    fun detect(material: Material): Boolean {
        return material == this.material
    }
}

interface InteractableItem {
    fun entityInteract(event: PlayerEntityInteractEvent) {};
    fun blockInteract(event: PlayerBlockInteractEvent) {};
    fun swap(event: PlayerSwapItemEvent) {};
    fun drop(event: ItemDropEvent) {};
    fun use(event: PlayerEntityInteractEvent) {};
}

interface FoodItem  {
    val nurishment: Int
}