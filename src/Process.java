
public class Process {
	public String name;
	public int arrival;
	public int burst;
	public int priority;
	public int quantum;
	
	public int startTime;
	public int finishTime;
	
	public Process(String name, int arrival, int burst, int priority, int quantum) {
		super();
		this.name = name;
		this.arrival = arrival;
		this.burst = burst;
		this.priority = priority;
		this.quantum = quantum;
	}
	
	
}
