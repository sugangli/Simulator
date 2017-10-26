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
	
	public class HandleItemAction implements Action{
		
		@Override
		public void execute(Object... args){
			if(InnerQueue.getSize() == 0){
//				System.out.println("InnerQueue.getSize == 0");
				Busy = false;
				return;
			}
//			System.out.println("InnerQueue.getSize != 0");
			T item = InnerQueue.GetData();
			Serial<T> s = new Serial<T>(EventHandler, item);
			Action a = new Action() {
				
				@Override
				public void execute(Object... args) {
					EventQueue.AddEvent(EventQueue.Now(), HandleItemAction.this);
					return;
					
				}
			};
			
			s.AddSerialFinishedHandler(a);
			return;
		}

		
		
	}

	public void Enqueue(T item, boolean isPrioritized){
		
		InnerQueue.AddData(item, isPrioritized);
		if(!Busy){
			
			Busy = true;
//			System.out.println("Enqueue: HandleItemAction");
			EventQueue.AddEvent(EventQueue.Now(), new HandleItemAction());
			
		}
		
	}
}
