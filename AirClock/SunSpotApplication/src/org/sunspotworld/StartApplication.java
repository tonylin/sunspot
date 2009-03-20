/*
 * StartApplication.java
 *
 * Created on Mar 19, 2009 11:20:14 PM;
 */
package org.sunspotworld;

import com.sun.spot.peripheral.Spot;
import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.IAccelerometer3D;
import com.sun.spot.sensorboard.peripheral.IAccelerometer3DThresholdListener;
import com.sun.spot.sensorboard.peripheral.ISwitch;
import com.sun.spot.sensorboard.peripheral.ISwitchListener;
import com.sun.spot.sensorboard.peripheral.ITriColorLED;

import com.sun.spot.sensorboard.peripheral.LEDColor;
import java.util.Calendar;
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

    private final ITriColorLED leds[] = EDemoBoard.getInstance().getLEDs();
    private final ISwitch sw1 = EDemoBoard.getInstance().getSwitches()[0];
    private final ISwitch sw2 = EDemoBoard.getInstance().getSwitches()[1];
    private IAccelerometer3D accel = EDemoBoard.getInstance().getAccelerometer();
    private int control = 0;
    private boolean timerSetting = false;
    private int[] timeset = new int[4];

    /**
     * The rest is boiler plate code, for Java ME compliance
     *
     * startApp() is the MIDlet call that starts the application.
     */
    protected void startApp() throws MIDletStateChangeException {
        System.out.println("StartApp");
        // Initialize and start the application
        EDemoBoard demoBoard = EDemoBoard.getInstance();
        AirText disp = new AirText(demoBoard);
        //Initialize switches
        sw1.addISwitchListener(new ISwitchListener() {

            public void switchPressed(ISwitch sw) {
                setControlMode();
            }

            public void switchReleased(ISwitch sw) {
            }
        });
        sw2.addISwitchListener(new ISwitchListener() {

            public void switchPressed(ISwitch sw) {
                increaseTimeUnit();
            }

            public void switchReleased(ISwitch sw) {
            }
        });
        //Add accelerometer listener
        accel.enableThresholdEvents(IAccelerometer3D.X_AXIS, true);
        accel.enableThresholdEvents(IAccelerometer3D.Z_AXIS, true);
        accel.setThresholds(IAccelerometer3D.X_AXIS, -1, 1, !false);
        accel.setThresholds(IAccelerometer3D.Z_AXIS, -1, 1, !false);
        accel.addIAccelerometer3DThresholdListener(new IAccelerometer3DThresholdListener() {

            public void thresholdChanged(IAccelerometer3D accel, int axis, double low, double high, boolean relative) {
                System.out.println("threshold changed:"+","+axis+","+low+","+high);
            }

            public void thresholdExceeded(IAccelerometer3D accel, int axis, double val, boolean relative) {
                System.out.println("threshold Exceed:"+axis+","+val);
            }
            
        });
        // Main loop of the application
        while (true) {
            if (control == 0) {
                disp.setColor(0, 0, 255);
                disp.swingThis(getTimeText(), 10);
            }
        }
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
        clearLED();// turn off the LEDs when we exit
        accel.enableThresholdEvents(IAccelerometer3D.X_AXIS, false);
        accel.enableThresholdEvents(IAccelerometer3D.Z_AXIS, false);
    }

    /**
     * Set control mode by press switch 1
     * 0->running, 1->Hour0[0-2], 2->Hour1[0-9],3->Min0[0-5],4->Min1[0-9]
     */
    private void setControlMode() {
                //set control mode to set the value of each time unit
                control = (control + 1) % 5;
                System.out.println("sw1:" + control);
                clearLED();
                if (control == 0) {
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, timeset[0] * 10 + timeset[1]);
                    cal.set(Calendar.MINUTE, timeset[2] * 10 + timeset[3]);
                    System.out.println("set time=" + cal.getTime());
                    Spot.getInstance().getPowerController().setTime(cal.getTime().getTime());
                    timerSetting = false;
                } else {
                    if (!timerSetting) {
                        Calendar cal = Calendar.getInstance();
                        System.out.println("get Time=" + cal.getTime());
                        int h = cal.get(Calendar.HOUR_OF_DAY);
                        int m = cal.get(Calendar.MINUTE);
                        timeset[0] = h / 10;
                        timeset[1] = h % 10;
                        timeset[2] = m / 10;
                        timeset[3] = m % 10;
                        timerSetting = true;
                    }
                    setLED(control - 1, LEDColor.RED);
                    displayTimeUnit(timeset[control - 1]);
                }
    }

    /**
     * increase each time unit by press switch 2 if in time setting mode.
     */
    private void increaseTimeUnit() {
                //increase the time unit
                System.out.println("sw2:" + control);
                if(control==0) {
                    return;
                }
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
     * convert current time (HH:MM) to a string
     * @return
     */
    private String getTimeText() {
        Calendar cal = Calendar.getInstance();
        String time = formatTimeUnit(cal.get(Calendar.HOUR_OF_DAY)) + ":" + formatTimeUnit(cal.get(Calendar.MINUTE));
        System.out.println("get TimeText=" + time);
        return time;
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

    /**
     * Turn off the LEDs
     */
    private void clearLED() {
        for (int i = 0; i < 8; i++) {
            leds[i].setOff();
        }
    }

    /**
     * Set the color of one led by its index
     * @param index
     * @param color
     */
    private void setLED(int index, LEDColor color) {
        leds[index].setColor(color);
        if (!leds[index].isOn()) {
            leds[index].setOn();
        }
    }

    /**
     * Display binary format of the number n with LED
     * @param n
     */
    private void displayTimeUnit(int n) {
        for (int i = 4; i < 8; i++) {
            leds[i].setOff();
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
     * Format an integer to a string. If the integer is less than 10, it will append "0" as a prifix.
     * For example, 6 => "06" and 55 => "55"
     * @param unit
     * @return
     */
    private String formatTimeUnit(int unit) {
        return (unit < 10 ? "0" : "") + unit;
    }
}
