package com.jobconnect.controller;

import com.jobconnect.model.Job;
import com.jobconnect.model.User;
import com.jobconnect.model.JobApplication;
import com.jobconnect.model.ApplicationStatus;
import com.jobconnect.service.ApplicationService;
import com.jobconnect.service.JobService;
import com.jobconnect.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/employer")
public class EmployerController {

    private final UserService userService;
    private final JobService jobService;
    private final ApplicationService applicationService;

    public EmployerController(UserService userService,
                              JobService jobService,
                              ApplicationService applicationService) {
        this.userService = userService;
        this.jobService = jobService;
        this.applicationService = applicationService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        String email = userService.getCurrentUserEmail();
        if (email == null) {
            return "redirect:/login";
        }

        Optional<User> optionalUser = userService.findByEmail(email);
        User employer = optionalUser.orElse(null);

        String displayName;
        if (employer != null && employer.getFullName() != null && !employer.getFullName().isBlank()) {
            displayName = employer.getFullName().trim();
        } else {
            displayName = "Employer";
        }
        String initials = displayName.substring(0, 1).toUpperCase();

        List<Job> jobs = (employer != null)
                ? jobService.getJobsForEmployer(employer)
                : List.of();

        long totalJobs = jobs.size();
        long activeJobs = jobs.stream().filter(Job::isActive).count();
        long totalApplicants = applicationService.countApplicationsForJobs(jobs);

        model.addAttribute("employerName", displayName);
        model.addAttribute("employerInitials", initials);
        model.addAttribute("jobs", jobs);
        model.addAttribute("totalJobsCount", totalJobs);
        model.addAttribute("activeJobsCount", activeJobs);
        model.addAttribute("totalApplicantsCount", totalApplicants);

        return "employer-dashboard";
    }

    @GetMapping("/jobs/{id}/applications")
    public String viewJobApplications(@PathVariable Long id, Model model) {
        Job job = jobService.getJobById(id);
        List<JobApplication> applications = applicationService.getApplicationsForJob(job);
        model.addAttribute("job", job);
        model.addAttribute("applications", applications);
        return "employer-applications";
    }

    @PostMapping("/applications/{id}/status")
    public String updateApplicationStatus(@PathVariable Long id,
                                          @RequestParam("status") ApplicationStatus status,
                                          RedirectAttributes redirectAttributes) {
        JobApplication app = applicationService.getById(id);
        applicationService.updateStatus(id, status);
        redirectAttributes.addFlashAttribute("jobSuccess", "Application status updated.");
        return "redirect:/employer/jobs/" + app.getJob().getId() + "/applications";
    }

    @PostMapping("/jobs")
    public String createJob(@RequestParam String title,
                            @RequestParam String location,
                            @RequestParam String jobType,
                            @RequestParam("salaryRange") String salaryRange,
                            @RequestParam String description,
                            @RequestParam String requirements,
                            @RequestParam("deadline") String deadline,
                            RedirectAttributes redirectAttributes) {

        try {
            jobService.createJobFromForm(
                    title,
                    location,
                    jobType,
                    salaryRange,
                    description,
                    requirements,
                    deadline
            );
            redirectAttributes.addFlashAttribute("jobSuccess", "Job posted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "jobError",
                    "Failed to post job: " + e.getMessage()
            );
        }

        return "redirect:/employer/dashboard";
    }

 // 🔹 NEW: show edit form
    @GetMapping("/jobs/{id}/edit")
    public String showEditJobForm(@PathVariable Long id, Model model) {
        Job job = jobService.getJobById(id);
        model.addAttribute("job", job);
        return "employer-edit-job";
    }

    // 🔹 NEW: handle edit submit
    @PostMapping("/jobs/{id}/edit")
    public String updateJob(@PathVariable Long id,
                            @RequestParam String title,
                            @RequestParam String location,
                            @RequestParam String jobType,
                            @RequestParam("salaryRange") String salaryRange,
                            @RequestParam String description,
                            @RequestParam String requirements,
                            @RequestParam("deadline") String deadline,
                            RedirectAttributes redirectAttributes) {

        try {
            jobService.updateJobFromForm(
                    id,
                    title,
                    location,
                    jobType,
                    salaryRange,
                    description,
                    requirements,
                    deadline
            );
            redirectAttributes.addFlashAttribute("jobSuccess", "Job updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("jobError", "Failed to update job: " + e.getMessage());
        }

        return "redirect:/employer/dashboard";
    }
    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            jobService.deleteJobById(id);
            redirectAttributes.addFlashAttribute("jobSuccess", "Job deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("jobError", "Failed to delete job: " + e.getMessage());
        }
        return "redirect:/employer/dashboard";
    }

    @PostMapping("/jobs/{id}/toggle")
    public String toggleJob(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            jobService.toggleJobActive(id);
            redirectAttributes.addFlashAttribute("jobSuccess", "Job status updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("jobError", "Failed to update job status: " + e.getMessage());
        }
        return "redirect:/employer/dashboard";
    }
}
