package project.pa165.musiclibrary.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import project.pa165.musiclibrary.dto.AlbumDto;
import project.pa165.musiclibrary.exception.AlbumBadRequestException;
import project.pa165.musiclibrary.exception.AlbumNotFoundException;
import project.pa165.musiclibrary.services.AlbumService;
import project.pa165.musiclibrary.util.ErrorInfo;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

/**
 * @author Milan
 */
@RestController
@RequestMapping(value = "/albums")
public class AlbumController {

    private AlbumService albumService;

    public AlbumService getAlbumService() {
        return albumService;
    }

    @Inject
    public void setAlbumService(AlbumService albumService) {
        this.albumService = albumService;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public List<AlbumDto> getAllAlbums() {
        return albumService.getAllAlbums();
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public List<AlbumDto> getAlbumsByTerm(@RequestParam("term") String term) {
        return getAlbumService().findAlbumByTitle(term);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public AlbumDto getAlbumById(@PathVariable("id") Long id) throws AlbumNotFoundException {
        AlbumDto album = getAlbumService().findAlbum(id);
        if (album == null) throw new AlbumNotFoundException(id.toString());
        return album;
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteAlbum(@PathVariable("id") Long id) {
        if (getAlbumService().findAlbum(id) == null) throw new AlbumNotFoundException(id.toString());
        albumService.deleteAlbum(id);
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public void createAlbum(@Valid @RequestBody AlbumDto album, Errors errors) {
        if (errors.hasErrors())
            throw new AlbumBadRequestException("Failed to map JSON to AlbumDto", errors);
        albumService.createAlbum(album);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void updateAlbum(@Valid @RequestBody AlbumDto album, Errors errors) {
        if (errors.hasErrors())
            throw new AlbumBadRequestException("Failed to map JSON to AlbumDto", errors);
        albumService.updateAlbum(album);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AlbumNotFoundException.class)
    public
    @ResponseBody
    ErrorInfo handleAlbumNotFoundException(AlbumNotFoundException ex) {
        return new ErrorInfo(404, ex.getMessage());
    }
}