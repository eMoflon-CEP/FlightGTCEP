package flight.util;

public class Measurement {
	
	final public static double NANOSECONDS = 1E9;
	final public static int MB = 1024*1024;
	
	private Runtime runtime;
	
	private double start;
	private double end;
	private double startPause;
	private double endPause;
	private double pause;
	private long usedMemory;
	
	public Measurement() {
		runtime = Runtime.getRuntime();
	}
	
	public void start() {
		pause = 0;
		start = System.nanoTime();
	}
	
	public void end() {
		end = System.nanoTime();
		usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / MB;
	}
	
	public void pause() {
		startPause = System.nanoTime();
	}
	
	public void resume() {
		endPause = System.nanoTime();
		pause += endPause - startPause;
		System.out.println("Resumed after: " + (endPause - startPause)/NANOSECONDS +"s.");
	}
	
	public double durationSeconds() {
		return (end-start-pause)/NANOSECONDS;
	}
	
	public double durationNanoSeconds() {
		return end-start-pause;
	}
	
	public long usedMemoryMB() {
		return usedMemory;
	}
	
	@Override
	public String toString() {
		return "took: "+durationSeconds()+"s ("+durationNanoSeconds()+"ns) and required "+usedMemory+"MB of memory.";
	}
}
