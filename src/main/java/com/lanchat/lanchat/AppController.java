package com.lanchat.lanchat;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;



@Controller
public class AppController {

    private final RoomService rooms;
    private final ConnectionMonitor monitor;

    public AppController(RoomService rooms, ConnectionMonitor monitor) {
        this.rooms = rooms;
        this.monitor = monitor;
    }

    @GetMapping("/")
    public String homePage() {
        return "redirect:/join";
    }

    @GetMapping("/join")
    public String joinPage() {
        return "join";
    }

    @PostMapping("/join")
    public String join(@RequestParam String key, Model model) {
        var room = rooms.find(key);
        if (room.isEmpty()) {
            model.addAttribute("message", "Invalid meeting code");
            return "join";
        }
        return "redirect:/room/" + room.get().code();
    }

    @GetMapping("/room/{code}")
    public String room(@PathVariable String code, Model model, HttpServletRequest request) {
        var room = rooms.find(code);
        if (room.isEmpty()) {
            return "redirect:/join";
        }
        monitor.joined(request.getSession().getId(), request.getRemoteAddr(), room.get().code());
        model.addAttribute("code", room.get().code());
        return "room";
    }
}
