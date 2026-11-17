package com.campus.complaint.controller;

import com.campus.complaint.Constants;
import com.campus.complaint.dao.ComplaintDao;
import com.campus.complaint.dao.StudentDao;
import com.campus.complaint.model.Complaint;
import com.campus.complaint.model.ComplaintForm;
import com.campus.complaint.model.ProfileForm;
import com.campus.complaint.model.Student;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final ComplaintDao complaintDao;
    private final StudentDao studentDao;

    public StudentController(ComplaintDao complaintDao, StudentDao studentDao) {
        this.complaintDao = complaintDao;
        this.studentDao = studentDao;
    }

    private Long sid(HttpSession session) {
        return (Long) session.getAttribute("studentId");
    }

    // ---------- Dashboard: list own complaints ----------
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long studentId = sid(session);
        List<Complaint> complaints = complaintDao.findByStudent(studentId);
        model.addAttribute("complaints", complaints);
        model.addAttribute("counts", complaintDao.countsByStatus(studentId));
        model.addAttribute("total", complaints.size());
        return "dashboard";
    }

    // ---------- Create ----------
    @GetMapping("/complaints/new")
    public String newForm(Model model) {
        model.addAttribute("complaint", new ComplaintForm());
        model.addAttribute("categories", Constants.CATEGORIES);
        model.addAttribute("heading", "Register a Complaint");
        model.addAttribute("formAction", "/student/complaints/new");
        return "complaint_form";
    }

    @PostMapping("/complaints/new")
    public String create(@Valid @ModelAttribute("complaint") ComplaintForm form, BindingResult result,
                         HttpSession session, Model model, RedirectAttributes redirect) {
        if (!result.hasFieldErrors("category") && !Constants.CATEGORIES.contains(form.getCategory())) {
            result.rejectValue("category", "invalid", "Invalid category");
        }
        if (result.hasErrors()) {
            model.addAttribute("categories", Constants.CATEGORIES);
            model.addAttribute("heading", "Register a Complaint");
            model.addAttribute("formAction", "/student/complaints/new");
            return "complaint_form";
        }
        complaintDao.save(sid(session), form.getCategory(), form.getDescription());
        redirect.addFlashAttribute("success", "Complaint submitted successfully.");
        return "redirect:/student/dashboard";
    }

    // ---------- Update (only while Pending) ----------
    @GetMapping("/complaints/{cid}/edit")
    public String editForm(@PathVariable Long cid, HttpSession session, Model model,
                           RedirectAttributes redirect) {
        Optional<Complaint> c = complaintDao.findByIdAndStudent(cid, sid(session));
        if (c.isEmpty()) {
            redirect.addFlashAttribute("error", "Complaint not found.");
            return "redirect:/student/dashboard";
        }
        if (!"Pending".equals(c.get().getStatus())) {
            redirect.addFlashAttribute("error", "This complaint is already being processed and can no longer be edited.");
            return "redirect:/student/dashboard";
        }
        ComplaintForm form = new ComplaintForm();
        form.setCategory(c.get().getCategory());
        form.setDescription(c.get().getDescription());
        model.addAttribute("complaint", form);
        model.addAttribute("categories", Constants.CATEGORIES);
        model.addAttribute("heading", "Edit Complaint #" + cid);
        model.addAttribute("formAction", "/student/complaints/" + cid + "/edit");
        return "complaint_form";
    }

    @PostMapping("/complaints/{cid}/edit")
    public String update(@PathVariable Long cid, @Valid @ModelAttribute("complaint") ComplaintForm form,
                         BindingResult result, HttpSession session, Model model,
                         RedirectAttributes redirect) {
        if (!result.hasFieldErrors("category") && !Constants.CATEGORIES.contains(form.getCategory())) {
            result.rejectValue("category", "invalid", "Invalid category");
        }
        if (result.hasErrors()) {
            model.addAttribute("categories", Constants.CATEGORIES);
            model.addAttribute("heading", "Edit Complaint #" + cid);
            model.addAttribute("formAction", "/student/complaints/" + cid + "/edit");
            return "complaint_form";
        }
        int rows = complaintDao.updateByStudent(cid, sid(session), form.getCategory(), form.getDescription());
        if (rows == 0) {
            redirect.addFlashAttribute("error", "Could not update. The complaint may already be in process.");
        } else {
            redirect.addFlashAttribute("success", "Complaint #" + cid + " updated.");
        }
        return "redirect:/student/dashboard";
    }

    // ---------- Delete (only while Pending) ----------
    @PostMapping("/complaints/{cid}/delete")
    public String delete(@PathVariable Long cid, HttpSession session, RedirectAttributes redirect) {
        int rows = complaintDao.deleteByStudent(cid, sid(session));
        if (rows == 0) {
            redirect.addFlashAttribute("error", "Could not delete. The complaint may already be in process.");
        } else {
            redirect.addFlashAttribute("success", "Complaint #" + cid + " deleted.");
        }
        return "redirect:/student/dashboard";
    }

    // ---------- Profile (student information CRUD) ----------
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Student s = studentDao.findById(sid(session)).orElseThrow();
        ProfileForm form = new ProfileForm();
        form.setFullName(s.getFullName());
        form.setEmail(s.getEmail());
        form.setDepartment(s.getDepartment());
        form.setContactNo(s.getContactNo());
        model.addAttribute("profile", form);
        model.addAttribute("usn", s.getUsn());
        model.addAttribute("departments", Constants.DEPARTMENTS);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profile") ProfileForm form, BindingResult result,
                                HttpSession session, Model model, RedirectAttributes redirect) {
        Long id = sid(session);
        if (!result.hasFieldErrors("email") && studentDao.emailUsedByOther(form.getEmail(), id)) {
            result.rejectValue("email", "duplicate", "This email is already used by another student");
        }
        if (result.hasErrors()) {
            model.addAttribute("usn", studentDao.findById(id).orElseThrow().getUsn());
            model.addAttribute("departments", Constants.DEPARTMENTS);
            return "profile";
        }
        studentDao.updateProfile(id, form.getFullName(), form.getEmail(), form.getDepartment(), form.getContactNo());
        session.setAttribute("studentName", form.getFullName());
        redirect.addFlashAttribute("success", "Profile updated.");
        return "redirect:/student/dashboard";
    }
}
