package org.sunspotworld;

import java.util.Calendar;

public class ClockState implements IState {

    StartApplication app;
    AirText disp;
    
    public ClockState( StartApplication app ){
        this.app = app;
        disp = new AirText( app.demoBoard );
    }
    
    public void switch1(){
        app.changeState( app.STATE_SET_CLOCK );
    }

    public void switch2(){
        app.changeState( app.STATE_SET_TIMER );
    }

    public void run(){
        System.out.println( "I'm now in clock mode" );
        while (true) {
            disp.setColor(0, 0, 255);
            disp.swingThis(getTimeText(), 3);
        }
    }
    
    
    /**
     * convert current time (HH:MM) to a string
     * @return
     */
    private String getTimeText() {
        Calendar cal = Calendar.getInstance();
        String hours = formatTimeUnit( cal.get(Calendar.HOUR_OF_DAY) );
        String minutes = formatTimeUnit(cal.get(Calendar.MINUTE));
        
        String time = hours + ":" + minutes;
        System.out.println("get TimeText=" + time);
        return time;
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
