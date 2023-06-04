package com.user;

import com.util.DBUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class UserManagement {
    public void readUserAPI() throws Exception{
        try {
            //create url
            URL url = new URL("https://jsonplaceholder.typicode.com/users");

            //create connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            //read respond
            InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder respond = new StringBuilder();
            String jsonLine;
            while ((jsonLine = bufferedReader.readLine()) != null){
                respond.append(jsonLine);
            }
            bufferedReader.close();
//            System.out.println(respond.toString());

            Connection conn = DBUtil.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select id from Users");
            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<Integer> ids = new ArrayList<>();
            while (resultSet.next()){
                ids.add(resultSet.getInt(1));
            }

            //get user data
            JSONArray jsonArray = new JSONArray(respond.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject userObject = (JSONObject) jsonArray.get(i);
                User user = new User();
                user.setId(Integer.parseInt(userObject.get("id").toString()));
                user.setName(userObject.get("name").toString());
                user.setUserName(userObject.get("username").toString());
                user.setEmail(userObject.get("email").toString());

                JSONObject addObject = (JSONObject) userObject.get("address");
                    user.address = new Address();
                    user.address.setStreet(addObject.get("street").toString());
                    user.address.setSuite(addObject.get("suite").toString());
                    user.address.setCity(addObject.get("city").toString());
                    user.address.setZipcode(addObject.get("zipcode").toString());
                    JSONObject geoObject = (JSONObject) addObject.get("geo");
                        user.address.geo = new Geography();
                        user.address.geo.setLat(geoObject.get("lat").toString());
                        user.address.geo.setLng(geoObject.get("lng").toString());


                user.setPhone(userObject.get("phone").toString());
                user.setWebsite(userObject.get("website").toString());

                JSONObject companyObject = (JSONObject) userObject.get("company");
                    user.company = new Company();
                    user.company.setName(companyObject.get("name").toString());
                    user.company.setCatchPhrase(companyObject.get("catchPhrase").toString());
                    user.company.setBs(companyObject.get("bs").toString());

                for(Integer id : ids){
                    try {
                        if(user.getId() != id){
                            CallableStatement callableStatement = conn.prepareCall("{call addUser(?, ?, ?, ? , ?, ?, ?, ?)}");
                            callableStatement.setInt(1, user.getId());
                            callableStatement.setString(2, user.getName());
                            callableStatement.setString(3, user.getUserName());
                            callableStatement.setString(4, user.getEmail());
                            callableStatement.setString(5, String.valueOf(addObject));
                            callableStatement.setString(6, user.getPhone());
                            callableStatement.setString(7, user.getWebsite());
                            callableStatement.setString(8, String.valueOf(companyObject));
                            if(callableStatement.executeUpdate() >0){
                                System.out.println("User ID: " + user.getId() + " inserted!");
                            }
                            callableStatement.close();
                        }else {
                            CallableStatement callableStatement = conn.prepareCall("{call updateUser(?, ?, ?, ? , ?, ?, ?, ?)}");
                            callableStatement.setInt(1, user.getId());
                            callableStatement.setString(2, user.getName());
                            callableStatement.setString(3, user.getUserName());
                            callableStatement.setString(4, user.getEmail());
                            callableStatement.setString(5, String.valueOf(addObject));
                            callableStatement.setString(6, user.getPhone());
                            callableStatement.setString(7, user.getWebsite());
                            callableStatement.setString(8, String.valueOf(companyObject));
                            if(callableStatement.executeUpdate() >0){
                                System.out.println("User ID: " + user.getId() + " updated!");
                            }
                            callableStatement.close();
                        }
                    }catch (Exception e){}

                }
            }
            resultSet.close();
            preparedStatement.close();
            conn.close();
            connection.disconnect();
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

}
