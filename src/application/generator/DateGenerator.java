package application.generator;


import org.apache.commons.math3.random.RandomDataGenerator;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;

public class DateGenerator implements Generator{

    private final DateType dateType;

    private LocalDate startDate = LocalDate.now();
    private LocalDate endDate = LocalDate.now().plusYears(2);
    private long startDateLong;
    private long endDateLong;

    private LocalTime startTime = LocalTime.MIDNIGHT;
    private LocalTime endTime = LocalTime.NOON;
    private long startTimeLong;
    private long endTimeLong;

    private RandomDataGenerator randomDataGenerator;

    public DateGenerator(DateType dateType){
        this.dateType = dateType;
    }

    @Override
    public void initiateGenerator() {
        randomDataGenerator = new RandomDataGenerator();
        switch (dateType){
            case TIMESTAMP:
                startTimeLong = Time.valueOf(startTime).getTime();
                endTimeLong = Time.valueOf(endTime).getTime();
                startDateLong = Date.valueOf(startDate).getTime();
                endDateLong = Date.valueOf(endDate).getTime();
                System.out.println(endDateLong);
                System.out.println(startDateLong - endDateLong);
                break;
            case TIME:
                startTimeLong = Time.valueOf(startTime).getTime();
                endTimeLong = Time.valueOf(endTime).getTime();
                break;
            case DATE:
                startDateLong = Date.valueOf(startDate).getTime();
                endDateLong = Date.valueOf(endDate).getTime();
                break;
        }
    }


    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    @Override
    public Object generate() {
        switch (dateType){
            case DATE:
                return new Date(randomDataGenerator.nextLong(startDateLong, endDateLong));
            case TIME:
                return new Time(randomDataGenerator.nextLong(startTimeLong, endTimeLong));
            case TIMESTAMP:
                long a = randomDataGenerator.nextLong(startDateLong, endDateLong);
                long b = a % (24 * 60 * 60 * 1000);
                Date d = new Date(a - b);
                long c = randomDataGenerator.nextLong(startTimeLong, endTimeLong);
                return new Timestamp(d.getTime() + c );
        }
        return timestamp;
    }

    // Start Date

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    // End Date

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    // Start Time

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    // End Time

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
