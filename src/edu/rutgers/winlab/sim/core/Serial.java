package edu.rutgers.winlab.sim.core;

import java.util.ArrayList;
import java.util.List;

public class Serial<T> {
	
	public interface SerialAction<T> {
		public double execute(Serial<T> s, T parameter);
	}
	
	private class SerialEventDataStructure{
		public SerialAction<T> E;
		public T Parameter;
		
		public SerialEventDataStructure(SerialAction<T> e, T parameter){
			E = e;
			Parameter = parameter;
		}
		public SerialAction<T> getE() {
			return E;
		}
//		public void setE(SerialAction e) {
//			E = e;
//		}
		public T getParameter() {
			return Parameter;
		}
//		public void setParameter(T parameter) {
//			Parameter = parameter;
//		}
	}
	
	private List<SerialEventDataStructure> _events = new ArrayList<SerialEventDataStructure>();
    private List<SerialEventDataStructure> _lastEvents = new ArrayList<SerialEventDataStructure>();
    private List<Action> _serialFinishedHandlers = new ArrayList<>();
    
    public Serial(SerialAction<T> FirstEvent, T parameter){
//    	System.out.println("Serial()");
    	AddEvent(FirstEvent, parameter);
    	ScheduleNextEvent(EventQueue.Now());
    }
    
    public void AddEvent(SerialAction<T> e, T parameter){
    	_events.add(new SerialEventDataStructure(e, parameter));
    }
    
    public void AddLastEvent(SerialAction<T> e, T parameter){
    	_lastEvents.add(new SerialEventDataStructure(e, parameter));
    }
    
    public void AddSerialFinishedHandler(Action a) {
    	_serialFinishedHandlers.add(a);
    }
    
    public void RemoveSerialFinishedHandler(Action a) {
    	_serialFinishedHandlers.remove(a);
    }
    
    private void SerialFinished() {
    	for (Action a : _serialFinishedHandlers)
    		a.execute(this);
    }
   
    private void ScheduleNextEvent(double time) {
    	EventQueue.AddEvent(time, RunEventAction);
    }
    
    private Action RunEventAction = new Action() {

		@Override
		public double execute(Object... args) {
			if(_events.size() > 0){
	    		
	    		SerialEventDataStructure seds = _events.get(0);
	    		double execute_time = seds.getE().execute(Serial.this, seds.Parameter);
    			double nextEventTime = EventQueue.Now() + execute_time;
	    		_events.remove(0);
//	    		System.out.printf("RunEventAction.execute: now:%f %f %s%n", EventQueue.Now(), execute_time, seds.getParameter());
	    		ScheduleNextEvent(nextEventTime);
	    		
	    	}else{
	    		
	    		if(_lastEvents.size() > 0){
	    			
	    			SerialEventDataStructure seds = _lastEvents.get(0);
	    			double execute_time = seds.getE().execute(Serial.this, seds.Parameter);
	    			double nextEventTime = EventQueue.Now() + execute_time;
	    			_lastEvents.remove(0);
//	    			System.out.printf("RunEventAction.execute: now:%f %f %s%n", EventQueue.Now(), execute_time, seds.getParameter());
	    			ScheduleNextEvent(nextEventTime);
	    			
	    		}else{
	    			SerialFinished();
	    		}
	    		
	    	}
			return 0;
			
		}

		
    	
    };
    
    public static void main(String[] args){
    	SerialAction<Integer> a1 = new SerialAction<Integer>() {
			
			@Override
			public double execute(Serial<Integer> s, Integer parameter) {
				System.out.printf("A1: %d%n", parameter);
				return 1;
			}
		}; 
    	Serial<Integer> s = new Serial<Integer>(a1, 3);
    	s.AddEvent(new SerialAction<Integer>() {
			
			@Override
			public double execute(Serial<Integer> ss, Integer parameter) {
				int count = parameter;
				System.out.printf("A2: %d%n", count);
				if (count > 0) ss.AddEvent(this, count - 1);
				if (count > 0) ss.AddEvent(this, count - 2);
				return 2;
			}
		}, 3);
    	s.AddLastEvent(new SerialAction<Integer>() {
			
			@Override
			public double execute(Serial<Integer> ss, Integer parameter) {
				int count = parameter;
				System.out.printf("A3: %d%n", parameter);
				if (count > 0) ss.AddLastEvent(this, count - 1);
				if (count > 0) ss.AddEvent(a1, count - 1);
				return 3;
			}
		}, 3);
    	EventQueue.Run();
    }
    
}
