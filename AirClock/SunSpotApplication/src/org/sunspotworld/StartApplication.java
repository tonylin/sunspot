/*
 * StartApplication.java
 *
 * Created on Mar 19, 2009 11:20:14 PM;
 */
package org.sunspotworld;

import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.IAccelerometer3D;
import com.sun.spot.sensorboard.peripheral.ISwitch;
import com.sun.spot.sensorboard.peripheral.ISwitchListener;
import com.sun.spot.sensorboard.peripheral.ITriColorLED;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * The startApp method of this class is called by the VM to start the
 * application.
 * 
 * The manifest specifies this class as MIDlet-1, which means it will
 * be selected for execution.
 */
public class StartApplication extends MIDlet {

    public final EDemoBoard demoBoard = EDemoBoard.getInstance();
    public final ITriColorLED leds[] = EDemoBoard.getInstance().getLEDs();
    public IAccelerometer3D accel = EDemoBoard.getInstance().getAccelerometer();
    
    private final ISwitch sw1 = EDemoBoard.getInstance().getSwitches()[0];
    private final ISwitch sw2 = EDemoBoard.getInstance().getSwitches()[1];
    
    public final int STATE_CLOCK = 0;
    public final int STATE_SET_CLOCK = 1;
    public final int STATE_SET_TIMER = 2;
    
    
    private IState currentState;
    private ClockState clock = new ClockState(this);
    private SetClockState clockSetter = new SetClockState(this);
    private SetTimerState timerSetter = new SetTimerState( this );
    
    /**
     * The rest is boiler plate code, for Java ME compliance
     *
     * startApp() is the MIDlet call that starts the application.
     */
    protected void startApp() throws MIDletStateChangeException {
        System.out.println("StartApp");
        // Adding the listeners for the switches.
        sw1.addISwitchListener(new ISwitchListener() {

            public void switchPressed(ISwitch sw) {
                System.out.println( "Switch 1 pressed!" );
                currentState.switch1();
            }

            public void switchReleased(ISwitch sw) {
            }
        });
        sw2.addISwitchListener(new ISwitchListener() {

            public void switchPressed(ISwitch sw) {
                System.out.println( "Switch 2 pressed!");
                currentState.switch2();
            }

            public void switchReleased(ISwitch sw) {
            }
        });
        
        changeState( STATE_CLOCK );
    }
    
    /**
     * Change the state
     * @param state - See constants STATE_*
     */
    public void changeState( int state ){
        System.out.println( "Changing clock state to: " + state );
        switch( state ){
            case STATE_CLOCK:
                currentState = clock;
                break;
            case STATE_SET_CLOCK:
                currentState = clockSetter;
                break;
            case STATE_SET_TIMER:
                currentState = timerSetter;
                break;
            default:
                throw new RuntimeException( "Undefined state for the clock" );
        }
        currentState.run();
    }

    /**
     * This will never be called by the Squawk VM.
     */
    protected void pauseApp() {
    }

    /**
     * Called if the MIDlet is terminated by the system.
     * I.e. if startApp throws any exception other than MIDletStateChangeException,
     * if the isolate running the MIDlet is killed with Isolate.exit(), or
     * if VM.stopVM() is called.
     *
     * It is not called if MIDlet.notifyDestroyed() was called.
     */
    protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
        // turn off the leds
        for (int i = 0; i < 8; i++) {
            leds[i].setOff();
        }
        
        // Disable accelerometer events
        accel.enableThresholdEvents(IAccelerometer3D.X_AXIS, false);
        accel.enableThresholdEvents(IAccelerometer3D.Z_AXIS, false);
    }

    
}
