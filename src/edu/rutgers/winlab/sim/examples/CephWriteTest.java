package edu.rutgers.winlab.sim.examples;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import edu.rutgers.winlab.sim.ceph.CephPacket;
import edu.rutgers.winlab.sim.ceph.Client;
import edu.rutgers.winlab.sim.ceph.OSD;
import edu.rutgers.winlab.sim.ceph.SimpleCrush;
import edu.rutgers.winlab.sim.core.EventQueue;
import edu.rutgers.winlab.sim.core.ISerializableHelper;
import edu.rutgers.winlab.sim.core.MACPacket;
import edu.rutgers.winlab.sim.core.Node;

public class CephWriteTest {

	public static void main(String[] args) {
		Client n1 = new Client("Client1");
		int num_of_osd = 10;
		int num_of_PG = 100;
		OSD.setNum_of_PG(num_of_PG);
		
		for (int i = 0; i < num_of_osd; i++){
			OSD osd = new OSD(Integer.toString(i));
			Node.AddNodeLink(n1, osd, 0);
			OSD.getOSDMap().put(Integer.toString(i),osd);
		}
		
		SimpleCrush sc = new SimpleCrush(OSD.getOSDMap(), num_of_PG, 3);
		OSD.configureLAN(OSD.getOSDMap());

		for(int i = 0; i < 1024*10; i++){
			String object_name1 = Integer.toString(i);
			OSD temp_osd = OSD.getOSDMap().get(OSD.targetOSDLookup(object_name1));
			int object_size = ISerializableHelper.MBYTE; 
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
