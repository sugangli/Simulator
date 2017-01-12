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

public class ParallelOSD extends Node{
	
	private HashMap<String, Long> CommitMap = new HashMap<String, Long>();
	private HashMap<String, Node> WRITEMap = new HashMap<String, Node>();
	private HashMap<String, Integer> ACKMap = new HashMap<String, Integer>();//ACK Counter, add 1 if one ack comes back
	private static double DISK_BANDWIDTH_MBitPS = 1224;
	private static double CONSTANT_WRITE_DELAY = 100 * EventQueue.MICRO_SECOND;
	private static double EC_ENCODE_MBitPS_ = 11200; // RDP Encoding for K=6, M = 2
	private static long ACK_SIZE = 64 * ISerializableHelper.BYTE;
	private static int EC_K = 6;
	private static int EC_M = 2;
	private static int Num_of_PG;

	public static int getEC_K() {
		return EC_K;
	}



	public static void setEC_K(int eC_K) {
		EC_K = eC_K;
	}



	public static int getEC_M() {
		return EC_M;
	}



	public static void setEC_M(int eC_M) {
		EC_M = eC_M;
	}
	
	private Serial.SerialAction<MACPacket> processPacket = new Serial.SerialAction<MACPacket>() {

		@Override
		public double execute(Serial<MACPacket> s, MACPacket macpacket) {
			Payload payload = macpacket.getMacpayload();
			
			double process_delay = 0;
			if(payload == null) return 0;
			String[] s_array = payload.getVal().split(",");
			switch (s_array[1]){
			case "READ":	
//				System.out.printf("OSD %s get %s at Time %f\n", OSD.this.getName(), payload.toString(), EventQueue.Now());
				break;
				
			case "WRITE":
				System.out.printf("OSD %s get %s Size %d Time %f\n", ParallelOSD.this.getName(), payload.toString(), payload.getSizeInBits(), EventQueue.Now());
				int PG_num = Integer.parseInt(s_array[0]) % getNum_of_PG();
				List<String> osd_group = SimpleCrush.getDefaultMap().get(PG_num);
				ParallelOSD.this.sendPacket(new MACPacket(ParallelOSD.this, ParallelOSD.getOSDMap().get(osd_group.get(1)), 
						new CephPacket(s_array[0] + ",COMMIT", ACK_SIZE)), false);
				ParallelOSD.this.sendPacket(new MACPacket(ParallelOSD.this, ParallelOSD.getOSDMap().get(osd_group.get(2)), 
						new CephPacket(s_array[0] + ",COMMIT", ACK_SIZE)), false);
				ACKMap.put(s_array[0], 0);
				WRITEMap.put(s_array[0], macpacket.From);
				process_delay = payload.getSizeInBits() / DISK_BANDWIDTH_MBitPS / ISerializableHelper.MBIT
						+ CONSTANT_WRITE_DELAY;		
				//send commit to other two OSD;
				break;
				
			case "BUFFER":
				System.out.printf("OSD %s get %s Size %d Time %f\n", ParallelOSD.this.getName(), payload.toString(), payload.getSizeInBits(), EventQueue.Now());
				if(CommitMap.get(s_array[0]) != null){
					ParallelOSD.this.sendPacket(new MACPacket(ParallelOSD.this, ParallelOSD.getOSDMap().get(ParallelOSD.targetOSDLookup(s_array[0])), 
							new CephPacket(s_array[0] + ",COMMITACK", ACK_SIZE)), false);
					process_delay = payload.getSizeInBits() / DISK_BANDWIDTH_MBitPS / ISerializableHelper.MBIT
							+ CONSTANT_WRITE_DELAY;
				}else{
					CommitMap.put(s_array[0], payload.getSizeInBits());
				}
				
				break;
				
			case "COMMIT":
				System.out.printf("OSD %s get %s Size %d Time %f\n", ParallelOSD.this.getName(), payload.toString(), payload.getSizeInBits(), EventQueue.Now());
				if(CommitMap.get(s_array[0]) != null){
					ParallelOSD.this.sendPacket(new MACPacket(ParallelOSD.this, (ParallelOSD) macpacket.From, 
							new CephPacket(s_array[0] + ",COMMITACK", ACK_SIZE)), false);
					process_delay = payload.getSizeInBits() / DISK_BANDWIDTH_MBitPS / ISerializableHelper.MBIT
							+ CONSTANT_WRITE_DELAY;
				}else{
					CommitMap.put(s_array[0], payload.getSizeInBits());
				}
				break;
			case "ECCOMMIT":
				System.out.printf("OSD %s get %s Size %d Time %f\n", ParallelOSD.this.getName(), payload.toString(), payload.getSizeInBits(), EventQueue.Now());
				if(CommitMap.get(s_array[0]) != null){
					ParallelOSD.this.sendPacket(new MACPacket(ParallelOSD.this, (ParallelOSD) macpacket.From, 
							new CephPacket(s_array[0] + ",ECCOMMITACK", ACK_SIZE)), false);
					process_delay = payload.getSizeInBits() / DISK_BANDWIDTH_MBitPS / ISerializableHelper.MBIT
							+ CONSTANT_WRITE_DELAY;
				}else{
					CommitMap.put(s_array[0], payload.getSizeInBits());
				}
				break;
			case "COMMITACK":
				System.out.printf("OSD %s get %s Size %d Time %f\n", ParallelOSD.this.getName(), payload.toString(), payload.getSizeInBits(), EventQueue.Now());
				int counter = ACKMap.get(s_array[0]);
				counter++;
				if (counter < 2){
					ACKMap.put(s_array[0], counter);
				}else{
					ParallelOSD.this.sendPacket(new MACPacket(ParallelOSD.this, WRITEMap.get(s_array[0]), 
							new CephPacket(s_array[0] + ",WRITEACK", ACK_SIZE)), false);
				}
				break;
				
			case "ECWRITE":
				System.out.printf("OSD %s get %s Size %d Time %f\n", ParallelOSD.this.getName(), payload.toString(), payload.getSizeInBits(), EventQueue.Now());
				int ec_PG_num = Integer.parseInt(s_array[0]) % getNum_of_PG();
				List<String> ec_osd_group = SimpleCrush.getDefaultMap().get(ec_PG_num);
				long ec_data_size = payload.getSizeInBits();
				ACKMap.put(s_array[0], 0);
				WRITEMap.put(s_array[0], macpacket.From);
				for (int i = 1; i < EC_K + EC_M; i++){
					ParallelOSD.this.sendPacket(new MACPacket(ParallelOSD.this, ParallelOSD.getOSDMap().get(ec_osd_group.get(i)), 
							new CephPacket(s_array[0] + ",ECCOMMIT", ACK_SIZE)), false);
				}
				process_delay =  ec_data_size / DISK_BANDWIDTH_MBitPS / ISerializableHelper.MBIT
						+ CONSTANT_WRITE_DELAY;
				break;
			case "ECCOMMITACK":
				System.out.printf("OSD %s get %s Size %d Time %f\n", ParallelOSD.this.getName(), payload.toString(), payload.getSizeInBits(), EventQueue.Now());
				int ec_counter = ACKMap.get(s_array[0]);
				ec_counter++;
				if (ec_counter < EC_K + EC_M - 1){
					ACKMap.put(s_array[0], ec_counter);
				}else{
					ParallelOSD.this.sendPacket(new MACPacket(ParallelOSD.this, WRITEMap.get(s_array[0]), 
							new CephPacket(s_array[0] + ",ECWRITEACK", ACK_SIZE)), false);
				}
				break;
			default:
				System.out.printf("OSD %s get %s at Time %f. Not regconized!\n", ParallelOSD.this.getName(), payload.toString(), EventQueue.Now());
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



	public ParallelOSD(String name) {
		super(name);
		setProcessPacket(processPacket);
		// TODO Auto-generated constructor stub
	}
	private static HashMap<String, ParallelOSD> OSDMap = new HashMap<String, ParallelOSD>();
	
	public static HashMap<String, ParallelOSD> getOSDMap() {
		return OSDMap;
	}

	

	public static int getNum_of_PG() {
		return Num_of_PG;
	}

	public static void setNum_of_PG(int num_of_PG) {
		Num_of_PG = num_of_PG;
	}
	
	public static void configureLAN(HashMap<String, ParallelOSD> OSDs){
		List<String> input = new ArrayList<String>();
		for(Map.Entry<String, ParallelOSD> entry : OSDs.entrySet() ){
			input.add(entry.getKey());
		}
		List<String[]> result = SimpleCrush.Combination(input, 2);
		for(String[] item : result){
			ParallelOSD osd1 = OSDMap.get(item[0]);
			ParallelOSD osd2 = OSDMap.get(item[1]);
			Node.AddNodeLink(osd1, osd2, 0);
		}
	}

	public  static void main(String[] args){

//		Client n1 = new Client("Client1");
//		int num_of_osd = 3;
//		int num_of_PG = 3;
//		setNum_of_PG(num_of_PG);
//		
//		for (int i = 0; i < num_of_osd; i++){
//			OSD osd = new OSD(Integer.toString(i));
//			Node.AddNodeLink(n1, osd, 0);
//			OSDMap.put(Integer.toString(i),osd);
//		}
//		
//		List<List<String>> finallist  = SimpleCrush.Crush(getOSDMap(), num_of_PG , 3);
//		SimpleCrush.shuffleList(finallist, num_of_PG);
//		configureLAN(OSDMap);
//
//		for(int i = 0; i < 1024 * 10; i++){
//			String object_name1 = Integer.toString(i);
//			OSD temp_osd = getOSDMap().get(targetOSDLookup(object_name1));
//			int object_size = 1 * ISerializableHelper.MBYTE; 
//			n1.sendPacket(new MACPacket(n1, temp_osd, new CephPacket(object_name1 + ",WRITE", object_size)), false);
//
//		}
//		PrintStream out;
//		try {
//			out = new PrintStream(new FileOutputStream("output.txt"));
//			System.setOut(out);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		EventQueue.Run();

	}


}
