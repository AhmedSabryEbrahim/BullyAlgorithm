package Cluster;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.*;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;

public class ProcessSockets {

	public int[] ProcTable;
	public int pID, PortNum, CoordinatorID;
	public ServerSocket socket;
	public boolean PingCoordinator,alive;

	public ProcessSockets(int _pID, int _PortNum) {
		pID = _pID;
		alive=true;
		PortNum = _PortNum;
		PingCoordinator = false;
		CoordinatorID = -1;
		try {
			socket = new ServerSocket(PortNum);
			System.out.println("P" + pID + " Port" + PortNum);
		} catch (IOException ex) {
			System.out.println("Socket Error " + PortNum + " " + ex.getMessage());
		}
	}

	public boolean SendMessage(int _ProcID, String Message) {
		try {
			if (_ProcID != pID&& ProcTable[_ProcID]!=-1) {
				Socket client = new Socket(InetAddress.getLocalHost(), ProcTable[_ProcID]);
				OutputStream outToServer = client.getOutputStream();
				PrintWriter out = new PrintWriter(outToServer);
				out.write(Message);
				out.flush();
				out.close();
				client.close();
			}
		} catch (Exception ex) {
			System.out.println("ex   " + _ProcID + " " + pID + " " + ex.toString());
			return false;
		}
		return true;
	}

	 public String Listen() {
		String Message = "";
		try {
			Socket client;
			try {
				socket.setSoTimeout(1000);
				client = socket.accept();
				Scanner scan = new Scanner(client.getInputStream());
				Message += scan.nextLine();
				scan.close();
				client.close();
			} catch (SocketTimeoutException ex) {
			}

		} catch (IOException ex) {
			System.out.println("Exception in P" + pID + " Listening..." + ex);
			return "";
		}
		return Message;
	}

	public boolean PingToCoordinator() {
		if (CoordinatorID == pID)
			return true;
		else if (CoordinatorID != -1) {
			return this.SendMessage(CoordinatorID, "LIVE-" + pID);
		}
		return false;
	}

	public boolean Brodcasting(String Message) {
		for (int i = 0; i < ProcTable.length; i++)
			if (i != pID && ProcTable[i]!=-1)
				SendMessage(i, Message+"-"+pID);
		return true;
	}

	public void setCoordinator(int _i) {
		CoordinatorID = _i;
	}

	public void AddToProcTable(Process[] _procs) {
		this.ProcTable = new int[_procs.length];
		for (int i = 0; i < _procs.length; i++) {
			if (i != this.pID)
				this.ProcTable[i] = _procs[i].PSocket.PortNum;
		}
	}

	public boolean Close() {
		try {
			socket.close();
			alive=false; 
		} catch (Exception ex) {
		}
		return true;
	}
}
