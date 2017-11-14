package edu.rutgers.winlab.sim.core;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.PriorityQueue;

public class EventQueue {

	public final static double SECOND = 1;
	public final static double MILLI_SECOND = SECOND / 1000;
	public final static double MICRO_SECOND = MILLI_SECOND / 1000;

	private static EventQueue Default = new EventQueue();

	//	private LinkedList<Event> events = new LinkedList<Event>();
	private EvenComparator ec = new EvenComparator();
	private PriorityQueue<Event> events  = new PriorityQueue<Event>(100, ec);
	private double now;

	public double getNow() {
		return this.now;
	};

	public void setNow(double now) {
		this.now = now;
	};

	public static void AddEvent(double time, Action action, Object... args) {

		Default.addEvent(time, action, args);

	}

	public static void Run() {

		Default.run();

	}
	
	public static void RunSeconds(double duration) {
		Default.runSeconds(duration);
	}

	public static void Reset() {

		Default.reset();

	}

	public static double Now() {
		return Default.getNow();
	};

	private void addEvent(double time, Action action, Object...args) {
		assert time >= now;
		Event e = new Event(time, action, args);
		events.add(e);
	}

	private void run() {
		while(! events.isEmpty()) {
			Event e = events.poll();
			now = e.Time;
			e.DoEvent();
		}
	}
	
	private void runSeconds(double duration) {
		while(! events.isEmpty() && getNow() < duration) {
			Event e = events.poll();
			now = e.Time;
			e.DoEvent();
		}
	}


	private void reset() {

		assert events.isEmpty();
		now = 0;

	}

	private class Event {

		public double Time;
		public Action action;
		public Object[] Args;

		public void DoEvent() {

			action.execute(Args);

		}

		public Event(double time, Action action, Object... args) {

			this.Time = time;
			this.action = action;
			this.Args = args;

		}

	}

	public static Action Test1 = new Action() {

		@Override
		public void  execute(Object... args) {
			Integer val = (Integer) args[0];
			System.out.printf("[Test1] Now: %f val: %d\n", EventQueue.Now(), val);
			if (val.intValue() > 0)
				EventQueue.AddEvent(EventQueue.Now(), Test1, val - 1);
			return;
		}

	};

	public static Action Test2 = new Action() {

		@Override
		public void execute(Object... args) {
			Integer val = (Integer) args[0];
			System.out.printf("[Test2] Now: %f val: %d\n", EventQueue.Now(), val);
			if (val > 0)
				EventQueue.AddEvent(EventQueue.Now() + EventQueue.MILLI_SECOND, Test2, val - 1);
			else
				EventQueue.AddEvent(EventQueue.Now() + EventQueue.MILLI_SECOND, Test1, val + 5);

			return;
		}


	};

	public class EvenComparator implements Comparator<Event>{

		@Override
		public int compare(Event event_A, Event event_B) {
			// TODO Auto-generated method stub
			double result = event_A.Time - event_B.Time;
			return (result > 0)?1:-1;
		}

	}

	public static void main(String[] args) {

		EventQueue.AddEvent(EventQueue.Now() + EventQueue.MILLI_SECOND, Test1, 5);
		EventQueue.AddEvent(EventQueue.Now() + EventQueue.MILLI_SECOND, Test2, 5);
		EventQueue.Run();


	}

}
