package com.example.santiago.adopet;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Mascota {
    public String Name;
    public String Sex;
    public String Type;
    public int Age;
    public boolean Vaccine;
    public String Photo;
    public String Raza;
    public String Direccion;
    public String Ciudad;
    public String Phone;

    public Mascota(){}


    public Mascota(String name, String sex, String type, int age, boolean vaccine, String photo, String raza, String direccion, String ciudad, String phone) {
        Name = name;
        Sex = sex;
        Type = type;
        Age = age;
        Vaccine = vaccine;
        Photo = photo;
        Raza = raza;
        Direccion = direccion;
        Ciudad = ciudad;
        Phone = phone;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("Name", Name);
        result.put("Sex",Sex);
        result.put("Type",Type);
        result.put("Age",Age);
        result.put("Vaccine",Vaccine);
        result.put("Photo",Photo);
        result.put("Raza",Raza);
        result.put("Direccion",Direccion);
        result.put("Ciudad",Ciudad);
        result.put("Phone",Phone);
        return result;
    }
}
