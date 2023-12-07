package dev.tricked.subnauticraft.features

import dev.tricked.subnauticraft.Utils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.attribute.Attribute
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.item.Material
import net.minestom.server.potion.Potion
import net.minestom.server.potion.PotionEffect
import net.minestom.server.tag.Tag
import net.minestom.server.timer.TaskSchedule

object Scanner {
    val scannerTag = Tag.Boolean("scanner")
    val scanningLeft = Tag.Integer("scanningLeft").defaultValue(20)
    val researchList = Tag.String("researchList").defaultValue("")
    val events = EventNode.all("scanner").addListener(PlayerSpawnEvent::class.java) { event ->
        event.player.inventory.addItemStack(
                Utils.createItem(
                    Material.IRON_SWORD,
                    Component.text("Scanner", NamedTextColor.DARK_PURPLE),
                    arrayOf(Component.text("Right click to scan")),
                    1
                ).meta { m->
                        m.set(scannerTag, true)
                }.build()
        )
    }.addListener(PlayerEntityInteractEvent::class.java) { event ->
        if (!event.player.inventory.itemInMainHand.hasTag(scannerTag)) return@addListener
        val resarchList = event.player.getTag(researchList).split(",")


        val entity = event.target;
        val left = event.target.getTag(scanningLeft);

        if(left == 0 || resarchList.contains(entity.entityType.name())) {
            event.player.sendActionBar(Component.text("${entity.entityType.name()} has already been researched", NamedTextColor.RED))
            return@addListener
        }


        if (left % 2 == 0) {
            entity.entityMeta.isHasGlowingEffect = true
            event.instance.scheduler().scheduleTask( {
                entity.entityMeta.isHasGlowingEffect = false
            }, TaskSchedule.tick(2), TaskSchedule.stop())
        } else {
//            entity.entityMeta.isHasGlowingEffect = false

        }

        if (left == 1) {
            entity.entityMeta.isHasGlowingEffect = true
            event.instance.scheduler().scheduleTask( {
                entity.entityMeta.isHasGlowingEffect = false
            }, TaskSchedule.tick(50), TaskSchedule.stop())
        }

        entity.setTag(scanningLeft, left - 1)

//        event.player.level = left -1;
        if(left-1 == 0) {
            event.player.sendActionBar(
                Component.text("Scanning completed researched ${entity.entityType.name()}", NamedTextColor.GREEN)
            )
            event.player.setTag(researchList, "${event.player.getTag(researchList)},${entity.entityType.name()}")
        } else {
            event.player.sendActionBar(
                Component.text("Scanning... ${entity.entityType.name()}, time left: ${left-1}", NamedTextColor.GREEN)
            )
        }
    }

}