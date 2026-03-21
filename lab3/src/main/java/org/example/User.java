package org.example;

import org.apache.commons.codec.digest.DigestUtils;

public class User {
    private String login;
    private String password;
    private Role role;
    private String rentedVehicleId;

    public User(String login, String password, Role role, String rentedVehicleId) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.rentedVehicleId = rentedVehicleId;
    }

    public User(User other){
        this.login = other.login;
        this.password = other.password;
        this.role = other.role;
        this.rentedVehicleId = other.rentedVehicleId;
    }

    public static User fromCSVLine(String line){
        String[] data = line.split(";", -1);
        String rentedVehId = data[3].isEmpty() ? null : data[3];

        return switch(data[2]){
            case "ADMIN", "USER" -> new User(data[0], data[1], Role.valueOf(data[2]), rentedVehId);
            default -> null;
        };
    }

    public String toCSV(){
        String rentedVehIdToCSV = (getRentedVehicleId() == null) ? "" : getRentedVehicleId();
        return getLogin() + ";" + getPassword() + ";" + getRole().name() + ";" + rentedVehIdToCSV;
    }

    @Override
    public String toString(){
        String str = " has rented a vehicle:";
        if(getRentedVehicleId() == null) { str = " hasn't rented a vehicle:"; }
        return "User: " + getLogin() + str;
    }

    public String toStringUserAccountDetails(){
        String string = "have";
        if(getRentedVehicleId() == null) { string = "haven't"; }
        return "You " + string + " rented a vehicle which ";
    }

    public User cloneUser(){
        return new User(this);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getRentedVehicleId() {
        return rentedVehicleId;
    }

    public void setRentedVehicleId(String rentedVehicleId) {
        this.rentedVehicleId = rentedVehicleId;
    }
}
