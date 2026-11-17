package com.campus.complaint.controller;

import com.campus.complaint.Constants;
import com.campus.complaint.dao.AdminDao;
import com.campus.complaint.dao.ComplaintDao;
import com.campus.complaint.dao.StudentDao;
import com.campus.complaint.model.Complaint;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminDao adminDao;
    private final ComplaintDao complaintDao;
    private final StudentDao studentDao;
    private final PasswordEncoder encoder;

    public AdminController(AdminDao adminDao, ComplaintDao complaintDao, StudentDao studentDao,
                           PasswordEncoder encoder) {
        this.adminDao = adminDao;
        this.complaintDao = complaintDao;
        this.studentDao = studentDao;
        this.encoder = encoder;
    }

    // ---------- Login / Logout ----------
    @GetMapping("/login")
    public String loginForm() {
        return "admin_login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password,
                        HttpSession session, Model model) {
        var admin = adminDao.findByUsername(username.trim());
        if (admin.isEmpty() || !encoder.matches(password, admin.get().getPasswordHash())) {
            model.addAttribute("error", "Invalid username or password");
            return "admin_login";
        }
        session.setAttribute("adminId", admin.get().getAdminId());
        session.setAttribute("adminName", admin.get().getUsername());
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

    // ---------- Dashboard: all complaints + search/filter ----------
    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String id,
                            @RequestParam(required = false) String category,
                            @RequestParam(required = false) String status,
                            Model model) {
        Long complaintId = null;
        String idError = null;
        if (id != null && !id.isBlank()) {
            try {
                complaintId = Long.parseLong(id.trim());
            } catch (NumberFormatException e) {
                idError = "Complaint ID must be a number";
            }
        }
        List<Complaint> complaints = idError != null
                ? List.of() : complaintDao.search(complaintId, category, status);

        model.addAttribute("complaints", complaints);
        model.addAttribute("counts", complaintDao.countsByStatus(null));
        model.addAttribute("categories", Constants.CATEGORIES);
        model.addAttribute("statuses", Constants.STATUSES);
        model.addAttribute("priorities", Constants.PRIORITIES);
        model.addAttribute("fId", id == null ? "" : id);
        model.addAttribute("fCategory", category == null ? "" : category);
        model.addAttribute("fStatus", status == null ? "" : status);
        model.addAttribute("idError", idError);
        return "admin_dashboard";
    }

    // ---------- Update status + priority ----------
    @PostMapping("/complaints/{cid}/update")
    public String updateComplaint(@PathVariable Long cid,
                                  @RequestParam String status, @RequestParam String priority,
                                  @RequestParam(required = false) String fId,
                                  @RequestParam(required = false) String fCategory,
                                  @RequestParam(required = false) String fStatus,
                                  RedirectAttributes redirect) {
        if (!Constants.STATUSES.contains(status) || !Constants.PRIORITIES.contains(priority)) {
            redirect.addFlashAttribute("error", "Invalid status or priority.");
        } else if (complaintDao.updateByAdmin(cid, status, priority) == 0) {
            redirect.addFlashAttribute("error", "Complaint #" + cid + " not found.");
        } else {
            redirect.addFlashAttribute("success", "Complaint #" + cid + " updated.");
        }
        // keep the current filters after saving
        if (fId != null && !fId.isBlank()) redirect.addAttribute("id", fId);
        if (fCategory != null && !fCategory.isBlank()) redirect.addAttribute("category", fCategory);
        if (fStatus != null && !fStatus.isBlank()) redirect.addAttribute("status", fStatus);
        return "redirect:/admin/dashboard";
    }

    // ---------- Student management ----------
    @GetMapping("/students")
    public String students(Model model) {
        model.addAttribute("students", studentDao.findAll());
        return "admin_students";
    }

    @PostMapping("/students/{sid}/delete")
    public String deleteStudent(@PathVariable Long sid, RedirectAttributes redirect) {
        studentDao.deleteById(sid);
        redirect.addFlashAttribute("success", "Student and their complaints were deleted.");
        return "redirect:/admin/students";
    }
}
