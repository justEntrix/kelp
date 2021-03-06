package de.pxav.kelp.core.npc.version;

import de.pxav.kelp.core.application.KelpVersionTemplate;
import de.pxav.kelp.core.npc.KelpNpc;
import de.pxav.kelp.core.npc.KelpNpcMeta;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * This version implementation is responsible for all
 * version specific tasks of an NPC. An example for
 * this would be sending the spawn packets.
 *
 * For identification of NPCs and communication with the
 * core module, NPC version implementations use {@code KelpNpcMeta}.
 *
 * @see KelpNpcMeta
 * @author pxav
 */
@KelpVersionTemplate
public abstract class NpcVersionTemplate {

  /**
   * Sends the spawn packets for the NPC to the given player, so
   * that they can see it.
   *
   * @param npc     An instance of the NPC you want to spawn.
   * @param player  The player who should see the NPC.
   * @return        The metadata of the NPC containing information, which
   *                is needed to identify the NPC again later.
   */
  public abstract KelpNpcMeta spawnNpc(KelpNpc npc, Player player);

  /**
   * Despawns an NPC for the given player, so that they can no
   * longer see it. It also remove the title lines of the NPC.
   *
   * @param npc     An instance of the NPC, which should be removed.
   * @param player  The player for whom the NPC should be despawned.
   */
  public abstract void deSpawn(KelpNpc npc, Player player);

  public abstract void walkTo(KelpNpc npc, Player player, Location target, float yaw, float pitch);

  /**
   * Refreshes the NPC data and all meta packets. When the sneak state
   * of an NPC has updated for example, then this method has to be
   * called to send this update to the player as well.
   *
   * @param npc     An instance of the NPC, which should be refreshed.
   * @param player  The player, who should see the updates.
   *                Note: You must have spawned the NPC before to
   *                this very player.
   */
  public abstract void refresh(KelpNpc npc, Player player);

}
