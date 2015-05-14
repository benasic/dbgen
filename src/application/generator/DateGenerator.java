package application.generator;


import java.sql.Date;
import java.util.concurrent.TimeUnit;

public class DateGenerator implements Generator{

    @Override
    public void initiateGenerator() {

    }

    private Date date = new Date(TimeUnit.DAYS.toMillis(356));
    @Override
    public Object generate() {
        return date;
    }
}
