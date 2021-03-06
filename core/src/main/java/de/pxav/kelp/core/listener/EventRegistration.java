package de.pxav.kelp.core.listener;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import de.pxav.kelp.core.logger.KelpLogger;
import de.pxav.kelp.core.logger.LogLevel;
import de.pxav.kelp.core.reflect.MethodCriterion;
import de.pxav.kelp.core.reflect.MethodFinder;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * This class handles the bukkit-event registration in Kelp.
 *
 * Normally in bukkit you have to implement an empty {@code Listener} interface
 * and register the class via {@code PluginManager} as a listener class,
 * but by using Kelp this step becomes unnecessary.
 *
 * Listeners are automatically registered if they are simply annotated with {@code @EventHandler} and
 * you do not have to implement any interfaces anymore or register listeners manually.
 *
 * @author pxav
 */
@Singleton
public class EventRegistration {

  private final MethodFinder methodSearcher;
  private final JavaPlugin javaPlugin;
  private final Injector injector;
  private final KelpLogger logger;

  @Inject
  public EventRegistration(MethodFinder methodSearcher,
                           JavaPlugin javaPlugin,
                           Injector injector,
                           KelpLogger logger) {
    this.methodSearcher = methodSearcher;
    this.javaPlugin = javaPlugin;
    this.injector = injector;
    this.logger = logger;
  }

  /**
   * This method searches for all methods annotated with
   * {@code EventHandler} and registers the corresponding listener.
   *
   * Execute this method on every application startup to register all
   * events properly.
   *
   * @param packageNames The names of the packages you want to scan.
   *                     This also includes all sub-packages of the given packages.
   * @see EventHandler
   * @see Listener
   * @see org.bukkit.plugin.PluginManager
   */
  public void initialize(String... packageNames) {
    logger.log("[EVENT] Registering event handlers in " + Arrays.toString(packageNames));
    Preconditions.checkNotNull(packageNames);
    this.methodSearcher
            .filter(packageNames, MethodCriterion.annotatedWith(EventHandler.class))
            .forEach(
                    method -> {
                      logger.log(LogLevel.DEBUG, "EventHandler '" + method.getName() + "' successfully registered.");

                      // fetch annotation metadata if an annotation was found.
                      EventHandler handler = method.getAnnotation(EventHandler.class);

                      // register the event and manually wrap it into a listener class so that it is valid
                      // for bukkit.
                      Bukkit.getPluginManager()
                              .registerEvent(
                                      ((Class<? extends Event>) method.getParameterTypes()[0]),
                                      new Listener() {},
                                      handler.priority(),
                                      (listener, event) -> {
                                        try {
                                          if (method.getParameterTypes()[0].isAssignableFrom(event.getClass())) {
                                            method.invoke(injector.getInstance(method.getDeclaringClass()), event);
                                          }
                                        } catch (IllegalAccessException | InvocationTargetException e) {
                                          e.printStackTrace();
                                        }
                                      },
                                      javaPlugin,
                                      handler.ignoreCancelled());
                    });
    logger.log("[EVENT] Registration of event handlers complete.");
  }

}
