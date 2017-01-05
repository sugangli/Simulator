package edu.rutgers.winlab.sim.ceph;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.rutgers.winlab.sim.core.EventQueue;
import edu.rutgers.winlab.sim.core.ISerializableHelper;
import edu.rutgers.winlab.sim.core.MACPacket;
import edu.rutgers.winlab.sim.core.Node;
import edu.rutgers.winlab.sim.core.Payload;
import edu.rutgers.winlab.sim.core.Serial;


public class OSD extends Node{


	private Serial.SerialAction<MACPacket> processPacket = new Serial.SerialAction<MACPacket>() {

		@Override
		public double execute(Serial<MACPacket> s, MACPacket macpacket) {
			Payload payload = macpacket.getMacpayload();
			double process_delay = 0;
			if(payload == null) return 0;
			String[] s_array = payload.getVal().split(",");
			switch (s_array[1]){
			case "READ":	
				System.out.printf("OSD %s get %s at Time %f\n", OSD.this.getName(), payload.toString(), EventQueue.Now());
				break;
				
			case "WRITE":
				System.out.printf("OSD %s get %s at Time %f\n", OSD.this.getName(), payload.toString(), EventQueue.Now());
				int PG_num = Integer.parseInt(s_array[0]) % getNum_of_PG();
				List<String> osd_group = SimpleCrush.getDefaultMap().get(PG_num);
				OSD.this.sendPacket(new MACPacket(OSD.this, OSD.getOSDMap().get(osd_group.get(1)), 
						new CephPacket(s_array[0] + ",REPLICATION", payload.getSizeInBits())), false);
				OSD.this.sendPacket(new MACPacket(OSD.this, OSD.getOSDMap().get(osd_group.get(2)), 
						new CephPacket(s_array[0] + ",REPLICATION", payload.getSizeInBits())), false);
				ACKMap.put(s_array[0], 0);
				WRITEMap.put(s_array[0], macpacket.From);
				process_delay = payload.getSizeInBits() / DISK_BANDWIDTH_MBPS / ISerializableHelper.MBIT
						+ CONSTANT_WRITE_DELAY;		
				//send replica to other two OSD;
				break;
				
			case "BUFFER":
				break;
				
			case "COMMIT":
				break;
				
			case "DATA":
				break;
				
			case "COMMITACK":
				break;
				
			case "WRITEACK":
				break;
				
			case "REPLICATION":
				System.out.printf("OSD %s get %s at Time %f\n", OSD.this.getName(), payload.toString(), EventQueue.Now());
				OSD from = (OSD) macpacket.From;
				OSD.this.sendPacket(new MACPacket(OSD.this, from, 
						new CephPacket(s_array[0] + ",REPLICATIONACK", 64 * ISerializableHelper.BYTE)), false);
				process_delay = payload.getSizeInBits() / DISK_BANDWIDTH_MBPS / ISerializableHelper.MBIT
						+ CONSTANT_WRITE_DELAY;
				break;
				
			case "REPLICATIONACK":
				System.out.printf("OSD %s get %s at Time %f\n", OSD.this.getName(), payload.toString(), EventQueue.Now());
				int counter = ACKMap.get(s_array[0]);
				counter++;
				if (counter < 2){
					ACKMap.put(s_array[0], counter);
				}else{
					OSD.this.sendPacket(new MACPacket(OSD.this, WRITEMap.get(s_array[0]), 
							new CephPacket(s_array[0] + ",WRITEACK", 64 * ISerializableHelper.BYTE)), false);
				}
				break;
				
			default:
				System.out.println(s_array[1] + "Operation can not be regconized! Drop it!");
				break;
			}
			//return 1. replicate process delay 2. primary process delay
			return process_delay;
		}
	};
	
	public static String targetOSDLookup(String object_name){
		int PG_num = Integer.parseInt(object_name) % getNum_of_PG();
		List<String> osd_group = SimpleCrush.getDefaultMap().get(PG_num);
		return osd_group.get(0);
	}



	public OSD(String name) {
		super(name);
		setProcessPacket(processPacket);
		// TODO Auto-generated constructor stub
	}
	private static HashMap<String, OSD> OSDMap = new HashMap<String, OSD>();
	
	public static HashMap<String, OSD> getOSDMap() {
		return OSDMap;
	}

	private HashMap<String, Integer> CommitMap = new HashMap<String, Integer>();
	private HashMap<String, Node> WRITEMap = new HashMap<String, Node>();
	private HashMap<String, Integer> ACKMap = new HashMap<String, Integer>();//ACK Counter, add 1 if one ack comes back
	private static double DISK_BANDWIDTH_MBPS = 1224;
	private static double CONSTANT_WRITE_DELAY = 100 * EventQueue.MICRO_SECOND;
	private static int Num_of_PG;

	public static int getNum_of_PG() {
		return Num_of_PG;
	}

	public static void setNum_of_PG(int num_of_PG) {
		Num_of_PG = num_of_PG;
	}
	
	public static void configureLAN(HashMap<String, OSD> OSDs){
		List<String> input = new ArrayList<String>();
		for(Map.Entry<String, OSD> entry : OSDs.entrySet() ){
			input.add(entry.getKey());
		}
		List<String[]> result = SimpleCrush.Combination(input, 2);
		for(String[] item : result){
			OSD osd1 = OSDMap.get(item[0]);
			OSD osd2 = OSDMap.get(item[1]);
			Node.AddNodeLink(osd1, osd2, 0);
		}
	}

	public  static void main(String[] args){

		Client n1 = new Client("Client1");
		int num_of_osd = 3;
		int num_of_PG = 3;
		setNum_of_PG(num_of_PG);
		
		for (int i = 0; i < num_of_osd; i++){
			OSD osd = new OSD(Integer.toString(i));
			Node.AddNodeLink(n1, osd, 0);
			OSDMap.put(Integer.toString(i),osd);
		}
		
		SimpleCrush sc = new SimpleCrush(getOSDMap(), num_of_PG, 3);
		configureLAN(OSDMap);

		for(int i = 0; i < 1024 * 10; i++){
			String object_name1 = Integer.toString(i);
			OSD temp_osd = getOSDMap().get(targetOSDLookup(object_name1));
			int object_size = 1 * ISerializableHelper.MBYTE; 
			n1.sendPacket(new MACPacket(n1, temp_osd, new CephPacket(object_name1 + ",WRITE", object_size)), false);

		}
		PrintStream out;
		try {
			out = new PrintStream(new FileOutputStream("output.txt"));
			System.setOut(out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		EventQueue.Run();

	}






}
