package me.universi.capacity.controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import jakarta.validation.Valid;
import me.universi.capacity.entidades.Video;
import me.universi.capacity.exceptions.VideoException;
import me.universi.capacity.service.VideoService;


@RestController
@RequestMapping("/api/capacitacao")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping("/gerenciador-capacitacao")
    public List<Video> videoList() {
        return videoService.getAllVideo();
    }

    @GetMapping("/video/{id}")
    public ResponseEntity<Video> getVideoById(@PathVariable Long id) {
        Video video = videoService.getVideoById(id);
        if (video != null) {
            return ResponseEntity.ok(video);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> createVideo(@Valid @RequestBody Video video) throws VideoException {
        boolean result = videoService.saveOrUpdateVideo(video);

        if (result) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Sucesso!! Video adicionado.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Video> updateVideo(@PathVariable Long id, @Valid @RequestBody Video video) throws VideoException {
        video.setId(id);
        boolean result = videoService.saveOrUpdateVideo(video);

        if (result) {
            return ResponseEntity.ok(video);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteVideo(@PathVariable Long id) {
        boolean result = videoService.deleteVideo(id);

        if (result) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // @GetMapping("/play/{id}")
    // public ResponseEntity<String> playVideo(@PathVariable Long id) {
    //     Video video = videoService.getVideoById(id);
    //     if (video == null) {
    //         return ResponseEntity.notFound().build();
    //     }

    //     return ResponseEntity.ok(video.getUrl());
    // }

    @GetMapping("/categoria/{category}")
    public List<Video> listarVideosPorCategoria(@PathVariable String category) {
        return videoService.getVideosByCategory(category);
    }

    // @GetMapping("/home-capacitacao")
    // public String home() {
    //     return "videoHome";
    // }

    @GetMapping("/playlist/{playlist}")
    public List<Video> listarVideosPlaylist(@PathVariable String playlist) {
        return videoService.getVideosByPlaylist(playlist);
    }
}
