package com.jobconnect.controller;

import com.jobconnect.model.Job;
import com.jobconnect.model.JobApplication;
import com.jobconnect.model.User;
import com.jobconnect.service.ApplicationService;
import com.jobconnect.service.JobService;
import com.jobconnect.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/seeker")
public class SeekerController {

    private final UserService userService;
    private final JobService jobService;
    private final ApplicationService applicationService;

    public SeekerController(UserService userService,
                            JobService jobService,
                            ApplicationService applicationService) {
        this.userService = userService;
        this.jobService = jobService;
        this.applicationService = applicationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        String email = userService.getCurrentUserEmail();
        User seeker = userService.findByEmail(email).orElse(null);

        List<Job> jobs = jobService.getAllActiveJobs();
        List<JobApplication> apps =
                (seeker != null) ? applicationService.getApplicationsForUser(seeker) : List.of();

        model.addAttribute("user", seeker);
        model.addAttribute("jobs", jobs);
        model.addAttribute("applications", apps);

        return "jobseeker-dashboard";
    }

    @PostMapping("/jobs/{id}/apply")
    public String apply(@PathVariable Long id,
                        @RequestParam("coverLetter") String coverLetter,
                        @RequestParam("resumeSummary") String resumeSummary) {

        String email = userService.getCurrentUserEmail();
        User seeker = userService.findByEmail(email).orElse(null);
        Job job = jobService.getJobById(id);

        if (seeker != null && job != null) {
            applicationService.applyToJob(job, seeker, coverLetter, resumeSummary);
        }

        return "redirect:/seeker/dashboard";
    }

    @PostMapping("/applications/{id}/withdraw")
    public String withdraw(@PathVariable Long id) {

        String email = userService.getCurrentUserEmail();
        User seeker = userService.findByEmail(email).orElse(null);

        applicationService.withdrawApplication(id, seeker);

        return "redirect:/seeker/dashboard";
    }
}
