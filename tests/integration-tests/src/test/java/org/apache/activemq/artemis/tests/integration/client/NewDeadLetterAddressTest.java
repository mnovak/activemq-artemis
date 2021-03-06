/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.artemis.tests.integration.client;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.tests.util.ServiceTestBase;
import org.junit.Before;
import org.junit.After;

import org.junit.Test;

import org.junit.Assert;

import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientProducer;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.server.ActiveMQServer;
import org.apache.activemq.artemis.core.server.ActiveMQServers;
import org.apache.activemq.artemis.core.settings.impl.AddressSettings;

/**
 *
 * A NewDeadLetterAddressTest
 */
public class NewDeadLetterAddressTest extends ServiceTestBase
{
   private ActiveMQServer server;

   private ClientSession clientSession;
   private ServerLocator locator;

   @Test
   public void testSendToDLAWhenNoRoute() throws Exception
   {
      SimpleString dla = new SimpleString("DLA");
      SimpleString address = new SimpleString("empty_address");
      AddressSettings addressSettings = new AddressSettings();
      addressSettings.setDeadLetterAddress(dla);
      addressSettings.setSendToDLAOnNoRoute(true);
      server.getAddressSettingsRepository().addMatch(address.toString(), addressSettings);
      SimpleString dlq = new SimpleString("DLQ1");
      clientSession.createQueue(dla, dlq, null, false);
      ClientProducer producer = clientSession.createProducer(address);
      producer.send(createTextMessage(clientSession, "heyho!"));
      clientSession.start();
      ClientConsumer clientConsumer = clientSession.createConsumer(dlq);
      ClientMessage m = clientConsumer.receive(500);
      m.acknowledge();
      Assert.assertNotNull(m);
      Assert.assertEquals(m.getBodyBuffer().readString(), "heyho!");
   }

   @Override
   @Before
   public void setUp() throws Exception
   {
      super.setUp();
      TransportConfiguration transportConfig = new TransportConfiguration(ServiceTestBase.INVM_ACCEPTOR_FACTORY);

      Configuration configuration = createDefaultConfig()
         .setSecurityEnabled(false)
         .addAcceptorConfiguration(transportConfig);
      server = addServer(ActiveMQServers.newActiveMQServer(configuration, false));
      // start the server
      server.start();
      // then we create a client as normal
      locator =
               addServerLocator(ActiveMQClient.createServerLocatorWithoutHA(new TransportConfiguration(
                  INVM_CONNECTOR_FACTORY)));
      ClientSessionFactory sessionFactory = createSessionFactory(locator);
      clientSession = sessionFactory.createSession(false, true, false);
   }

   @Override
   @After
   public void tearDown() throws Exception
   {
      if (clientSession != null)
      {
         try
         {
            clientSession.close();
         }
         catch (ActiveMQException e1)
         {
            //
         }
      }
      clientSession = null;
      super.tearDown();
   }

}
