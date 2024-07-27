package net.illuc.kontraption.network

object KontraptionVSGamePackets { // yoinkered from the vs2 github

    fun register() {
        /*KontraptionPacketPlayerDriving::class.register()
        PacketStopChunkUpdates        ::class.register()
        PacketRestartChunkUpdates     ::class.register()
        PacketSyncVSEntityTypes       ::class.register()*/
    }

    fun registerHandlers() {
        /*KontraptionPacketPlayerDriving::class.registerServerHandler { driving, iPlayer ->
            println("registeringHandlers")
            val player = (iPlayer as MinecraftPlayer).player as ServerPlayer
            val seat = player.vehicle as? KontraptionShipMountingEntity
                    ?: return@registerServerHandler
            if (true) {//(seat.isController) {
                println("seat is controller :thumbsup")
                val ship = KontraptionVSUtils.getShipObjectManagingPos(seat.level, seat.blockPosition()) as? LoadedServerShip
                        ?: return@registerServerHandler

                val attachment: KontraptionSeatedControllingPlayer = ship.getAttachment()
                        ?: KontraptionSeatedControllingPlayer(seat.direction.opposite).apply { ship.setAttachment(this) }

                attachment.forwardImpulse   = driving.impulse.z
                attachment.leftImpulse      = driving.impulse.x
                attachment.upImpulse        = driving.impulse.y
                attachment.pitch            = driving.rotation.x
                attachment.yaw              = driving.rotation.y
                attachment.roll             = driving.rotation.z
                println(attachment.yaw)
            }
        }*/

        // Syncs the entity handlers to the client
        /* do we actually need it? TODO: uncomment and fix if stuff breakers
        PacketSyncVSEntityTypes::class.registerClientHandler { syncEntities ->
            syncEntities.entity2Handler.forEach { (id, handler) ->
                VSEntityManager.pair(
                        RegistryObject<EntityType>.byId(id),
                        ResourceLocation.tryParse(handler)?.let { VSEntityManager.getHandler(it) }
                                ?: throw IllegalStateException("No handler: $handler")
                )
            }
        }*/
    }
}
