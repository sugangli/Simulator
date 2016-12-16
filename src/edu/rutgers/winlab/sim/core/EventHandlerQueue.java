package edu.rutgers.winlab.sim.core;

public class EventHandlerQueue<T> {
	
	private SimulatorQueue<T> InnerQueue;
	private Serial.SerialAction<T> EventHandler;
	public boolean Busy;
	public SimulatorQueue<T> getInnerQueue() {
		return InnerQueue;
	}
	public void setInnerQueue(SimulatorQueue<T> innerQueue) {
		InnerQueue = innerQueue;
	}
	public boolean isBusy() {
		return Busy;
	}
	public void setBusy(boolean busy) {
		Busy = busy;
	}
	
	public EventHandlerQueue(SimulatorQueue<T> innerQueue, Serial.SerialAction<T> eventHandler){
		InnerQueue = innerQueue;
		EventHandler = eventHandler;
		Busy = false;
	}
	
	public class HandleItemAction extends Action{
		
		@Override
		public double execute(Object... args){
			if(InnerQueue.getSize() == 0){
				System.out.println("InnerQueue.getSize == 0");
				Busy = false;
				return 0;
			}
			System.out.println("InnerQueue.getSize != 0");
			T item = InnerQueue.GetData();
			Serial<T> s = new Serial<T>(EventHandler, item);
			Action a = new Action() {
				
				@Override
				public double execute(Object... args) {
					EventQueue.AddEvent(EventQueue.Now(), HandleItemAction.this);
					return 0;
					
				}
			};
			
			s.AddSerialFinishedHandler(a);
			return 0;
		}

		
		
	}

	public void Enqueue(T item, boolean isPrioritized){
		
		InnerQueue.AddData(item, isPrioritized);
		if(!Busy){
			
			Busy = true;
			System.out.println("Enqueue: HandleItemAction");
			EventQueue.AddEvent(EventQueue.Now(), new HandleItemAction());
			
		}
		
	}
}
