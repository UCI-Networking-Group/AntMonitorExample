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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import edu.uci.calit2.antmonitor.lib.AntMonitorActivity;
import edu.uci.calit2.antmonitor.lib.vpn.OutPacketFilter;
import edu.uci.calit2.antmonitor.lib.vpn.TLSCertificateActivity;
import edu.uci.calit2.antmonitor.lib.vpn.VpnController;
import edu.uci.calit2.antmonitor.lib.vpn.VpnState;

import edu.uci.calit2.antmonitor.lib.logging.PacketProcessor.TrafficType;

public class MainActivity extends Activity implements AntMonitorActivity, View.OnClickListener {

    /** Tag for logging */
    private static final String TAG = MainActivity.class.getSimpleName();

    /** User id, used for marking log files as belonging to a certain user */
    public static final String userID = "demo";

    /** The controller that will be used to start/stop the VPN service */
    private VpnController mVpnController;

    /** Button to allow users to connect to the VPN */
    Button btnConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* SETUP UI */

        // Setup connect button
        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(this);

        // We disable the button until we bind to VPN service. Until then, we cannot
        // control the VPN service, so the button should remain disabled
        btnConnect.setEnabled(false);

        // Setup disconnect button
        // You can disable it also, but for the sake of a simple example, we will leave it enabled
        Button btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        btnDisconnect.setOnClickListener(this);


        /* SETUP VPN */

        // Initialize the controller
        mVpnController = VpnController.getInstance(this);

        // We plan on using SSL bumping, so we must install root certificate
        Intent i = new Intent(MainActivity.this, TLSCertificateActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(i, VpnController.REQUEST_INSTALL_CERT);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnConnect:
                // If the user was able to press this button, that means we are already bound

                // Another requirement for establishing a VPN service is having user's permission

                // So, before we connect, see if the user granted us VPN rights before:
                Intent intent = android.net.VpnService.prepare(MainActivity.this);

                // If the intent is not null, we do not have VPN rights
                if (intent != null) {
                    // Ask user for VPN rights. If they are granted,
                    // onActivityResult will be called with RESULT_OK
                    startActivityForResult(intent, 0);
                } else {
                    // VPN rights were granted before, attempt a connection
                    onActivityResult(0, RESULT_OK, null);
                }
                break;
            case R.id.btnDisconnect:
                // User wants to disconnect from VPN
                mVpnController.disconnect();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // bind to our service here. You must bind before you connect to VPN
        mVpnController.bind();
        // Note that we are not yet bound. We will be bound when we receive our first update about
        // the VPN state in onVpnStateChanged(), at which point we can enable the connect button
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Unbind from the service as we no longer need to receive VPN status updates and we
        // will no longer be controlling the VPN (no connect/disconnect)
        mVpnController.unbind();
    }

    /**
     * Receives VPN state updates so that we can update our activity as needed.
     * You will only receive updates if you are bound to the vpn controller.
     * @param vpnState The new state of the VPN connection.
     */
    @Override
    public void onVpnStateChanged(VpnState vpnState) {
        Log.d(TAG, "Received new vpn state: " + vpnState);

        // Receiving an update means we are bound, and we can control the VPN service, so now we can
        // allow the user to press the "connect" button (if we are not already connecting/connected)
        btnConnect.setEnabled((vpnState != VpnState.CONNECTED && vpnState != VpnState.CONNECTING));
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        // Check if the user granted us rights to VPN
        if (result == Activity.RESULT_OK) {
            // If so, we can attempt a connection

            // Prepare Packet Filter
            OutPacketFilter outFilter = new ExamplePacketFilterOut(this);

            // Prepare packet consumers (off-line packet processing)
            ExamplePacketConsumer incConsumer =
                    new ExamplePacketConsumer(this, TrafficType.INCOMING_PACKETS, userID);

            ExamplePacketConsumer outConsumer =
                    new ExamplePacketConsumer(this, TrafficType.OUTGOING_PACKETS, userID);

            // Pass in the given SDK API and pass in a AuthenticateAPIListener to know when
            // the connection is complete (and whether it is successful)
            mVpnController.connect(null, outFilter, incConsumer, outConsumer);

        } else if (request == VpnController.REQUEST_INSTALL_CERT) {
            if (result == Activity.RESULT_OK) {
                Log.d(TAG, "Certificate installed.");
            } else {
                Log.d(TAG, "Certificate not installed.");
            }
        }
    }
}
