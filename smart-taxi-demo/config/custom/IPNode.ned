//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License
// along with this program.  If not, see http://www.gnu.org/licenses/.
// 

package manet;

import inet.nodes.inet.WirelessHost;
import org.mixim.modules.power.battery.BatteryStats;
import org.mixim.modules.power.battery.SimpleBattery;
import inet.applications.IPingApp;
import inet.applications.ISCTPApp;
import inet.applications.ITCPApp;
import inet.applications.IUDPApp;
import inet.transport.ISCTP;
import inet.transport.ITCP;
import inet.transport.IUDP;
import inet.util.PcapRecorder;
import inet.networklayer.ipv4.RoutingTable;
import inet.networklayer.common.InterfaceTable;
import inet.mobility.contract.IMobility;
import inet.linklayer.IWirelessNic;
import inet.linklayer.IWiredNic;
import inet.linklayer.IExternalNic;
import inet.base.NotificationBoard;
import inet.nodes.inet.NetworkLayer;
import inet.transport.udp.UDP;
import inet.linklayer.loopback.LoopbackInterface;
//import inet.nodes.inet.StandardHost;

// This host is an example on how a host with INET network stack
// can be equipped with mobility- and wireless-modules from MiXiM. 
//
// It contains an INET network stack and a MiXiM 802.15.4 NIC (TI_CC2420)
// as well as a battery module from MiXiM which is used by the NIC.
//
// This host's NIC is able to operate in a multi-ConnectionManager
// network, i.e. more than one physical network in parallel (non-interfering).
// TODO: factor out battery into INET

module IPNode
{
     parameters:
        @display("i=device/pc2");
        @node;
        @labels(node,ethernet-node,wireless-node);
        
        //IP protoctol
        bool IPForward = default(false);  // disable routing by default
        string routingFile = default("");
        bool forwardMulticast = default(false);
        bool useEthernet = default(false);
        
    gates:
      inout ethg @labels(EtherFrame-conn);
      inout direct[];
    submodules:
        //Application
        appl: JDEECoApplication {
            parameters:
                @display("p=70,30;i=app");
        }
        
        udp: UDP if useEthernet {
            parameters:
                @display("p=329,141");
        }
        
        //Network layer
        networkLayer: NetworkLayer if useEthernet {
            parameters:
                proxyARP = true;
                @display("p=329,287;q=queue");
        }
        
        routingTable: RoutingTable {
            parameters:
                @display("p=53,225;is=s");
                IPForward = IPForward;
                forwardMulticast = forwardMulticast;
                routingFile = routingFile;
        }
        
        //linklayer
        interfaceTable: InterfaceTable {
            parameters:
                @display("p=53,287;is=s");
        }
        
        lo0: LoopbackInterface {
            @display("p=78,406");
        } 
        
        eth: <default("EthernetInterface")> like IWiredNic if useEthernet {
            parameters:
                @display("p=368,406,row,60;q=txQueue");
        }
        
        notificationBoard: NotificationBoard {
            parameters:
                @display("p=53,155;is=s");
        }
        
    connections allowunconnected:
      networkLayer.ifOut++ --> lo0.upperLayerIn;
      lo0.upperLayerOut --> networkLayer.ifIn++;
    
      //# Connect IP branch
      ethg <--> { @display("m=s"); } <--> eth.phys;
      eth.upperLayerOut --> networkLayer.ifIn++;
      eth.upperLayerIn <-- networkLayer.ifOut++;
               
      appl.lowerLayerOut --> udp.appIn++;
      appl.lowerLayerIn <-- udp.appOut++;
      
      udp.ipOut --> networkLayer.transportIn++;
      udp.ipIn <-- networkLayer.transportOut++;
}

