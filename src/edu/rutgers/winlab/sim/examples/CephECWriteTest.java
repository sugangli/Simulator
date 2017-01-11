package edu.rutgers.winlab.sim.examples;

import java.util.List;

import edu.rutgers.winlab.sim.ceph.CephPacket;
import edu.rutgers.winlab.sim.ceph.Client;
import edu.rutgers.winlab.sim.ceph.OSD;
import edu.rutgers.winlab.sim.ceph.SimpleCrush;
import edu.rutgers.winlab.sim.core.EventQueue;
import edu.rutgers.winlab.sim.core.ISerializableHelper;
import edu.rutgers.winlab.sim.core.MACPacket;
import edu.rutgers.winlab.sim.core.Node;

public class CephECWriteTest {

	public static void main(String[] args) {
		Client n1 = new Client("Client1");
		int num_of_osd = 10;
		int num_of_PG = 100;
		OSD.setNum_of_PG(num_of_PG);

		int ec_K = 6;
		int ec_M = 2;
		OSD.setEC_K(ec_K);
		OSD.setEC_M(ec_M);
		for (int i = 0; i < num_of_osd; i++){
			OSD osd = new OSD(Integer.toString(i));
			Node.AddNodeLink(n1, osd, 0);
			OSD.getOSDMap().put(Integer.toString(i),osd);
		}


		List<List<String>> finallist = SimpleCrush.Crush(OSD.getOSDMap(), num_of_PG,  ec_K + ec_M);


		OSD.configureLAN(OSD.getOSDMap());


		SimpleCrush.shuffleList(finallist, num_of_PG);//shuffle the list for every experiment

		int object_size =   ISerializableHelper.MBYTE; 
		long write_time = 1;
		long workload = write_time * object_size;
		//			System.out.printf("workload:%d\n", workload);
		for(int j = 0; j < write_time; j++){
			String object_name1 = Integer.toString(j);
			OSD temp_osd = OSD.getOSDMap().get(OSD.targetOSDLookup(object_name1));
			n1.sendPacket(new MACPacket(n1, temp_osd, new CephPacket(object_name1 + ",ECWRITE", object_size)), false);

		}

		//			PrintStream out;
		//			try {
		//				out = new PrintStream(new FileOutputStream("output.txt"));
		//				System.setOut(out);
		//			} catch (FileNotFoundException e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			}

		EventQueue.Run();
		//			long time7 = System.currentTimeMillis();
		//			System.out.printf("Delay6: %d\n", time7 - time6);
		double finish_time = EventQueue.Now();
		EventQueue.Reset();
		double bandwidth  = workload/finish_time/ISerializableHelper.MBYTE;
		System.out.printf("%f MB/s\n", bandwidth);




	}


}