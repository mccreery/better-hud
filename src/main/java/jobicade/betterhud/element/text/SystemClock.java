package jobicade.betterhud.element.text;

import java.util.Date;

public class SystemClock extends Clock {
    public SystemClock() {
        super("systemClock");
    }

    @Override
    protected Date getDate() {
        return new Date();
    }
}
