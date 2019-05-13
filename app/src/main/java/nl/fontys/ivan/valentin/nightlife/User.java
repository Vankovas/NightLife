package nl.fontys.ivan.valentin.nightlife;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String email, password, gender, transport, atmosphere, entertainment;

    private String dateOfBirth;
    private List<String> foodChoice;

    public String getEmail() {
        return email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public String getAtmosphere() {
        return atmosphere;
    }

    public void setAtmosphere(String atmosphere) {
        this.atmosphere = atmosphere;
    }

    public String getEntertainment() {
        return entertainment;
    }

    public void setEntertainment(String entertainment) {
        this.entertainment = entertainment;
    }

    public List<String> getFoodChoice() {
        return foodChoice;
    }

    public void setFoodChoice(List<String> foodChoice) {
        this.foodChoice = foodChoice;
    }

    public List<String> getMusicChoice() {
        return musicChoice;
    }

    public void setMusicChoice(List<String> musicChoice) {
        this.musicChoice = musicChoice;
    }

    private List<String> musicChoice;

    public User() {

    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.dateOfBirth = "10/1/1998";
        this.gender = "";
        this.transport = "";
        this.foodChoice = new ArrayList<String>();
        this.atmosphere = "";
        this.musicChoice = new ArrayList<String>();
        this.entertainment = "";
    }

    @Exclude
    public String getUserInfoString() {
        return String.format("\n" + "Email:" + this.email + "\n" +
                "Password:" + this.getPassword() + "\n" + "" +
                "DOB:" + this.getDateOfBirth() + "\n" +
                "Gender:" + this.getGender() + "\n" +
                "Transport:" + this.getTransport() + "\n" +
                "FoodChoice:" + this.getFoodChoice() + "\n" +
                "Atmosphere:" + this.getAtmosphere() + "\n" +
                "MusicChoice:" + this.getMusicChoice() + "\n" +
                "Entertainment:" + this.getEntertainment() + "\n");
    }
}



