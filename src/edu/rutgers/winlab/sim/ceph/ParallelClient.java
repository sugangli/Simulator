package edu.rutgers.winlab.sim.ceph;

import java.util.List;

import edu.rutgers.winlab.sim.core.EventHandlerQueue;
import edu.rutgers.winlab.sim.core.EventQueue;
import edu.rutgers.winlab.sim.core.FIFOQueue;
import edu.rutgers.winlab.sim.core.ISerializableHelper;
import edu.rutgers.winlab.sim.core.MACPacket;
import edu.rutgers.winlab.sim.core.Node;
import edu.rutgers.winlab.sim.core.Payload;
import edu.rutgers.winlab.sim.core.Serial;
import edu.rutgers.winlab.sim.core.Serial.SerialAction;

public class ParallelClient extends Node {
	
	private EventHandlerQueue<MACPacket> applicationqueue;
	private static int EC_K;
	private static int EC_M;
	private static double EC_ENCODE_MBitPS_ = 11200; // RDP Encoding for K=6, M = 2
	
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
		public double execute(Serial<MACPacket> s, MACPacket parameter) {
			CephPacket payload = (CephPacket)parameter.getMacpayload();
			if(payload == null) return 0;
			String[] s_array = payload.getVal().split(",");
			switch (s_array[1]){
			case "READ":	
				System.out.printf("ParallelClient %s get %s Size %d Time %f\n", ParallelClient.this.getName(), payload.toString(), payload.getSizeInBits(), EventQueue.Now());
				break;
			case "WRITEACK":
				System.out.printf("ParallelClient %s get %s Size %d Time %f\n", ParallelClient.this.getName(), payload.toString(), payload.getSizeInBits(), EventQueue.Now());
				break;
			case "ECWRITEACK":
				System.out.printf("ParallelClient %s get %s Size %d Time %f\n", ParallelClient.this.getName(), payload.toString(), payload.getSizeInBits(), EventQueue.Now());
				break;
			default:
				break;			
			}
			return 0;
		}
	};
	
	public class ECWriteAction implements SerialAction<MACPacket>{

		@Override
		public double execute(Serial<MACPacket> s, MACPacket macpacket) {
			Payload payload = macpacket.getMacpayload();
			double process_delay = 0;
			if(payload == null) return 0;
			String[] s_array = payload.getVal().split(",");
			assert ParallelOSD.getNum_of_PG() != 0;
			int PG_num = Integer.parseInt(s_array[0]) % ParallelOSD.getNum_of_PG();
			List<String> ec_osd_group = SimpleCrush.getDefaultMap().get(PG_num);
			long ec_data_size = payload.getSizeInBits()/EC_K;
			ParallelClient.this.sendPacket(new MACPacket(ParallelClient.this, ParallelOSD.getOSDMap().get(ParallelOSD.targetOSDLookup(s_array[0])), 
					new CephPacket(s_array[0] + ",ECWRITE", ec_data_size)), false);
			for (int i = 1; i < EC_K + EC_M; i++){
//				System.out.printf("ParallelClient %s send %s Size %d Time %f\n", ParallelClient.this.getName(), payload.toString(), payload.getSizeInBits(), EventQueue.Now());
//				System.out.println(EC_K + EC_M);
				ParallelClient.this.sendPacket(new MACPacket(ParallelClient.this, ParallelOSD.getOSDMap().get(ec_osd_group.get(i)), 
						new CephPacket(s_array[0] + ",BUFFER", ec_data_size)), false);
			}
			process_delay = payload.getSizeInBits() / EC_ENCODE_MBitPS_/ ISerializableHelper.MBIT;
			return process_delay;
		}
		
	}
	
	public void ECWriteOp(MACPacket macpacket, boolean isPrioritized){
		applicationqueue.Enqueue(macpacket, isPrioritized);
	}
	
	
	public class ParallelWriteAction implements SerialAction<MACPacket>{

		@Override
		public double execute(Serial<MACPacket> s, MACPacket macpacket) {
			Payload payload = macpacket.getMacpayload();
			double process_delay = 0;
			if(payload == null) return 0;
			String[] s_array = payload.getVal().split(",");
			assert ParallelOSD.getNum_of_PG() != 0;
			int PG_num = Integer.parseInt(s_array[0]) % ParallelOSD.getNum_of_PG();
			List<String> osd_group = SimpleCrush.getDefaultMap().get(PG_num);
			ParallelClient.this.sendPacket(macpacket, false);
			ParallelClient.this.sendPacket(new MACPacket(ParallelClient.this, ParallelOSD.getOSDMap().get(osd_group.get(1)), new CephPacket(s_array[0] + ",BUFFER", payload.getSizeInBits())), false);
			ParallelClient.this.sendPacket(new MACPacket(ParallelClient.this, ParallelOSD.getOSDMap().get(osd_group.get(2)), new CephPacket(s_array[0] + ",BUFFER", payload.getSizeInBits())), false);
			return process_delay;
		}
		
	}
	
	public void ParallelWriteOp(MACPacket macpacket, boolean isPrioritized){
		applicationqueue.Enqueue(macpacket, isPrioritized);
	}

	

	public ParallelClient(String name, boolean isEC) {
		super(name);
		setProcessPacket(processPacket);
		if(!isEC){
			applicationqueue = new  EventHandlerQueue<MACPacket>(new FIFOQueue<MACPacket>(Integer.MAX_VALUE),  new ParallelWriteAction());
		}else{
			applicationqueue = new  EventHandlerQueue<MACPacket>(new FIFOQueue<MACPacket>(Integer.MAX_VALUE),  new ECWriteAction());
		}
		
	}
}
