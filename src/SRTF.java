import java.util.LinkedList;
import java.util.List;

public class SRTF implements Scheduler {
	private List<Process> executionOrder = new LinkedList<>();
	public List<Process> getExecutionOrder() {
        return executionOrder;
    }
    @Override
    public void run(List<Process> processes, int contextSwitch) {
		

        int NumProcess = processes.size();
        int timer = 0;
        int finish = 0;

        for (Process proc : processes) {
            proc.remaining = proc.burst;
        }

        List<Process> ReadyList = new LinkedList<>();
        Process prev_Process = null;

        while (NumProcess > finish) {

            for (Process proc : processes) {
                if (proc.remaining != 0 && proc.arrival <= timer && !ReadyList.contains(proc)) {
                    ReadyList.add(proc);
                }
            }

            int min = Integer.MAX_VALUE;
            Process current = null;

            for (Process proc : ReadyList) {
                if (proc.remaining > 0 && proc.remaining < min) {
                    min = proc.remaining;
                    current = proc;
                }
            }

            if (current == null) {
                timer++;
                prev_Process = null;
                continue;
            }

            if (prev_Process != null && prev_Process != current) {
                timer += contextSwitch;
            }

            timer++;
            current.remaining--;
            prev_Process = current;

            if (current.remaining == 0) {
                finish++;
                current.turnaround = timer - current.arrival;
                current.waiting = current.turnaround - current.burst;
                executionOrder .add(current);
                ReadyList.remove(current);
            }
        }
    }
}
