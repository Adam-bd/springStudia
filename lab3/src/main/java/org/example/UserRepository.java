package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements IUserRepository{
    List<User> users = new ArrayList<>();

    public UserRepository() {
        load();
    }

    @Override
    public User getUser(String login) {
        for(User u : users) {
            if(login.equals(u.getLogin())){
                return u.cloneUser();
            }
        }
        return null;
    }

    @Override
    public List<User> getUsers() {
        List<User> copiedUsers = new ArrayList<>();
        for(User u : users) {
            copiedUsers.add(u.cloneUser());
        }
        return copiedUsers;
    }

    @Override
    public boolean update(User user) {
        if (user == null){
            return false;
        }
        for(int i = 0; i < users.size(); ++i) {
            if(users.get(i).getLogin().equals(user.getLogin())){
                users.set(i, user);
                save();
                return true;
            }
        }
        return false;
    }

    @Override
    public void load() {
        try (BufferedReader br = new BufferedReader(new FileReader("users.csv"));){
            String line;
            while((line = br.readLine()) != null) {
                User user = User.fromCSVLine(line);
                if(user != null) {
                    users.add(user);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("users.csv"));){
            for(User u : users){
                bw.write(u.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
