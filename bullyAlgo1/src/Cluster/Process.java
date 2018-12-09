package Cluster;

import java.util.*;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.ArrayUtils;

import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry.Entry;

import sun.net.www.content.text.plain;

import java.io.*;
import java.net.*;

public class Process {
	ProcessSockets PSocket;
	private String Log;
	boolean ElectionFlag, stillCandidate,sendInvitation;
	int HighestPriority, numResponses, NoJobs;
	HashMap<Integer, String> RecivedRequests;
	int[] jobArr;
	private static final String EOL = "\n";
	public Process(int _pID, int _PortNum) {
		HighestPriority = -1;
		NoJobs = 0;
		this.PSocket = new ProcessSockets(_pID, _PortNum);
		RecivedRequests = new HashMap<Integer, String>();
		ElectionFlag = true;
		stillCandidate = true;
		sendInvitation=false;
		numResponses = -1;
		Log = "";
	}

	public void ReceiveOrders() {
		String Message = PSocket.Listen();
		String[] Messages = Message.split("-");
		if (Messages.length > 1) {
			RecivedRequests.put(Integer.parseInt(Messages[1]), Messages[0]);
		}
	}

	public String ExecuteOrders() {
		String ResponseMessages="";
		for (int key : RecivedRequests.keySet()) {
			
			if (RecivedRequests.get(key).equals("COORDINATOR")) {
				PSocket.setCoordinator(key);
				stillCandidate = false;
				ElectionFlag = false;
			}
			 else if (RecivedRequests.get(key).equals("COORDINATOR DEAD")) {
					CoordinatorDied();
				}
			 else if (RecivedRequests.get(key).equals("COORDINATOR NOT Responding")) {
					CoordinatorDied();
				}
			else if (RecivedRequests.get(key).contains("JOB")) {
				String ValStr = RecivedRequests.get(key);
				String[] _Message = ValStr.split("/");
				int mini = MinimumJob(strIntArr(_Message[2]));
				PSocket.SendMessage(key, "RESULT/" + mini + "-" + PSocket.pID);
			} else if (RecivedRequests.get(key).contains("RESULT")) {
				String ValStr = RecivedRequests.get(key);
				String[] _Message = ValStr.split("/");
				jobArr[key] = Integer.parseInt(_Message[1]);
				if (checkArr(jobArr, -1)) {
					ResponseMessages+="the minimum value is : " + MinimumJob(jobArr)+"\n";
				}
			} else if (RecivedRequests.get(key).equals("LIVE")) {
				PSocket.SendMessage(key, "YES-" + PSocket.pID);
				
			} else if (RecivedRequests.get(key).equals("YES")) {
				PSocket.setCoordinator(key);
				stillCandidate = false;
				ElectionFlag = false;
			} else if (RecivedRequests.get(key).equals("ELECTION")) {
				PSocket.SendMessage(key, "OK-" + PSocket.pID);
				stillCandidate = true;
				ElectionFlag = true;
			} else if (RecivedRequests.get(key).equals("OK")) {
				stillCandidate = false;
				ElectionFlag = true;
				HighestPriority = Math.max(HighestPriority, key);
			}

			ResponseMessages+=key + " -> " + PSocket.pID + " : " + RecivedRequests.get(key) + EOL;
		}

		RecivedRequests.clear();
		if (sendInvitation && ElectionFlag && stillCandidate) {
			PSocket.Brodcasting("COORDINATOR-" + PSocket.pID);
			PSocket.setCoordinator(PSocket.pID);
			ElectionFlag = false;
		}
		return ResponseMessages;
	}

	public boolean checkArr(int[] arr, int val) {
		
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == val)
				return false;
		}
		return true;
	}

	// Bonus Feature
	public int[] strIntArr(String str) {
		
		str = str.replace("[", "");
		str = str.replace("]", "");
		str = str.replace(" ", "");

		if(!str.isEmpty()) {
		String[] arrStr = str.split(",");
		int[] arr = new int[arrStr.length];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = Integer.parseInt(arrStr[i]);
		}

		return arr;
		}
		
		return null;
	}

	public void MinimumJob(int[] arr, boolean firstTime) {
		if ((PSocket.pID == PSocket.CoordinatorID) && firstTime) {
			NoJobs++;
			int LenArr = arr.length, NumProcs = PSocket.ProcTable.length - 1, chunck, start = 0, end = 0;
			if (LenArr >= NumProcs) {
				chunck = (int) LenArr / NumProcs;
				jobArr = new int[NumProcs];
				Arrays.fill(jobArr, -1);
				for (int i = 0; i < NumProcs; i++) {
					if (i != PSocket.pID) {
						start = end+1;
						int inc=end + chunck;
						end =(i==NumProcs-1)? LenArr:((inc<=LenArr)?inc:LenArr) ;
						PSocket.SendMessage(i, "JOB/" + NoJobs + "/"
								+ Arrays.toString(ArrayUtils.subarray(arr, start, end)) + "-" + PSocket.pID);
					}
				}
			}
		}
	}

	int MinimumJob(int[] arr) {
		int minValue = Integer.MAX_VALUE;
		for (int i = 0; i < arr.length; i++)
			if (arr[i] < minValue)
				minValue = arr[i];
		return minValue;
	}

	public void Election() {
		if (stillCandidate) {
			for (int i = PSocket.ProcTable.length - 1; i >= this.PSocket.pID; i--) {
				if(PSocket.ProcTable[i]!=-1) {
				PSocket.SendMessage(i, "ELECTION-" + this.PSocket.pID);
				numResponses++;
				}
			}
			sendInvitation=true;
		}
	}
	public void CoordinatorDied() {
		System.out.println(PSocket.pID+" Coordinator Died");
		if(PSocket.CoordinatorID!=-1)
			PSocket.ProcTable[PSocket.CoordinatorID]=-1;
		PSocket.CoordinatorID=-1;
		ElectionFlag=true;
		stillCandidate=true;
	}

}
