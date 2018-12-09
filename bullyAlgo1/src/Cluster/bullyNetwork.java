package Cluster;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.lang.Object;

public class bullyNetwork {

	Process[] proc;
	int CoordinatorIdx;
	int numProcesses;
	boolean electionRequest;

	public bullyNetwork(int _numProcesses) {
		numProcesses = _numProcesses;
		CoordinatorIdx = -1;
		electionRequest = true;
		check=true;
		proc = new Process[numProcesses];
		for (int i = 0; i < numProcesses; i++) {
			proc[i] = new Process(i, 30000 + (i));
		}
		for (int i = 0; i < numProcesses; i++)
			proc[i].PSocket.AddToProcTable(proc);
	}
	// Listen-Then-Talk
    boolean check;
	public String[] bullySimulation(int ProcID, int k, int[] arr) {
         String []ResponseMessages=new String[3];
         Arrays.fill(ResponseMessages, "");
		for (int i = 0; i < numProcesses; i++) {
			if (proc[i].PSocket.alive) {
				proc[i].ReceiveOrders();
				if (i == ProcID)
					ResponseMessages[0]+=proc[i].ExecuteOrders();
					
				
				CoordinatorIdx = proc[i].PSocket.CoordinatorID;
                 if(CoordinatorIdx!=-1)
                	 ResponseMessages[2]=""+CoordinatorIdx;
				if (arr != null &&check==true) {
					proc[CoordinatorIdx].MinimumJob(arr, true);
					check=false;
				}


				if (CoordinatorIdx != -1 && i == ProcID) {
					if (proc[i].PSocket.PingToCoordinator()) { // Process Ping to Coordinator
						boolean NotResponding=true;
						if (i != CoordinatorIdx) {
							proc[CoordinatorIdx].ReceiveOrders(); // Coordinator Take Action
							ResponseMessages[1]+=proc[CoordinatorIdx].ExecuteOrders();
						}
						proc[i].ReceiveOrders();
						ResponseMessages[0]+=proc[i].ExecuteOrders(); // Process Execute Orders form Coordinator
					} else {
						CoordinatorIdx = -1;
						proc[i].CoordinatorDied();
						proc[i].PSocket.Brodcasting("COORDINATOR DEAD");
					}
				} else { // Election
					if (i == ProcID) {
						proc[i].Election();
					}
				}
			}
		}
		return ResponseMessages;
	}

	public void KillAll() {
		for (int i = 0; i < numProcesses; i++) {
			proc[i].PSocket.Close();
		}
	}

	public void KillCoordinator(int id) {
		proc[id].PSocket.Close();
	}
}
