package com.campus.complaint.controller;

import com.campus.complaint.dao.StudentDao;
import com.campus.complaint.model.Student;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private static final String[] DEPARTMENTS = {
            "Computer Science", "Information Science", "Electronics", "Mechanical", "Civil", "Electrical"
    };

    private final StudentDao studentDao;
    private final PasswordEncoder encoder;

    public AuthController(StudentDao studentDao, PasswordEncoder encoder) {
        this.studentDao = studentDao;
        this.encoder = encoder;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    // ---------- Registration ----------
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("departments", DEPARTMENTS);
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid Student student, BindingResult result, Model model,
                           RedirectAttributes redirect) {
        if (student.getPassword() == null || student.getPassword().isBlank()) {
            result.rejectValue("password", "required", "Password is required");
        }
        if (!result.hasFieldErrors("usn") && studentDao.existsByUsn(student.getUsn())) {
            result.rejectValue("usn", "duplicate", "This USN is already registered");
        }
        if (!result.hasFieldErrors("email") && studentDao.existsByEmail(student.getEmail())) {
            result.rejectValue("email", "duplicate", "This email is already registered");
        }
        if (result.hasErrors()) {
            model.addAttribute("departments", DEPARTMENTS);
            return "register";
        }
        student.setPasswordHash(encoder.encode(student.getPassword()));
        studentDao.save(student);
        redirect.addFlashAttribute("success", "Registration successful. Please log in.");
        return "redirect:/login";
    }

    // ---------- Login / Logout ----------
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String usn, @RequestParam String password,
                        HttpSession session, Model model) {
        var student = studentDao.findByUsn(usn.trim());
        if (student.isEmpty() || !encoder.matches(password, student.get().getPasswordHash())) {
            model.addAttribute("error", "Invalid USN or password");
            return "login";
        }
        session.setAttribute("studentId", student.get().getStudentId());
        session.setAttribute("studentName", student.get().getFullName());
        return "redirect:/student/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
