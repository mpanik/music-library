package project.pa165.musiclibrary.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import project.pa165.musiclibrary.dto.SongDto;
import project.pa165.musiclibrary.util.Genre;
import project.pa165.musiclibrary.exception.SongNotFoundException;
import project.pa165.musiclibrary.services.SongService;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class SongControllerTest {

    private MockMvc mockMvc;
    @Mock
    private SongService songService;
    @InjectMocks
    private SongController songController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(songController).build();

        SongDto song1 = createSong(1l, "Walk", (short) 1, 200, Genre.ROCK, 320, "test");
        SongDto song2 = createSong(2l, "Arlandria Walking", (short) 2, 300, Genre.HOLIDAY, 128, "just song");

        when(songService.findSongByTitle("walk")).thenReturn(Arrays.asList(song1, song2));
        when(songService.findSong(1l)).thenReturn(song1);
    }

    @Test
    public void testGetSongsByTerm() throws Exception {
        String term = "walk";
        mockMvc.perform(get("/music/songs/search" + "?term=" + term))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Walk"))
                .andExpect(jsonPath("$[0].trackNumber").value(1))
                .andExpect(jsonPath("$[0].duration").value(200))
                .andExpect(jsonPath("$[0].genre").value(Genre.ROCK.toString()))
                .andExpect(jsonPath("$[0].bitrate").value(320))
                .andExpect(jsonPath("$[0].note").value("test"))
                .andExpect(jsonPath("$[0].artist").value(nullValue()))
                .andExpect(jsonPath("$[0].album").value(nullValue()))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Arlandria Walking"))
                .andExpect(jsonPath("$[1].trackNumber").value(2))
                .andExpect(jsonPath("$[1].duration").value(300))
                .andExpect(jsonPath("$[1].genre").value(Genre.HOLIDAY.toString()))
                .andExpect(jsonPath("$[1].bitrate").value(128))
                .andExpect(jsonPath("$[1].note").value("just song"))
                .andExpect(jsonPath("$[1].artist").value(nullValue()))
                .andExpect(jsonPath("$[1].album").value(nullValue()));
        verify(songService).findSongByTitle(term);
    }

    @Test
    public void testGetSongById() throws Exception {
        Long id = 1l;
        mockMvc.perform(get("/music/songs/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Walk"))
                .andExpect(jsonPath("$.trackNumber").value(1))
                .andExpect(jsonPath("$.duration").value(200))
                .andExpect(jsonPath("$.genre").value(Genre.ROCK.toString()))
                .andExpect(jsonPath("$.bitrate").value(320))
                .andExpect(jsonPath("$.note").value("test"))
                .andExpect(jsonPath("$.artist").value(nullValue()))
                .andExpect(jsonPath("$.album").value(nullValue()));
        verify(songService).findSong(id);
    }

    @Test
    public void testGetSongByIdNoMatch() throws Exception {
        when(songService.findSong(any(Long.class))).thenThrow(new SongNotFoundException());
        Long id = 1l;
        mockMvc.perform(get("/music/songs/" + id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Song could not be found"));
        verify(songService).findSong(id);
    }

    private SongDto createSong(Long id, String title, Short trackNumber, Integer length,
                               Genre genre, Integer bitrate, String note) {
        SongDto song = new SongDto();
        song.setId(id);
        song.setTitle(title);
        song.setTrackNumber(trackNumber);
        song.setDuration(length);
        song.setGenre(genre);
        song.setBitrate(bitrate);
        song.setNote(note);
        return song;
    }
}