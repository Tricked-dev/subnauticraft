package dev.tricked.subnauticraft

import dev.tricked.subnauticraft.features.AcidMushroom
import dev.tricked.subnauticraft.features.Oxygen
import dev.tricked.subnauticraft.features.ServerListPingHandler
import dev.tricked.subnauticraft.features.Weight
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.extras.lan.OpenToLAN
import net.minestom.server.extras.lan.OpenToLANConfig
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.generator.GenerationUnit
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.network.packet.server.play.DeclareRecipesPacket.Ingredient
import net.minestom.server.recipe.ShapelessRecipe
import net.minestom.server.utils.NamespaceID
import net.minestom.server.utils.time.TimeUnit
import net.minestom.server.world.DimensionType
import java.time.Duration


class Titanium : ShapelessRecipe(
    "titanium",
    "subnauticraft",
    listOf(
        Ingredient(
            listOf(
                ItemStack.of(Material.IRON_BARS).withDisplayName(Component.text("Titanium", NamedTextColor.DARK_PURPLE))
            )
        )
    ),
    ItemStack.of(Material.IRON_INGOT).withDisplayName(Component.text("Titanium Ingot", NamedTextColor.DARK_PURPLE))
) {
    override fun shouldShow(player: Player): Boolean {
        return true
    }
}


fun main(args: Array<String>) {
    System.setProperty("minestom.use-new-chunk-sending", "true")


    val minecraftServer = MinecraftServer.init()


    val recipeManager = MinecraftServer.getRecipeManager()
    recipeManager.addRecipe(Titanium())




    val eventHandler = MinecraftServer.getGlobalEventHandler()
    eventHandler.addChild(Oxygen.events)
    eventHandler.addChild(AcidMushroom.events)


    val fullbright = DimensionType.builder(NamespaceID.from("minestom:subnauticraft"))
        .ambientLight(1.0f)
        .build()



    MinecraftServer.getDimensionTypeManager().addDimension(fullbright)

    val instanceManager = MinecraftServer.getInstanceManager()

    val instanceContainer = instanceManager.createInstanceContainer(fullbright)

        var done =false;

    instanceContainer.setGenerator { unit: GenerationUnit ->
        unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK)
        if(!done) {
            unit.modifier().fill(Pos(0.0, 30.0, 0.0), Pos(5.0, 40.0, 5.0), Block.WATER)
done=true;
        }
    }



    instanceContainer.worldBorder.setDiameter(50.0, 20)

    instanceContainer.setBlock(
        Pos(1.0, 40.0, 1.0), Block.GLOWSTONE
    )
    instanceContainer.setBlock(
        Pos(1.0, 41.0, 1.0), Block.CRAFTING_TABLE
    )

    instanceContainer.setBlock(
        Pos(6.0, 40.0, 3.0), Block.CACTUS
    )
    instanceContainer.setBlock(Pos(2.0,30.0,2.0), Block.TUBE_CORAL)
    instanceContainer.setBlock(Pos(3.0,30.0,2.0), Block.TUBE_CORAL)
    instanceContainer.setBlock(Pos(4.0,30.0,2.0), Block.TUBE_CORAL)
    instanceContainer.setBlock(Pos(4.0,30.0,3.0), Block.TUBE_CORAL)
    instanceContainer.setBlock(Pos(4.0,30.0,4.0), Block.TUBE_CORAL)
    instanceContainer.setBlock(Pos(3.0,30.0,3.0), Block.TUBE_CORAL)

    MinecraftServer.getGlobalEventHandler().addListener(
        ServerListPingEvent::class.java
    ) { event: ServerListPingEvent ->
        ServerListPingHandler.handlePingEvent(event)
    }

    val handler = EventNode.all("subnauticraft")
        .addListener<PlayerLoginEvent>(PlayerLoginEvent::class.java) { event: PlayerLoginEvent ->
            event.setSpawningInstance(instanceContainer)
            event.player.gameMode = GameMode.CREATIVE
            event.player.respawnPoint = Pos(0.0, 42.0, 0.0)
        }.addListener(PlayerBlockInteractEvent::class.java) { event: PlayerBlockInteractEvent ->
            if (event.block == Block.CRAFTING_TABLE) {
                event.player.openInventory(Inventory(InventoryType.CRAFTING, "Fabricator (Open Book)"))
            }
        }
    eventHandler.addChild(Weight.events)
    eventHandler.addChild(handler)

    OpenToLAN.open(OpenToLANConfig().eventCallDelay(Duration.of(1, TimeUnit.DAY)))


    minecraftServer.start("0.0.0.0", 25565)
}


//
//object PlayerInit {
//    private val inventory: Inventory? = null
//    private val DEMO_NODE: EventNode<Event> = EventNode.all("demo")
//        .addListener<EntityAttackEvent>(EntityAttackEvent::class.java) { event: EntityAttackEvent ->
//            val source: Entity = event.entity
//            val entity: Entity = event.target
//            entity.takeKnockback(
//                0.4f,
//                sin(source.getPosition().yaw() * 0.017453292),
//                -cos(source.getPosition().yaw() * 0.017453292)
//            )
//            if (entity is Player) {
//                val target: Player = entity as Player
//                target.damage(DamageType.fromEntity(source), 5)
//            }
//            if (source is Player) {
//                (source as Player).sendMessage("You attacked something!")
//            }
//        }
//        .addListener<PlayerDeathEvent>(
//            PlayerDeathEvent::class.java
//        ) { event: PlayerDeathEvent -> event.chatMessage = Component.text("custom death message") }
//        .addListener<PickupItemEvent>(PickupItemEvent::class.java) { event: PickupItemEvent ->
//            val entity: Entity = event.livingEntity
//            if (entity is Player) {
//                // Cancel event if player does not have enough inventory space
//                val itemStack = event.itemEntity.itemStack
//                event.isCancelled = !(entity as Player).getInventory().addItemStack(itemStack)
//            }
//        }
//        .addListener<ItemDropEvent>(ItemDropEvent::class.java) { event: ItemDropEvent ->
//            val player: Player = event.player
//            val droppedItem = event.itemStack
//            val playerPos: Pos = player.getPosition()
//            val itemEntity = ItemEntity(droppedItem)
//            itemEntity.setPickupDelay(Duration.of(500, TimeUnit.MILLISECOND))
//            itemEntity.setInstance(player.getInstance(), playerPos.withY { y -> y + 1.5 })
//            val velocity: Vec = playerPos.direction().mul(6)
//            itemEntity.setVelocity(velocity)
//        }
//        .addListener<PlayerDisconnectEvent>(
//            PlayerDisconnectEvent::class.java
//        ) { event: PlayerDisconnectEvent ->
//            println(
//                "DISCONNECTION " + event.player.username
//            )
//        }
//        .addListener<PlayerLoginEvent>(PlayerLoginEvent::class.java) { event: PlayerLoginEvent ->
//            val player: Player = event.player
//            val instances: Set<Instance?> =
//                MinecraftServer.getInstanceManager().instances
//            val instance: Instance? =
//                instances.stream().skip(Random().nextInt(instances.size).toLong()).findFirst().orElse(null)
//            event.setSpawningInstance(instance)
//            val x =
//                (abs(
//                    ThreadLocalRandom.current().nextInt().toDouble()
//                ) % 500 - 250).toInt()
//            val z =
//                (abs(
//                    ThreadLocalRandom.current().nextInt().toDouble()
//                ) % 500 - 250).toInt()
//            player.setRespawnPoint(Pos(0, 40f, 0))
//        }
//        .addListener<PlayerSpawnEvent>(PlayerSpawnEvent::class.java) { event: PlayerSpawnEvent ->
//            val player: Player = event.player
//            player.setGameMode(GameMode.CREATIVE)
//            player.setPermissionLevel(4)
//            val itemStack = ItemStack.builder(Material.STONE)
//                .amount(64)
//                .meta { itemMetaBuilder: ItemMeta.Builder ->
//                    itemMetaBuilder.canPlaceOn(java.util.Set.of<Block>(Block.STONE))
//                        .canDestroy(java.util.Set.of<Block>(Block.DIAMOND_ORE))
//                }
//                .build()
//            player.getInventory().addItemStack(itemStack)
//            val bundle = ItemStack.builder(Material.BUNDLE)
//                .meta(
//                    BundleMeta::class.java
//                ) { bundleMetaBuilder: BundleMeta.Builder ->
//                    bundleMetaBuilder.addItem(ItemStack.of(Material.DIAMOND, 5))
//                    bundleMetaBuilder.addItem(ItemStack.of(Material.RABBIT_FOOT, 5))
//                }
//                .build()
//            player.getInventory().addItemStack(bundle)
//        }
//        .addListener<PlayerPacketOutEvent>(
//            PlayerPacketOutEvent::class.java
//        ) { event: PlayerPacketOutEvent? -> }
//        .addListener<PlayerPacketEvent>(
//            PlayerPacketEvent::class.java
//        ) { event: PlayerPacketEvent? -> }
//        .addListener<PlayerUseItemOnBlockEvent>(
//            PlayerUseItemOnBlockEvent::class.java
//        ) { event: PlayerUseItemOnBlockEvent ->
//            if (event.hand != Player.Hand.MAIN) return@addListener
//            val itemStack = event.itemStack
//            var block = event.getInstance().getBlock(event.position)
//            event.player.sendMessage("MESSAGE " + ThreadLocalRandom.current().nextDouble())
//            if ("false" == block.getProperty("waterlogged") && itemStack.material() == Material.WATER_BUCKET) {
//                block = block.withProperty("waterlogged", "true")
//                println("SET WATERLOGGER")
//            } else if ("true" == block.getProperty("waterlogged") && itemStack.material() == Material.BUCKET) {
//                block = block.withProperty("waterlogged", "false")
//                println("SET NOT WATERLOGGED")
//            } else return@addListener
//            event.getInstance().setBlock(event.position, block)
//        }
//        .addListener<PlayerBlockPlaceEvent>(
//            PlayerBlockPlaceEvent::class.java
//        ) { event: PlayerBlockPlaceEvent? -> }
//        .addListener<PlayerBlockInteractEvent>(
//            PlayerBlockInteractEvent::class.java
//        ) { event: PlayerBlockInteractEvent ->
//            var block = event.block
//            val rawOpenProp = block.getProperty("open") ?: return@addListener
//            block = block.withProperty("open", !rawOpenProp.toBoolean().toString())
//            event.getInstance().setBlock(event.blockPosition, block)
//        }
//
//    init {
//        val instanceManager = MinecraftServer.getInstanceManager()
//        val instanceContainer = instanceManager.createInstanceContainer(DimensionType.OVERWORLD)
//        instanceContainer.setGenerator { unit: GenerationUnit ->
//            unit.modifier().fillHeight(0, 40, Block.STONE)
//        }
//        instanceContainer.setChunkSupplier { instance: Instance?, chunkX: Int, chunkZ: Int ->
//            LightingChunk(
//                instance!!, chunkX, chunkZ
//            )
//        }
//
//        // System.out.println("start");
//        // var chunks = new ArrayList<CompletableFuture<Chunk>>();
//        // ChunkUtils.forChunksInRange(0, 0, 32, (x, z) -> chunks.add(instanceContainer.loadChunk(x, z)));
//
//        // CompletableFuture.runAsync(() -> {
//        //     CompletableFuture.allOf(chunks.toArray(CompletableFuture[]::new)).join();
//        //     System.out.println("load end");
//        // });
//        inventory = Inventory(InventoryType.CHEST_1_ROW, Component.text("Test inventory"))
//        inventory!!.setItemStack(3, ItemStack.of(Material.DIAMOND, 34))
//    }
//
//    private val LAST_TICK = AtomicReference<TickMonitor>()
//    fun init() {
//        val eventHandler = MinecraftServer.getGlobalEventHandler()
//        eventHandler.addChild(DEMO_NODE)
//        MinestomAdventure.AUTOMATIC_COMPONENT_TRANSLATION = true
//        MinestomAdventure.COMPONENT_TRANSLATOR =
//            BiFunction { c: Component?, l: Locale? -> c }
//        eventHandler.addListener(
//            ServerTickMonitorEvent::class.java
//        ) { event: ServerTickMonitorEvent ->
//            LAST_TICK.set(
//                event.tickMonitor
//            )
//        }
//        val benchmarkManager = MinecraftServer.getBenchmarkManager()
//        MinecraftServer.getSchedulerManager().buildTask {
//            val players: Collection<Player> =
//                MinecraftServer.getConnectionManager().onlinePlayers
//            if (players.isEmpty()) return@buildTask
//            var ramUsage = benchmarkManager.usedMemory
//            ramUsage = (ramUsage / 1e6).toLong() // bytes to MB
//            val tickMonitor = LAST_TICK.get()
//            val header: Component = Component.text("RAM USAGE: $ramUsage MB")
//                .append(Component.newline())
//                .append(
//                    Component.text(
//                        "TICK TIME: " + MathUtils.round(
//                            tickMonitor.tickTime,
//                            2
//                        ) + "ms"
//                    )
//                )
//                .append(Component.newline())
//                .append(
//                    Component.text(
//                        "ACQ TIME: " + MathUtils.round(
//                            tickMonitor.acquisitionTime,
//                            2
//                        ) + "ms"
//                    )
//                )
//            val footer: Component = benchmarkManager.getCpuMonitoringMessage()
//            Audiences.players().sendPlayerListHeaderAndFooter(header, footer)
//        }.repeat(10, TimeUnit.SERVER_TICK).schedule()
//    }
//}