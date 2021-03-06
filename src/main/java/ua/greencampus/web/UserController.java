package ua.greencampus.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import ua.greencampus.common.ProviderSignInUtils;
import ua.greencampus.dto.UserDto;
import ua.greencampus.service.AuthenticationService;
import ua.greencampus.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Nikolay Yashchenko
 */
@Controller
public class UserController {

    private AuthenticationService authenticationService;
    private UserService userService;

    @Autowired
    public UserController(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @GetMapping(value = "/user/{id}")
    public String getById(@PathVariable(value = "id") Long id, Model model) {
        model.addAttribute("userid", id);
        return "user";
    }

    @GetMapping(value = "/user/account")
    public String getUser(Model model) {
        Long id = authenticationService.getLoggedInUserId();
        model.addAttribute("userid", id);
        return "userRead";
    }

    @GetMapping(value = "/user/create")
    public String createUser(Model model) {
        model.addAttribute("message", "create");
        return "userCreate";
    }


    @GetMapping(value = "/user/update/{id}")
    public String updateUser(@PathVariable("id") Long id, Model model) {
        model.addAttribute("userid", id);
        model.addAttribute("message", "update");
        return "userUpdateAdmin";
    }

    @GetMapping(value = "/user/update")
    public String updateForUser(Model model) {
        Long id = authenticationService.getLoggedInUserId();
        model.addAttribute("userid", id);
        model.addAttribute("message", "update");
        return "userUpdate";
    }

    @GetMapping(value = "/user/update/pass")
    public String updatePassword(Model model) {
        Long id = authenticationService.getLoggedInUserId();
        model.addAttribute("userid", id);
        model.addAttribute("message", "update");
        return "userPassword";
    }

    @GetMapping(value = "/users")
    public String getAll() {
        return "users";
    }

    @GetMapping(value = "/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, Model model) {
        model.addAttribute("userid", id);
        return "userDelete";
    }

    @GetMapping(value = "/login")
    public String loginPage(@RequestParam(name = "redirect", required = false) String redirect,
                            HttpServletRequest request, Model model) {
        request.getSession().setAttribute("url_redirect_login", redirect);
        model.addAttribute("userDto", new UserDto());
        return "login";
    }

    @Autowired
    private ProviderSignInUtils providerSignInUtils;

    @GetMapping(value = "/signup")
    public String signup(Model model, ServletWebRequest request) {
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(request);
        if (connection == null) return "redirect:/login";

        String email;
        if ((email = connection.fetchUserProfile().getEmail()) == null) {
            model.addAttribute("userDto", new UserDto());
            return "email";
        }
        // todo better
//        else if (userService.readByEmail(email) != null) {
//            model.addAttribute("userDto", new UserDto());
//            return "email";
//        }
        providerSignInUtils.doPostSignUp(email, request);
        return "/helper/closeWindow";
    }

    @PostMapping(value = "/signup/email")
    public String continueSignup(@ModelAttribute UserDto userDto, ServletWebRequest request) {
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(request);
        if (connection == null || userService.readByEmail(userDto.getEmail()) != null) {
            return "/helper/closeWindow";
        }
        providerSignInUtils.doPostSignUp(userDto.getEmail(), request);
        return "/helper/closeWindow";
    }

    @PostMapping(value = "/login/http-basic")
    @ResponseBody
    @CrossOrigin
    public Map<String, Object> principal(Principal principal, HttpServletRequest request) {
        if (principal != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("userId", authenticationService.getLoggedInUserId());
            result.put("role", SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                    .iterator().next());
            return result;
        }
        return null;
    }
}
