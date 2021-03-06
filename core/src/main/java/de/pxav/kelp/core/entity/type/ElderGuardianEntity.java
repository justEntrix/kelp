package de.pxav.kelp.core.entity.type;

import de.pxav.kelp.core.entity.KelpEntity;
import de.pxav.kelp.core.entity.KelpEntityType;
import de.pxav.kelp.core.entity.version.EntityVersionTemplate;
import org.bukkit.Location;

/**
 * A class description goes here.
 *
 * @author pxav
 */
public class ElderGuardianEntity extends KelpEntity {

  public ElderGuardianEntity() {}

  public ElderGuardianEntity(EntityVersionTemplate entityVersionTemplate, Object entity, int entityId, Location location) {
    super(entity, KelpEntityType.ELDER_GUARDIAN, location, entityId, entityVersionTemplate);
  }

}
