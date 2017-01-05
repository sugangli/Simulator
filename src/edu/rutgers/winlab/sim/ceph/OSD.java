package edu.rutgers.winlab.sim.ceph;

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
import edu.rutgers.winlab.sim.test.MyNode;
import edu.rutgers.winlab.sim.test.MyNode.MyData;

public class OSD extends Node{


	private Serial.SerialAction<MACPacket> processPacket = new Serial.SerialAction<MACPacket>() {

		@Override
		public double execute(Serial<MACPacket> s, MACPacket macpacket) {
			Payload payload = macpacket.getMacpayload();
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
				//send replica to other two OSD;
				break;
			case "BUFFER":
				break;
			case "COMMIT":
				break;
			case "COMMITACK":
				break;
			case "WRITEACK":
				break;
			case "REPLICATION":
				System.out.printf("OSD %s get %s at Time %f\n", OSD.this.getName(), payload.toString(), EventQueue.Now());
				OSD from = (OSD) macpacket.From;
				OSD.this.sendPacket(new MACPacket(OSD.this, from, 
						new CephPacket(s_array[0] + ",REPLICATIONACK", 1454 * ISerializableHelper.BYTE)), false);
				break;
			case "REPLICATIONACK":
				System.out.printf("OSD %s get %s at Time %f\n", OSD.this.getName(), payload.toString(), EventQueue.Now());
				int counter = ACKMap.get(s_array[0]);
				counter++;
				if (counter < 2){
					System.out.println("Counter:" + counter);
					ACKMap.put(s_array[0], counter);
				}else{
					System.out.printf("Node: %s", WRITEMap.get(s_array[0]).toString());
					OSD.this.sendPacket(new MACPacket(OSD.this, WRITEMap.get(s_array[0]), 
							new CephPacket(s_array[0] + ",WRITEACK", 1454 * ISerializableHelper.BYTE)), false);
					System.out.println("Send WRITEACK");
				}
				break;
			default:
				System.out.println(s_array[1] + "Operation can not be regconized! Drop it!");
				break;
			}
			//return 1. replicate process delay 2. primary process delay
			return 0;
		}
	};
	
	public static String targetOSDLookup(String object_name){
		int PG_num = Integer.parseInt(object_name) % getNum_of_PG();
		List<String> osd_group = SimpleCrush.getDefaultMap().get(PG_num);
		return osd_group.get(0);
	}



	//	private Serial.SerialAction<MACPacket> sendAckPacket = new Serial.SerialAction<MACPacket>() {
	//
	//		@Override
	//		public double execute(Serial<MACPacket> s, MACPacket parameter) {
	//			// TODO Auto-generated method stub
	//			return 0;
	//		}
	//	};

	public OSD(String name) {
		super(name);
		setProcessPacket(processPacket);
		// TODO Auto-generated constructor stub
	}
	private boolean isPrimary = false;
	private List<Integer> PGs = new ArrayList<Integer>();
	private static HashMap<String, OSD> OSDMap = new HashMap<String, OSD>();
	
	public static HashMap<String, OSD> getOSDMap() {
		return OSDMap;
	}

	private HashMap<String, Integer> CommitMap = new HashMap<String, Integer>();
	private HashMap<String, Node> WRITEMap = new HashMap<String, Node>();
	private HashMap<String, Integer> ACKMap = new HashMap<String, Integer>();//ACK Counter, add 1 if one ack comes back
	
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

		MyNode n1 = new MyNode("n1");
		MyNode n2 = new MyNode("n1");
		int num_of_osd = 3;
		int num_of_PG = 3;
		setNum_of_PG(num_of_PG);
		
		HashMap<String, OSD> osds = new HashMap<String, OSD>();
		for (int i = 0; i < num_of_osd; i++){
			OSD osd = new OSD(Integer.toString(i));
			Node.AddNodeLink(n1, osd, 0);
			OSDMap.put(Integer.toString(i),osd);
		}
		
		SimpleCrush sc = new SimpleCrush(getOSDMap(), num_of_PG, 3);
		configureLAN(OSDMap);

		String object_name1 = "1321231242";
//		String object_name2 = "1321256423";
//		String object_name3 = "8794165132";
		
		OSD temp_osd = getOSDMap().get(targetOSDLookup(object_name1));
		int object_size = 1 * ISerializableHelper.MBYTE; 
		n1.sendPacket(new MACPacket(n1, temp_osd, new CephPacket(object_name1 + ",WRITE", object_size)), false);
	

		//			n2.sendPacket(new MACPacket(n2, temp_osd, new CephPacket("123123123,READ")), false);


		//		n2.sendPacket(new MACPacket(n2, osd1, new CephPacket("READ")), false);

		EventQueue.Run();

	}






}
