package org.sunspotworld;

import com.sun.spot.peripheral.Spot;
import com.sun.spot.sensorboard.peripheral.IAccelerometer3D;
import com.sun.spot.sensorboard.peripheral.IAccelerometer3DThresholdListener;
import com.sun.spot.sensorboard.peripheral.LEDColor;
import java.util.Calendar;

public class SetClockState implements IState {

    protected StartApplication app;
    
    private int control = 0;
    private int[] timeset = new int[4];
    private double previous_Z;
    private double previous_X;
    
    public SetClockState( StartApplication app ){
        this.app = app;
    }
    
    /**
     * Cycle through the various "time units"
     */
    public void switch1(){
       control = (control + 1) % 5;
       System.out.println( control );
       
       if ( control == 0 ){
           saveTime();
           app.changeState( app.STATE_CLOCK );
       }
       else {
           setControlMode();
       }
       
    }

    /** 
     * Increase the unit number
     */
    public void switch2(){
        increaseTimeUnit();
    }

    public void run(){
        System.out.println( "I'm now setting the clock!!");
        //Add accelerometer listener
        //We use Z_AXIS to change the control mode and use X_AXIS to increase the time unit
        app.accel.setThresholds(IAccelerometer3D.X_AXIS, -1, 1, false);
        app.accel.setThresholds(IAccelerometer3D.Z_AXIS, -2, 2, false);
        app.accel.enableThresholdEvents(IAccelerometer3D.Z_AXIS, true);
        app.accel.addIAccelerometer3DThresholdListener(new IAccelerometer3DThresholdListener() {

            public void thresholdChanged(IAccelerometer3D accel, int axis, double low, double high, boolean relative) {
            }

            public void thresholdExceeded(IAccelerometer3D accel, int axis, double val, boolean relative) {
                if (axis == IAccelerometer3D.Z_AXIS) {
//                    System.out.println("Z:" + val);
                    if (previous_Z < 0 && val > 0) {  //passed the first stage and ready to set the control mode
                        setControlMode();
//                        System.out.println("reset Z");
                        //reset previous_Z so that control mode will not be modified until the next time
                        accel.enableThresholdEvents(IAccelerometer3D.X_AXIS, true);
                        previous_Z = 0;
                    } else {
                        //init the first stage
                        previous_Z = val;
//                        System.out.println("first stage");
                    }
                    accel.enableThresholdEvents(IAccelerometer3D.Z_AXIS, true);
                } else if (axis == IAccelerometer3D.X_AXIS && control > 0) {
//                    System.out.println("X:" + val);
                    if (previous_X < 0 && val > 0) {  //passed the first stage and ready to set the control mode
                        increaseTimeUnit();
//                        System.out.println("reset X");
                        //reset previous_Z so that control mode will not be modified until the next time
                        previous_X = 0;
                    } else {
                        //init the first stage
                        previous_X = val;
//                        System.out.println("first stage X");
                    }
                    accel.enableThresholdEvents(IAccelerometer3D.X_AXIS, true);
                }
            }
        });
    }
    
    private void saveTime(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, timeset[0] * 10 + timeset[1]);
        cal.set(Calendar.MINUTE, timeset[2] * 10 + timeset[3]);
        System.out.println("set time=" + cal.getTime());
        Spot.getInstance().getPowerController().setTime( cal.getTime().getTime());
    }
        
    /**
     * increase each time unit by press switch 2 if in time setting mode.
     */
    private void increaseTimeUnit() {
        //increase the time unit
        if (control == 0) {
            return;
        }
        System.out.println("sw2:" + control);
        switch (control) {
            case 1:
                timeset[control - 1] = (timeset[control - 1] + 1) % 3;
                break;
            case 3:
                timeset[control - 1] = (timeset[control - 1] + 1) % 6;
                break;
            case 2:
                if (timeset[0] == 2) {
                    timeset[control - 1] = (timeset[control - 1] + 1) % 4;
                } else {
                    timeset[control - 1] = (timeset[control - 1] + 1) % 10;
                }
                break;
            case 4:
                timeset[control - 1] = (timeset[control - 1] + 1) % 10;
                break;
        }
        printTimeSet();
        displayTimeUnit(timeset[control - 1]);
    }
    
    /**
     * Set control mode by press switch 1
     * 0->running, 1->Hour0[0-2], 2->Hour1[0-9],3->Min0[0-5],4->Min1[0-9]
     */
    private void setControlMode() {
        System.out.println("sw1:" + control);
        clearLED();
//        if (!timerSetting) {
            Calendar cal = Calendar.getInstance();
            System.out.println("get Time=" + cal.getTime());
            int h = cal.get(Calendar.HOUR_OF_DAY);
            int m = cal.get(Calendar.MINUTE);
            timeset[0] = h / 10;
            timeset[1] = h % 10;
            timeset[2] = m / 10;
            timeset[3] = m % 10;
//        }
            setLED(control - 1, LEDColor.RED);
            displayTimeUnit(timeset[control - 1]);
//        }
    }
    
    private void clearLED() {
        for (int i = 0; i < 8; i++) {
            app.leds[i].setOff();
        }
    }
    
    /**
     * Set the color of one led by its index
     * @param index
     * @param color
     */
    private void setLED(int index, LEDColor color) {
        app.leds[index].setColor(color);
        if (!app.leds[index].isOn()) {
            app.leds[index].setOn();
        }
    }

    /**
     * Display binary format of the number n with LED
     * @param n
     */
    private void displayTimeUnit(int n) {
        for (int i = 4; i < 8; i++) {
            app.leds[i].setOff();
        }
        System.out.println("display time unit:" + n);
        for (int i = 0; i < 4; i++) {
            if ((n & 1) == 1) {
                setLED(7 - i, LEDColor.GREEN);
                System.out.println("display time unit set led:" + (7 - i));
            }
            n = n >> 1;
        }
    }

    /**
     * Print timeSet array for logs
     */
    private void printTimeSet() {
        for (int i = 0; i < timeset.length; i++) {
            System.out.print(timeset[i]);
        }
        System.out.println();
    }
}
