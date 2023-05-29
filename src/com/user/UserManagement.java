package com.user;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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


                System.out.println(user.toString());
            }
            connection.disconnect();
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }
}
