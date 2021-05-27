package it.unimib.myfitapp;

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
        this.age = setUserAge(date);
        this.weight = weight;
        this.height = height;
        this.activity_level = activity_level;
        this.IMC = setIMC(sex,age,weight,height);
    }

    public String getUserName(){
        return name;
    }
    public String getUserSurname(){
        return surname;
    }
    public String getUserEmail(){
        return email;
    }
    public String getUserSex(){
        return sex;
    }
    public Date getUserAge(){
        return date;
    }
    public int setUserAge(Date date){
        GregorianCalendar today = new GregorianCalendar();
        GregorianCalendar bday = new GregorianCalendar();
        GregorianCalendar bdayThisYear = new GregorianCalendar();

        bday.setTime(date);
        bdayThisYear.setTime(date);
        bdayThisYear.set(Calendar.YEAR, today.get(Calendar.YEAR));

        int age = today.get(Calendar.YEAR) - bday.get(Calendar.YEAR);

        if(today.getTimeInMillis() < bdayThisYear.getTimeInMillis())
            age--;

        return age;
    }
    public float getUserWeight(){
        return weight;
    }
    public int getUserHeight(){
        return height;
    }
    public String getUserActivity_level(){
        return activity_level;
    }
    public float getIMC(){
        return IMC;
    }
    public float setIMC(String sex, int age, float weight, int height){
        return weight / ((height/100)*(height/100));
    }
}

