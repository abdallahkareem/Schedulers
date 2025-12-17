import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
public class AGScheduler implements Scheduler { // Hybrid Algorithm (FCFS, Priority_P, SRTF)
	@Override
	public void run(List<Process> processes,int contextSwitch) {
		int completed = 0;
		int time = 0;
		int n = processes.size();

		// initilize the remaining time to be equal the burst time
		for (Process process : processes) {
			process.remaining = process.burst;
		}

		Queue<Process> ready_queue = new LinkedList<>();
		while (completed < n) {
			for (Process p : processes) {
				if (p.remaining != 0 && p.arrival <= time && !ready_queue.contains(p)) {
					ready_queue.add(p);
				}
			}

			// no processes has arrived
			if (ready_queue.isEmpty()) {
				time++;
				continue;
			}
			Process current = ready_queue.poll();
			System.out.println(current.name);
			int current_quantum = current.quantum;
			
			// for the first 25% of the quantum we will serve as FCFS
			int quarter_quantum = (int) Math.ceil(current_quantum*0.25);
			for (int i = 0; i < quarter_quantum && current.remaining > 0; i++) {
				current.remaining--;
				time++;
			}
			if (current.remaining == 0) {
				completed++;
				current.quantum = 0;
				continue;
			}

			// for the second 25% = (50% - 25%) we will work as preemtive priority
			int second_quarter_quantum = (int) Math.ceil(current_quantum*0.5) - quarter_quantum;
			Process higher_priority_process = current;
			// find the highr priority process
			for (Process process : ready_queue) {
				if (process.priority < higher_priority_process.priority) {
					higher_priority_process = process;
				}
			}
			if (higher_priority_process != current) {
				int remain_priority_quantum = current_quantum -  quarter_quantum;
				current.quantum += (int) Math.ceil(remain_priority_quantum/2);
				ready_queue.add(current);
				continue;
			}
			for (int i = 0; i < second_quarter_quantum && current.remaining > 0; i++) {
				current.remaining--;
				time++;
			}
			if (current.remaining == 0) {
				completed++;
				current.quantum = 0;
				continue;
			}

			// for the rest quantum time we will work as SJF
			int remainingQuantum = current_quantum - (quarter_quantum + second_quarter_quantum);
			while (remainingQuantum > 0) {
				Process shortest_job_process = current;
				for (Process process : ready_queue) {
					if (process.remaining < shortest_job_process.remaining) {
						shortest_job_process = process;
					}
				}
				if (shortest_job_process != current) {
					current.quantum += remainingQuantum;
					ready_queue.add(current);
					break;
				}
				current.remaining--;
				time++;
				remainingQuantum--;
				if (current.remaining == 0) {
					completed++;
					current.quantum = 0;
					break;
				}
			}
			// if the process finished it's quantum and did't finish it's processes then add 2 to the quantum
			if (current.remaining > 0 && !ready_queue.contains(current)) {
				current.quantum += 2;
				ready_queue.add(current);
			}
		}
	}
}
