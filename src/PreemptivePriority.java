import java.util.List;

public class PreemptivePriority implements Scheduler {

	@Override
	public void run(List<Process> processes) {
		System.out.println("====== Preemptive Priority ======");
		int time = 0;
		
		int completed = 0; // Number of completed processes
		
		int n = processes.size();
		for(Process p : processes) {
			p.remaining = p.burst; // intialize all remainings as bursts 
		}
		
		while(completed < n) {
			Process current = null;
			for(int i = 0;i < n;i++) {
				Process p = processes.get(i);
				if(p.arrival <= time && p.remaining > 0) {
					if(current == null) {
						current = p;
					}
					else {
						if(p.priority < current.priority) {
							current = p;
						}
					}
				}
			}
			
		}
		
	}

}
