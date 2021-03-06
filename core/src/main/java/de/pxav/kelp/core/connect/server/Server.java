package de.pxav.kelp.core.connect.server;

import com.google.common.base.Preconditions;
import de.pxav.kelp.core.connect.KelpConnectVersionTemplate;
import de.pxav.kelp.core.connect.connection.Connection;
import de.pxav.kelp.core.connect.connection.ConnectionHolder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.util.concurrent.Future;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;

/**
 * @author Etrayed
 */
public class Server implements Closeable {

  private final KelpConnectVersionTemplate versionTemplate;

  private final ServerBootstrap serverBootstrap;

  private final ConnectionHolder connectionHolder;

  private ConnectionPropertiesFactory connectionPropertiesFactory;

  public Server(KelpConnectVersionTemplate versionTemplate, ServerProperties properties) {
    this.versionTemplate = versionTemplate;
    this.serverBootstrap = properties.bootstrap;
    this.connectionHolder = new ConnectionHolder();
    this.connectionPropertiesFactory = properties.connectionPropertiesFactory;
  }

  public Future<Void> bind() {
    return serverBootstrap.childHandler(new ChannelInitializer<Channel>() {

      @Override
      protected void initChannel(Channel channel) throws Exception {
        connectionHolder.register(new Connection(versionTemplate, connectionHolder, connectionPropertiesFactory
          .createProperties(Server.this, (InetSocketAddress) channel.remoteAddress()), Server.this, channel));
      }
    }).bind();
  }

  public boolean isReady() {
    return !serverBootstrap.group().isShutdown();
  }

  @Override
  public void close() {
    connectionHolder.closeAll();

    serverBootstrap.group().shutdownGracefully();
    serverBootstrap.childGroup().shutdownGracefully();
  }

  public void setConnectionPropertiesFactory(ConnectionPropertiesFactory propertiesFactory) {
    Preconditions.checkNotNull(propertiesFactory, "propertiesFactory cannot be null.");

    this.connectionPropertiesFactory = propertiesFactory;
  }

  public ConnectionPropertiesFactory getConnectionPropertiesFactory() {
    return connectionPropertiesFactory;
  }

  public boolean isRegistered(Connection connection) {
    return connectionHolder.isRegistered(connection);
  }

  public List<Connection> getRegisteredConnections() {
    return Collections.unmodifiableList(connectionHolder.getRegisteredConnections());
  }
}
