package me.universi.capacitacao.controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;
import me.universi.capacitacao.entidades.Video;
import me.universi.capacitacao.service.VideoService;


@RestController
@RequestMapping("/api/capacitacao")
public class VideoController {

    @Autowired
    private VideoService videoService;

    //Get para a página WEB onde está o 'CRUD' de gerenciamento de vídeos
    @GetMapping("/gerenciador-capacitacao")
    public ModelAndView videoList() {
        ModelAndView modelAndView = new ModelAndView();
        List<Video> videos = videoService.getAllVideo();
        modelAndView.addObject("videos", videos);
        modelAndView.setViewName("capacitacaoCrud");
        return modelAndView;
    }

    @GetMapping("/{id}") //Método para listar o vídeo pelo ID
    public ResponseEntity<Video> getVideoById(@PathVariable Long id) {
        Video video = videoService.getVideoById(id);
        return ResponseEntity.ok(video);
    }

    @PostMapping("/add") //Método para adicionar vídeo
    public ResponseEntity<Video> createVideo(@Valid @RequestBody Video video) {
        boolean result = videoService.saveOrUpdateVideo(video);

        if (result) {
            return ResponseEntity.status(HttpStatus.CREATED).body(video);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/edit/{id}") //Método para editar o vídeo pelo ID
    public ResponseEntity<Video> updateVideo(@PathVariable Long id, @Valid @RequestBody Video video) {
        video.setId(id);
        boolean result = videoService.saveOrUpdateVideo(video);

        if (result) {
            return ResponseEntity.ok(video);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete/{id}") //Método para deletar o vídeo pelo ID
    public ResponseEntity<?> deleteVideo(@PathVariable Long id) {
        boolean result = videoService.deleteVideo(id);

        if (result) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/play/{id}") //Método para listar e transmitir o vídeo do YouTube na tela '.HTML' pelo ID do vídeo
    public ModelAndView playVideo(@PathVariable Long id) {
    Video video = videoService.getVideoById(id);
    if (video == null) {
        return new ModelAndView("redirect:/");
    }
    ModelAndView mav = new ModelAndView("videoPlayer");
    mav.addObject("url", video.getUrl());
    return mav;
    }

    @GetMapping("/categoria/{category}")
    public ModelAndView listarVideosPorCategoria(@PathVariable String category, Model model) {
        List<Video> videos = videoService.getVideosByCategory(category);
        List<Video> videosDestaque = videoService.getVideosDestaqueByCategory(category);
        if (videos == null || videos.isEmpty()) {
            return new ModelAndView("redirect:/videos/home");  // Redireciona para a página inicial
        }
        ModelAndView mav = new ModelAndView("videoList");
        mav.addObject("videos", videos);
        mav.addObject("videosDestaque", videosDestaque);
        return mav;
    }

    @GetMapping("/home-capacitacao")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("videoHome");
        return modelAndView;
    }

    @GetMapping("/playlist/{playlist}")
        public ModelAndView listarVideosPlaylist(@PathVariable String playlist, Model model) {
        List<Video> videos = videoService.getVideosByPlaylist(playlist);
        if (videos == null || videos.isEmpty()) {
            return new ModelAndView("redirect:/");  // Redireciona para a página inicial
        }
        ModelAndView mav = new ModelAndView("VideoPlaylist");
        mav.addObject("videos", videos);
        return mav;
    }
}