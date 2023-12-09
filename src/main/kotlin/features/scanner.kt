package dev.tricked.subnauticraft.features

import dev.tricked.subnauticraft.InteractableItem
import dev.tricked.subnauticraft.Item
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import net.minestom.server.timer.TaskSchedule

object Scanner : Item(), InteractableItem {
    override val material = Material.IRON_SWORD
    override val name = Component.text("Scanner", NamedTextColor.DARK_PURPLE)
    override val lore = arrayOf(Component.text("Right click to scan"))
    override val weight = 1
    override val id = "scanner"

    val scanningLeft = Tag.Integer("scanningLeft").defaultValue(20)
    val researchList = Tag.String("researchList").defaultValue("")

    override fun entityInteract(event: PlayerEntityInteractEvent) {
        val resarchList = event.player.getTag(researchList).split(",")

        val entity = event.target;
        val left = event.target.getTag(scanningLeft);

        if (left == 0 || resarchList.contains(entity.entityType.name())) {
            event.player.sendActionBar(
                Component.text(
                    "${entity.entityType.name()} has already been researched",
                    NamedTextColor.RED
                )
            )
            return
        }


        if (left % 2 == 0) {
            entity.entityMeta.isHasGlowingEffect = true
            event.instance.scheduler().scheduleTask({
                entity.entityMeta.isHasGlowingEffect = false
            }, TaskSchedule.tick(2), TaskSchedule.stop())
        }

        if (left == 1) {
            entity.entityMeta.isHasGlowingEffect = true
            event.instance.scheduler().scheduleTask({
                entity.entityMeta.isHasGlowingEffect = false
            }, TaskSchedule.tick(50), TaskSchedule.stop())
        }

        entity.setTag(scanningLeft, left - 1)

        if (left - 1 == 0) {
            event.player.sendActionBar(
                Component.text("Scanning completed researched ${entity.entityType.name()}", NamedTextColor.GREEN)
            )
            event.player.setTag(researchList, "${event.player.getTag(researchList)},${entity.entityType.name()}")
        } else {
            event.player.sendActionBar(
                Component.text("Scanning... ${entity.entityType.name()}, time left: ${left - 1}", NamedTextColor.GREEN)
            )
        }
    }
}

