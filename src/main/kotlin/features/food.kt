package dev.tricked.subnauticraft.features

import dev.tricked.subnauticraft.Utils
import dev.tricked.subnauticraft.particle
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerEatEvent
import net.minestom.server.event.player.PlayerPreEatEvent
import net.minestom.server.item.ItemStack
import world.cepi.particle.Particle
import world.cepi.particle.ParticleType
import world.cepi.particle.data.OffsetAndSpeed
import world.cepi.particle.extra.Dust
import world.cepi.particle.extra.Item



//object Food {
//    val events = EventNode.all("food").addListener(
//        PlayerPreEatEvent::class.java
//    ) {event ->
//        event.isCancelled = true
//
//        val nutrition  = event.itemStack.getTag(Utils.nutishmentTag)
//        if(nutrition == null) return@addListener
//
//        event.player.food = (event.player.food + nutrition).coerceAtMost(20)
//
//        if (event.hand == Player.Hand.OFF) {
//            event.player.inventory.itemInOffHand = ItemStack.AIR
//        } else {
//            event.player.inventory.itemInMainHand = ItemStack.AIR
//        }
//
//        val particle = Particle.particle(
//            type = ParticleType.ITEM,
//            count = 500,
//            data = OffsetAndSpeed(0f, 2f, 0f, 2f),
//            extraData = Item(event.itemStack)
//        )
//
//        event.player.particle(
//            particle,
//           event.player.position.add(0.0,1.5,0.0)
//        )
//    }
//
//}