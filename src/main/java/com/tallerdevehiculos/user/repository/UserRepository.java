package com.tallerdevehiculos.user.repository;

import com.tallerdevehiculos.user.entity.Owner;
import com.tallerdevehiculos.user.entity.Repair;
import com.tallerdevehiculos.user.entity.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.tallerdevehiculos.user.config.FirebaseConfig;
import com.tallerdevehiculos.user.entity.Vehicle;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class UserRepository {

    private static FirebaseConfig firebase= new FirebaseConfig();
    private static Firestore db=firebase.getInstance();

    public static UserRepository repository= null;

    public static UserRepository getInstance() {
        if (repository == null){
            repository = new UserRepository();
        }
        return repository;
    }
    public User addUser  (User user){
        DocumentReference docRef = db.collection("users").document();
        user.setuId(docRef.getId());
//asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(user);
// result.get() blocks on response
        try {
            System.out.println("Update time : " + result.get().getUpdateTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return user;
    }

    public  User  findUserByuId (String uId){

        DocumentReference docRef = db.collection("users").document(uId);
// asynchronously retrieve the document
        ApiFuture<DocumentSnapshot> future = docRef.get();
// ...
// future.get() blocks on response
        DocumentSnapshot document = null;
        try {
            document = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        User user = null;
        if (document.exists()) {
            // convert document to POJO
            user = document.toObject(User.class);
            return user;
        }
        return user;
    }


    public List<User> findAllUsers() {

        //asynchronously retrieve all documents
        ApiFuture<QuerySnapshot> future = db.collection("users").get();
// future.get() blocks on response
        List<QueryDocumentSnapshot> documents = null;
        try {
            documents = future.get().getDocuments();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        List<User> lista = new ArrayList();

        for (DocumentSnapshot document : documents)
        {
            lista.add(document.toObject(User.class));
        }
        return lista;
    }

    public List<User> DoesTheUserExist (String id){

        ApiFuture<QuerySnapshot> future =
                db.collection("users").whereEqualTo("id", id).get();
// future.get() blocks on response
        List<QueryDocumentSnapshot> documents = null;
        try {
            documents = future.get().getDocuments();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        List<User> lista = new ArrayList<User>();

        for (DocumentSnapshot document : documents)
        {
            lista.add(document.toObject(User.class));
        }
        return lista;
    }

    public List<User> findUsersByRole(String role){

        ApiFuture<QuerySnapshot> future =
                db.collection("users").whereEqualTo("role", role).get();
// future.get() blocks on response
        List<QueryDocumentSnapshot> documents = null;
        try {
            documents = future.get().getDocuments();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        List<User> lista = new ArrayList<User>();

        for (DocumentSnapshot document : documents)
        {
            lista.add(document.toObject(User.class));
        }
        return lista;
    }

    public  String deleteUser  (User user ){

        ApiFuture<WriteResult> writeResult = db.collection("users").document(user.getuId()).delete();
// ...
        try {

            Owner owner =new Owner();
            owner.setuId(user.getuId());
            owner.setCreationDate(user.getCreationDate());
            owner.setEmail(user.getEmail());
            owner.setId(Integer.parseInt(user.getId()));
            owner.setName(user.getName());
            owner.setTelephone(user.getTelephone());
            owner.setLastName(user.getLastName());

            List<Vehicle> lista = findAllVehicles(owner);
            System.out.println(lista);
            for (Vehicle vhc : lista) {
                List<Repair> lista2 = findAllRepairs(vhc.getLicensePlate());
                for (Repair rpr : lista2) {
                    deleteRepair(rpr);
                }
                deleteVehicle(vhc.getLicensePlate());
            }
            System.out.println("Update time : " + writeResult.get().getUpdateTime());
        } catch (InterruptedException e) {
            e.printStackTrace();

            return "{" +
                    "\"status\"" +": \"0\""+
                    "}";

        } catch (ExecutionException e) {
            e.printStackTrace();

            return "{" +
                    "\"status\"" +": \"0\""+
                    "}";
        }

        return "{" +
                "\"status\""+":\"1\""+
                "}";
    }

    public  String editUser (User  user ){

        DocumentReference docRef = db.collection("users").document(user.getuId());

        Map<String, Object> updates = new HashMap<>();
        updates.put("email", user.getEmail());
        updates.put("id", user.getId());
        updates.put("lastName", user.getLastName());
        updates.put("name", user.getName());
        updates.put("telephone", user.getTelephone());
        updates.put("role", user.getRole());


// (async) Update one field
        ApiFuture<WriteResult> future = docRef.update(updates);

// ...
        WriteResult result = null;
        try {
            result = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();

            return "{" +
                    "\"status\"" +": \"0\""+
                    "}";

        } catch (ExecutionException e) {
            e.printStackTrace();

            return "{" +
                    "\"status\"" +": \"0\""+
                    "}";
        }

        return "{" +
                "\"status\""+":\"1\""+
                "}";
    }

    public  String deleteVehicle  (String LicensePlate){

        ApiFuture<WriteResult> writeResult = db.collection("vehicle").document(LicensePlate).delete();

        try {
            System.out.println("Update time : " + writeResult.get().getUpdateTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "{" +
                    "\"status\"" +":\"0\""+
                    "}";
        } catch (ExecutionException e) {
            e.printStackTrace();
            return "{" +
                    "\"status\"" +":\"0\""+
                    "}";
        }

        return "{" +
                "\"status\"" +":\"1\""+
                "}";
    }

    public List<Vehicle> findAllVehicles( Owner owner) {
        ApiFuture<QuerySnapshot> future =
                db.collection("vehicle").whereEqualTo("owner", owner).get();

        List<QueryDocumentSnapshot> documents = null;
        try {
            documents = future.get().getDocuments();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        List<Vehicle> lista = new ArrayList();

        for (DocumentSnapshot document : documents)
        {
            lista.add(document.toObject(Vehicle.class));
        }
        return lista;
    }

    public List<Repair> findAllRepairs(String LicensePlate) {
        ApiFuture<QuerySnapshot> future =
                db.collection("repairs").whereEqualTo("licensePlateVehicle", LicensePlate).get();
// future.get() blocks on response
        List<QueryDocumentSnapshot> documents = null;
        try {
            documents = future.get().getDocuments();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        List<Repair> lista = new ArrayList<>();

        for (DocumentSnapshot document : documents)
        {
            lista.add(document.toObject(Repair.class));
        }
        return lista;
    }
    public  String deleteRepair  (Repair  repair ){

        ApiFuture<WriteResult> writeResult = db.collection("repairs").document(repair.getrId()).delete();
// ...
        try {
            System.out.println("Update time : " + writeResult.get().getUpdateTime());
        } catch (InterruptedException e) {
            e.printStackTrace();

            return "{" +
                    "\"status\"" +": \"0\""+
                    "}";

        } catch (ExecutionException e) {
            e.printStackTrace();

            return "{" +
                    "\"status\"" +": \"0\""+
                    "}";
        }

        return "{" +
                "\"status\""+":\"1\""+
                "}";
    }

}
