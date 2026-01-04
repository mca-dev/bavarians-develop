package com.bavarians.controller;

import com.bavarians.graphql.model.Klient;
import com.bavarians.service.SecurityService;
import com.bavarians.service.UserService;
import com.bavarians.service.impl.DefaultUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController {
    @Autowired
    private SecurityService securityService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserValidator userValidator;
    @Autowired
    private DefaultUserDetailsService defaultUserDetailsService;


    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new Klient());
        return PagesConstants.REGISTRATION_PAGE;
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("userForm") Klient userForm, BindingResult bindingResult) {
        userValidator.validate(userForm, bindingResult);
        if (bindingResult.hasErrors()) {
            return PagesConstants.REGISTRATION_PAGE;
        }
        userService.save(userForm);
        return "redirect:/" + PagesConstants.LOGIN_PAGE;
    }

    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (securityService.isAuthenticated()) {
            return PagesConstants.WELCOME_PAGE;
        }
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");
        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");
        return PagesConstants.LOGIN_PAGE;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(HttpServletRequest request, @ModelAttribute Klient userForm) throws ServletException {
        securityService.autoLogin(userForm.getEmail(), userForm.getHaslo());
        if (securityService.isAuthenticated()) {
            return "redirect:/" + PagesConstants.WELCOME_PAGE;
        }
        return PagesConstants.LOGIN_PAGE;
    }
}