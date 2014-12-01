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
package org.apache.activemq.spi.core.remoting;

import org.apache.activemq.api.core.Pair;
import org.apache.activemq.api.core.TransportConfiguration;
import org.apache.activemq.spi.core.protocol.RemotingConnection;

/**
 * @author Clebert Suconic
 */

public interface TopologyResponseHandler
{
   // This is sent when the server is telling the client the node is being disconnected
   void nodeDisconnected(RemotingConnection conn, String nodeID, String scaleDownTargetNodeID);

   void notifyNodeUp(long uniqueEventID,
                     final String backupGroupName,
                     final String scaleDownGroupName,
                     final String nodeName,
                     final Pair<TransportConfiguration, TransportConfiguration> connectorPair,
                     final boolean isLast);

   // This is sent when any node on the cluster topology is going down
   void notifyNodeDown(final long eventTime, final String nodeID);
}