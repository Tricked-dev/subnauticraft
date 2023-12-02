package dev.tricked.subnauticraft.features

import dev.tricked.subnauticraft.Utils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag


object RepairTool {
    val repairToolTag = Tag.Boolean("repairTool")
    val events = EventNode.all("trapdoors")
        .addListener(PlayerLoginEvent::class.java) {event->
            event.player.inventory.addItemStack(
                with(
                    Utils.createItem(
                        Material.IRON_AXE,
                        Component.text("Repair Tool", NamedTextColor.DARK_PURPLE),
                        arrayOf(Component.text("Right click to repair doors")),
                        1
                    )
                ) {
                    setTag(repairToolTag, true)
                    build()
                }
            )
        }
        .addListener(PlayerBlockInteractEvent::class.java) {event->
            println("123!")
            if(!event.player.inventory.itemInMainHand.hasTag(repairToolTag)) return@addListener

            if(event.block.name() == "minecraft:tripwire_hook") {
                println("YupYup")
                println(" ${event.blockFace}")


                val offset = 1.0
                var blockPosition = event.blockPosition
                var face = event.blockFace;

                when (face) {
                    BlockFace.NORTH -> blockPosition = blockPosition.add(0.0, 0.0, offset)
                    BlockFace.SOUTH -> blockPosition = blockPosition.add(0.0, 0.0, -offset)
                    BlockFace.EAST -> blockPosition = blockPosition.add(offset, 0.0, 0.0)
                    BlockFace.WEST -> blockPosition = blockPosition.add(-offset, 0.0, 0.0)
                    else -> return@addListener
                }

                var possibleLeft = blockPosition
                var possibleRight = blockPosition

                when (face) {
                    BlockFace.NORTH -> {

                        possibleLeft=possibleLeft.add(-offset, 0.0, 0.0)
                        possibleRight= possibleRight.add(offset, 0.0, 0.0)
                    }
                    BlockFace.SOUTH -> {

                        possibleLeft=possibleLeft.add(-offset, 0.0, 0.0)
                        possibleRight= possibleRight.add(offset, 0.0, 0.0)
                    }
                    BlockFace.EAST -> {
                        possibleLeft=possibleLeft.add(0.0, 0.0, -offset)
                        possibleRight= possibleRight.add(0.0, 0.0, offset)
                    }
                    BlockFace.WEST -> {
                        possibleLeft=possibleLeft.add(0.0, 0.0, -offset)
                        possibleRight= possibleRight.add(0.0, 0.0, offset)
                    }
                    else -> return@addListener
                }

                val blockLeft = event.instance.getBlock(possibleLeft)
                val blockRight = event.instance.getBlock(possibleRight)
                print("$blockLeft $possibleLeft $blockRight $possibleRight")
            }

    }

}