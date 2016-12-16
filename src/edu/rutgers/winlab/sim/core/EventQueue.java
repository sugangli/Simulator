package edu.rutgers.winlab.sim.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class EventQueue {

	public final static double SECOND = 1;
	public final static double MILLI_SECOND = SECOND / 1000;
	public final static double MICRO_SECOND = MILLI_SECOND / 1000;

	private static EventQueue Default = new EventQueue();

	private LinkedList<Event> events = new LinkedList<Event>();
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

	public static void Reset() {

		Default.reset();

	}

	public static double Now() {
		return Default.getNow();
	};

	private void run() {

		while (!events.isEmpty()) {

			Event e = events.getFirst();
			System.out.printf("run() now:%f%s\n", e.Time, e.action);
			now = e.Time;
			e.DoEvent();
			events.removeFirst();

		}

	}

	private void addEvent(double time, Action action, Object... args) {

		assert time >= now;
		Event e = new Event(time, action, args);
			ListIterator<Event> iterator =  events.listIterator();
			while(iterator.hasNext()) {
				Event ex = iterator.next();
				if (ex.Time > time) {
					iterator.previous();
					iterator.add(e);
					e = null;
					break;
				}
			}
			if (e != null)
				iterator.add(e);

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
		public double  execute(Object... args) {
			Integer val = (Integer) args[0];
			System.out.printf("[Test1] Now: %f val: %d\n", EventQueue.Now(), val);
			if (val.intValue() > 0)
				EventQueue.AddEvent(EventQueue.Now(), Test1, val - 1);
			return 0;
		}

	};

	public static Action Test2 = new Action() {

		@Override
		public double execute(Object... args) {
			Integer val = (Integer) args[0];
			System.out.printf("[Test2] Now: %f val: %d\n", EventQueue.Now(), val);
			if (val > 0)
				EventQueue.AddEvent(EventQueue.Now() + EventQueue.MILLI_SECOND, Test2, val - 1);
			else
				EventQueue.AddEvent(EventQueue.Now() + EventQueue.MILLI_SECOND, Test1, val + 5);

			return 0;
		}


	};

	public static void main(String[] args) {
		
//		LinkedList<Integer> test = new LinkedList<>();
//		test.add(1);
//		test.add(2);
//		test.add(3);
//ListIterator<Integer> li = test.listIterator();
//System.out.println(li.next());
//li.previous();
//System.out.println(li.next());
//System.out.println(li.next());
//System.out.println(li.next());
//System.out.println(li.next());
		
		EventQueue.AddEvent(EventQueue.Now() + EventQueue.MILLI_SECOND, Test1, 5);
		EventQueue.AddEvent(EventQueue.Now() + EventQueue.MILLI_SECOND, Test2, 5);
		EventQueue.Run();
		// System.out.println("[Test1] Now: " + EventQueue.Now() + " val: " +
		// 1);

	}

}
