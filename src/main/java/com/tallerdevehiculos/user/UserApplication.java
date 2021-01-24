package com.tallerdevehiculos.user;

import com.tallerdevehiculos.user.entity.Owner;
import com.tallerdevehiculos.user.entity.User;
import com.tallerdevehiculos.user.entity.Vehicle;
import com.tallerdevehiculos.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@SpringBootApplication
@RequestMapping(value = "/users")
public class UserApplication {

    @Autowired
    private UserRepository repository;

    @GetMapping("/{uId}")
    public User findUser(@PathVariable String uId){
        return repository.findUserByuId(uId);
    }

    @GetMapping("/vehicles")
    public List<Vehicle> findVehiclesByUser(@RequestBody Owner owner){
        return repository.findAllVehicles(owner);
    }

    @GetMapping(params="id")
    public List<User> findUserById(@RequestParam(value = "id", required = true) String id){
        return repository.DoesTheUserExist(id);
    }

    @GetMapping(params="role")
    public List<User> findUserByRole(@RequestParam(value = "role", required = true) String role){
        return repository.findUsersByRole(role);
    }

    @PostMapping
    public User AddUser(@RequestBody User user){
        return repository.addUser(user);
    }

    @DeleteMapping
    public String DeleteUser(@RequestBody User user){
        return repository.deleteUser(user);
    }

    @PutMapping
    public String UpdateUser(@RequestBody User user){
        return repository.editUser(user);
    }

    @GetMapping
    public List<User> GetAllUsers(){
        return repository.findAllUsers();
    }

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

}
