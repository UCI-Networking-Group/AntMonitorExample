/*
 *  This file is part of AntMonitor <https://athinagroup.eng.uci.edu/projects/antmonitor/>.
 *  Copyright (C) 2018 Anastasia Shuba and the UCI Networking Group
 *  <https://athinagroup.eng.uci.edu>, University of California, Irvine.
 *
 *  AntMonitor is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  AntMonitor is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with AntMonitor. If not, see <http://www.gnu.org/licenses/>.
 */

package edu.uci.calit2.antexample;

import android.content.Context;

import edu.uci.calit2.antmonitor.lib.logging.ConnectionValue;
import edu.uci.calit2.antmonitor.lib.logging.PacketConsumer;
import edu.uci.calit2.antmonitor.lib.logging.PacketProcessor.TrafficType;
import edu.uci.calit2.antmonitor.lib.util.PacketDumpInfo;

/**
 * @author Anastasia Shuba
 */
public class ExamplePacketConsumer extends PacketConsumer {
    
    public ExamplePacketConsumer(Context context, TrafficType trafficType, String userID) {
        super(context, trafficType, userID);
    }

    @Override
    protected void consumePacket(PacketDumpInfo packetDumpInfo) {
        ConnectionValue cv = mapPacketToApp(packetDumpInfo);
        log(packetDumpInfo, cv.getAppName());
    }
}
