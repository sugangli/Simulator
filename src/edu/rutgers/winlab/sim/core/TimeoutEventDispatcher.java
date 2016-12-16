package edu.rutgers.winlab.sim.core;

public class TimeoutEventDispatcher {

	private double timeouttime;
	private boolean active;
	private TimeoutHandle timeouthandle;
	private Object[] args;
	private TimeoutHandle th;
	private Action action;
	public TimeoutEventDispatcher ted;

	public double getTimeoutTime() {
		return timeouttime;
	};

	public void setTimeoutTime(double timeouttime) {
		this.timeouttime = timeouttime;
	};

	public boolean getActive() {
		return active;
	};

	public void setActive(boolean active) {
		this.active = active;
	};

	public TimeoutEventDispatcher(double timeouttime, Action action, Object... args) {

		this.timeouttime = timeouttime;
		this.args = args;
		this.active = true;
		this.action = action;

		EventQueue.AddEvent(timeouttime, new TimeoutHandle());

	}

	public void Delay(double newtime) {

		assert newtime > this.timeouttime;
		this.timeouttime = newtime;

	}

	public void Cancel() {

		this.active = false;

	}

	private class TimeoutHandle extends Action {

		@Override
		public double execute(Object... args) {
			if (!active)
				return 0;
			if (timeouttime == EventQueue.Now()) {
				System.out.println("It's Now ! timeoutime :" + timeouttime);
				action.execute(TimeoutEventDispatcher.this.args);
				active = false;

			} else {

				System.out.println("Set timeoutime :" + timeouttime);
				EventQueue.AddEvent(timeouttime, this);

			}
			return 0;

		}

	}


	
	public static class TestAction extends Action {
		@Override
		public double execute(Object... args) {
			Integer val = 0;
			System.out.printf("[%f] Timeout! Test %d\n", EventQueue.Now(), val);
			return 0;
		}

	}
	
	

	public static void main(String[] args) {
		TimeoutEventDispatcher ted;
//		// TimeoutAction toa = new TimeoutAction((Integer) 0);
//		ted = new TimeoutEventDispatcher(EventQueue.Now() + EventQueue.MILLI_SECOND, new TestAction());
////		ted.Delay(EventQueue.Now() + 2 * EventQueue.MILLI_SECOND);
//
//		// TimeoutAction1 toa1 = new TimeoutAction1((Integer) 1);
//		EventQueue.AddEvent(EventQueue.Now() + 1.5 * EventQueue.MILLI_SECOND, new Action() {
//			@Override
//			public void execute(Object... args) {
//				Integer val = 1;
//				System.out.printf("[%f] Timeout! Test2 %d\n", EventQueue.Now(), val);
//				ted.Delay(EventQueue.Now() + 2 * EventQueue.MILLI_SECOND);
//			}
//		});
////
////		// TimeoutAction2 toa2 = new TimeoutAction2((Integer) 2);
//		EventQueue.AddEvent(EventQueue.Now() + 3499 * EventQueue.MILLI_SECOND, new Action() {
//			@Override
//			public void execute(Object... args) {
//				Integer val = 2;
//				System.out.printf("[%f] Cancel it! Test 3%d\n", EventQueue.Now(), val);
//				ted.Cancel();
//			}
//
//		});
//
//		EventQueue.Run();

	}

}
