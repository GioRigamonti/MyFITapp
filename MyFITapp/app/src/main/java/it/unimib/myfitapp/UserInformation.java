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
    private double weight;
    private int height;
    private String activity_level;
    private double IMC;

    public UserInformation(){
    }

    public UserInformation(String name,String surname, String email, String sex,
                   Date date, double weight, int height, String activity_level) {
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
                           int age, double weight, int height, String activity_level) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.sex = sex;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.activity_level = activity_level;
        this.IMC = setIMC(weight, height);
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
        return age;
    }

    public double getWeight(){
        return weight;
    }

    public int getHeight(){
        return height;
    }

    public String getActivity_level(){
        return activity_level;
    }

    public double getIMC(){
        return IMC;
    }

    public double setIMC(double weight, int height){
        double imc = (weight/(Math.pow(height*0.01,2)));
        Log.d("USER", String.valueOf(imc));
        return imc;
    }
}
