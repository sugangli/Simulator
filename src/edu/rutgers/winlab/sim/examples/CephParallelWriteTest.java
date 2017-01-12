package edu.rutgers.winlab.sim.examples;

import java.util.List;

import edu.rutgers.winlab.sim.ceph.CephPacket;
import edu.rutgers.winlab.sim.ceph.Client;
import edu.rutgers.winlab.sim.ceph.ParallelClient;
import edu.rutgers.winlab.sim.ceph.ParallelOSD;
import edu.rutgers.winlab.sim.ceph.SimpleCrush;
import edu.rutgers.winlab.sim.core.EventQueue;
import edu.rutgers.winlab.sim.core.ISerializableHelper;
import edu.rutgers.winlab.sim.core.MACPacket;
import edu.rutgers.winlab.sim.core.Node;

public class CephParallelWriteTest {

	public static void main(String[] args) {
		ParallelClient n1 = new ParallelClient("Client1", false);
		int num_of_osd = 3;
		int num_of_PG = 5;
		ParallelOSD.setNum_of_PG(num_of_PG);

		for (int i = 0; i < num_of_osd; i++){
			ParallelOSD osd = new ParallelOSD(Integer.toString(i));
			Node.AddNodeLink(n1, osd, 0);
			ParallelOSD.getOSDMap().put(Integer.toString(i),osd);
		}


		List<List<String>> finallist = SimpleCrush.ParallelCrush(ParallelOSD.getOSDMap(), num_of_PG, 3);


		ParallelOSD.configureLAN(ParallelOSD.getOSDMap());


		SimpleCrush.shuffleList(finallist, num_of_PG);//shuffle the list for every experiment

		int object_size =  ISerializableHelper.MBYTE; 
		long write_time = 1;
		long workload = write_time * object_size;
		//			System.out.printf("workload:%d\n", workload);
		for(int j = 0; j < write_time; j++){
			String object_name1 = Integer.toString(j);
			ParallelOSD temp_osd = ParallelOSD.getOSDMap().get(ParallelOSD.targetOSDLookup(object_name1));
			n1.ParallelWriteOp(new MACPacket(n1, temp_osd, new CephPacket(object_name1 + ",WRITE", object_size)), false);

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
