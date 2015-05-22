package application.generator;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateGenerator implements Generator{

    private final DateType dateType;

    private Date startDate = Date.valueOf("2015-05-01");
    private Date endDate = Date.valueOf("2015-05-30");
    private long startDateLong;
    private long endDateLong;

    private Time startTime = Time.valueOf("00:00:00");
    private Time endTime = Time.valueOf("01:00:00");
    private long startTimeLong;
    private long endTimeLong;

    private BooleanProperty monBooleanProperty = new SimpleBooleanProperty(true);
    private BooleanProperty tueBooleanProperty = new SimpleBooleanProperty(true);
    private BooleanProperty wenBooleanProperty = new SimpleBooleanProperty(true);
    private BooleanProperty thuBooleanProperty = new SimpleBooleanProperty(true);
    private BooleanProperty friBooleanProperty = new SimpleBooleanProperty(true);
    private BooleanProperty satBooleanProperty = new SimpleBooleanProperty(false);
    private BooleanProperty sunBooleanProperty = new SimpleBooleanProperty(false);

    private Calendar gregorianCalendar = new GregorianCalendar();

    private RandomDataGenerator randomDataGenerator;

    public DateGenerator(DateType dateType){
        this.dateType = dateType;
    }

    @Override
    public void initiateGenerator() {
        randomDataGenerator = new RandomDataGenerator();
        switch (dateType){
            case TIMESTAMP:
                startTimeLong = startTime.getTime();
                endTimeLong = endTime.getTime();
                startDateLong = startDate.getTime();
                // add one more day
                endDateLong = endDate.getTime() + 90000000;
                break;
            case TIME:
                startTimeLong = startTime.getTime();
                endTimeLong = endTime.getTime();
                break;
            case DATE:
                startDateLong = startDate.getTime();
                // add one more day
                endDateLong = endDate.getTime();
                break;
        }
    }


    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    @Override
    public Object generate() {
        switch (dateType){
            case DATE:
                //return new Date(randomDataGenerator.nextLong(startDateLong, endDateLong));
                Date d;
                while(true){
                    d = new Date(randomDataGenerator.nextLong(startDateLong, endDateLong));
                    if(monBooleanPropertyProperty().get()){
                        gregorianCalendar.setTime(d);
                        if(gregorianCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY){
                            break;
                        }
                    }
                    if(tueBooleanPropertyProperty().get()){
                        gregorianCalendar.setTime(d);
                        if(gregorianCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY){
                            break;
                        }
                    }
                    if(wenBooleanPropertyProperty().get()){
                        gregorianCalendar.setTime(d);
                        if(gregorianCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY){
                            break;
                        }
                    }
                    if(thuBooleanPropertyProperty().get()){
                        gregorianCalendar.setTime(d);
                        if(gregorianCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY){
                            break;
                        }
                    }
                   if(friBooleanPropertyProperty().get()){
                        gregorianCalendar.setTime(d);
                        if(gregorianCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
                            break;
                        }
                    }
                    if(satBooleanPropertyProperty().get()){
                        gregorianCalendar.setTime(d);
                        if(gregorianCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
                            break;
                        }
                    }
                    if(sunBooleanPropertyProperty().get()){
                        gregorianCalendar.setTime(d);
                        if(gregorianCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
                            break;
                        }
                    }
                }
                return d;

            case TIME:
                return new Time(randomDataGenerator.nextLong(startTimeLong, endTimeLong));
            case TIMESTAMP:
                Date dd = new Date(randomDataGenerator.nextLong(startDateLong, endDateLong));
                Time t = new Time(randomDataGenerator.nextLong(startTimeLong, endTimeLong));
                return Timestamp.valueOf(dd.toString() + " " + t.toString());
        }
        return timestamp;
    }

    // Start Date

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    // End Date

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    // Start Time

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    // End Time

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    // Monday

    public boolean getMonBooleanProperty() {
        return monBooleanProperty.get();
    }

    public BooleanProperty monBooleanPropertyProperty() {
        return monBooleanProperty;
    }

    public void setMonBooleanProperty(boolean monBooleanProperty) {
        this.monBooleanProperty.set(monBooleanProperty);
    }

    // Tuesday

    public boolean getTueBooleanProperty() {
        return tueBooleanProperty.get();
    }

    public BooleanProperty tueBooleanPropertyProperty() {
        return tueBooleanProperty;
    }

    public void setTueBooleanProperty(boolean tueBooleanProperty) {
        this.tueBooleanProperty.set(tueBooleanProperty);
    }

    // Wednesday

    public boolean getWenBooleanProperty() {
        return wenBooleanProperty.get();
    }

    public BooleanProperty wenBooleanPropertyProperty() {
        return wenBooleanProperty;
    }

    public void setWenBooleanProperty(boolean wenBooleanProperty) {
        this.wenBooleanProperty.set(wenBooleanProperty);
    }

    // Thursday

    public boolean getThuBooleanProperty() {
        return thuBooleanProperty.get();
    }

    public BooleanProperty thuBooleanPropertyProperty() {
        return thuBooleanProperty;
    }

    public void setThuBooleanProperty(boolean thuBooleanProperty) {
        this.thuBooleanProperty.set(thuBooleanProperty);
    }

    // Friday

    public boolean getFriBooleanProperty() {
        return friBooleanProperty.get();
    }

    public BooleanProperty friBooleanPropertyProperty() {
        return friBooleanProperty;
    }

    public void setFriBooleanProperty(boolean friBooleanProperty) {
        this.friBooleanProperty.set(friBooleanProperty);
    }

    // Saturday

    public boolean getSatBooleanProperty() {
        return satBooleanProperty.get();
    }

    public BooleanProperty satBooleanPropertyProperty() {
        return satBooleanProperty;
    }

    public void setSatBooleanProperty(boolean satBooleanProperty) {
        this.satBooleanProperty.set(satBooleanProperty);
    }

    // Sunday

    public boolean getSunBooleanProperty() {
        return sunBooleanProperty.get();
    }

    public BooleanProperty sunBooleanPropertyProperty() {
        return sunBooleanProperty;
    }

    public void setSunBooleanProperty(boolean sunBooleanProperty) {
        this.sunBooleanProperty.set(sunBooleanProperty);
    }

}
