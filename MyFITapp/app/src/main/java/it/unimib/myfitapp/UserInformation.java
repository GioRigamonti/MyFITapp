package it.unimib.myfitapp;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class UserInformation {
    private String name;
    private String surname;
    private String email;
    private String sex;
    private Date date;
    private int age;
    private float weight;
    private int height;
    private String activity_level;
    private float IMC;

    public UserInformation(){
    }

    public UserInformation(String name,String surname, String email, String sex,
                   Date date, float weight, int height, String activity_level) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.sex = sex;
        this.date = date;
        this.age = setAge(date);
        this.weight = weight;
        this.height = height;
        this.activity_level = activity_level;
        this.IMC = setIMC(weight, height);
    }
    public UserInformation(String name,String surname, String email, String sex,
                           int age, float weight, int height, String activity_level) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.sex = sex;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.activity_level = activity_level;
        this.IMC = setIMC(weight,height);
    }


    public String getName(){
        return name;
    }
    public String getSurname(){
        return surname;
    }
    public String getEmail(){
        return email;
    }
    public String getSex(){
        return sex;
    }
    public int getAge(){return age;}
    public Date getDate(){
        return date;
    }
    public int setAge(Date date){
        GregorianCalendar today = new GregorianCalendar();
        GregorianCalendar bday = new GregorianCalendar();
        GregorianCalendar bdayThisYear = new GregorianCalendar();

        bday.setTime(date);
        bdayThisYear.setTime(date);
        bdayThisYear.set(Calendar.YEAR, today.get(Calendar.YEAR));

        int age = today.get(Calendar.YEAR) - bday.get(Calendar.YEAR);

        if(today.getTimeInMillis() < bdayThisYear.getTimeInMillis())
            age--;
        /*Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();

        int age = 0;

        birthDate.setTime(date);
        if (birthDate.after(today)) {
            throw new IllegalArgumentException("Can't be born in the future");
        }

        age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        // If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year
        if ( (birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
                (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH ))){
            age--;

            // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
        }else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH )) &&
                (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH ))){
            age--;
        }*/
        return age;
    }
    public float getWeight(){
        return weight;
    }
    public int getHeight(){
        return height;
    }
    public String getActivity_level(){
        return activity_level;
    }
    public float getIMC(){
        return IMC;
    }
    public float setIMC(float weight, int height){
        Float calcIMC = (float) (weight / ((height*0.01)*(height*0.01)));
        return calcIMC;
    }
}

