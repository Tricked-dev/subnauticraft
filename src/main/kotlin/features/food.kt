package dev.tricked.subnauticraft.features


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