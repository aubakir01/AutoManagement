package com.example.userlistapp.controller;

import org.springframework.web.bind.annotation.PathVariable;
import com.example.userlistapp.model.User;
import com.example.userlistapp.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;


@Controller
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    @GetMapping("/users")
    public String users(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            Model model) {

        if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("users", userRepository.findByUsernameContainingIgnoreCase(keyword));
        } else if (role != null && !role.isEmpty()) {
            model.addAttribute("users", userRepository.findByRole(role));
        } else {
            model.addAttribute("users", userRepository.findAll());
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("role", role);

        return "users";
    }


    // 1) Показать форму добавления
    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        return "user_form";
    }


    // 3) Открыть форму редактирования
    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id: " + id));
        model.addAttribute("user", user);
        return "user_form";
    }

    // 4) Сохранить изменения
    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute User user,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            user.setId(id);
            return "user_form";
        }
        user.setId(id);
        userRepository.save(user);
        return "redirect:/users";
    }


    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/users";
    }

    // 2) Принять форму и сохранить в БД
    @PostMapping("/users")
    public String createUser(@Valid @ModelAttribute User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user_form";
        }
        userRepository.save(user);
        return "redirect:/users";
    }

}
