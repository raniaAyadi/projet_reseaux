import java.util.List;
import java.util.AbstractMap.SimpleEntry;

public class TrackerconnectTest {

	public static void main(String[] args) throws Exception {
		TrackerConnection tc = new TrackerConnection("127.0.0.1", 3000);
		List<SimpleEntry<String , Integer>> ret =  tc.getfile("s9j8Ui5o+IvcABA2Ds60stWf0YODRp3/");
		for(int i=0;i<ret.size();i++){
			SimpleEntry<String , Integer> ent = ret.get(i);
			System.out.println("peer:: ip:" + ent.getKey()+" port:" + ent.getValue().intValue());
			
		}
	}

}
