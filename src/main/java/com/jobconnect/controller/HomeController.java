package com.jobconnect.controller;

import com.jobconnect.model.Role;
import com.jobconnect.model.User;
import com.jobconnect.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/","/login"})
    public String home(Model model,
                       @RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       @RequestParam(value = "registered", required = false) String registered) {

        // Just return the page; messages are handled by param.* and success/errorMessage if set
        return "home";
    }

    @PostMapping("/register")
    public String register(@RequestParam("role") String role,
                           @RequestParam("fullName") String fullName,
                           @RequestParam("phoneNumber") String phoneNumber,
                           @RequestParam("email") String email,
                           @RequestParam("password") String password,
                           Model model) {
        try {
            User user = new User();
            user.setName(fullName);
            user.setPhoneNumber(phoneNumber);// NEW
            user.setEmail(email);
            user.setPassword(password);

            if ("EMPLOYER".equalsIgnoreCase(role)) {
                user.setRole(Role.EMPLOYER);
            } else {
                user.setRole(Role.JOB_SEEKER);
            }

            userService.registerUser(user);

            // your HTML shows a param-based success message
            return "redirect:/?registered=true";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "home";
        }
    }

    @GetMapping("/post-login")
    public String postLogin() {
        Role role = userService.getCurrentUserRole();
        if (role == Role.EMPLOYER) {
            return "redirect:/employer/dashboard";
        } else if (role == Role.JOB_SEEKER) {
            return "redirect:/seeker/dashboard";
        }
        return "redirect:/";
    }
}
