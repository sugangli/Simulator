package edu.rutgers.winlab.sim.core;


public class TimeoutEventDispatcher {

    private double _timeoutTime;
    private boolean _active;
    private final Object[] _args;
    private final Action _action;

    public TimeoutEventDispatcher(double timeouttime, Action action, Object... args) {
        this._timeoutTime = timeouttime;
        this._args = args;
        this._active = true;
        this._action = action;
        EventQueue.AddEvent(timeouttime, this::_timeout);
    }

    public double getTimeoutTime() {
        return _timeoutTime;
    }

    public boolean isActive() {
        return _active;
    }

    public void delay(double newTime) {
        if (newTime <= this._timeoutTime) {
            throw new IllegalArgumentException(String.format("NewTime(%d) <= TimeoutTime(%d)", newTime, _timeoutTime));
        }
        this._timeoutTime = newTime;
    }

    public void cancel() {
        this._active = false;
    }

    private void _timeout(Object... args) {
        if (!_active) {
            System.out.printf("[%d] Cancelled!%n", EventQueue.Now());
            return;
        }
        if (_timeoutTime == EventQueue.Now()) {
            System.out.printf("[%d] It's Now ! timeoutime :%d%n", EventQueue.Now(), _timeoutTime);
            _action.execute(_args);
            _active = false;
        } else {
            System.out.printf("[%d] Set timeoutime : %d%n", EventQueue.Now(), _timeoutTime);
            EventQueue.AddEvent(_timeoutTime, this::_timeout);
        }
    }

}