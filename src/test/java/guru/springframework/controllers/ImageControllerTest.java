package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.services.ImageService;
import guru.springframework.services.RecipeService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Ignore
public class ImageControllerTest {

  @Mock
  private ImageService imageService;

  @Mock
  private RecipeService recipeService;

  private ImageController controller;

  private MockMvc mockMvc;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    controller = new ImageController(imageService, recipeService);
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .setControllerAdvice(new ControllerExceptionHandler())
        .build();
  }

  @Test
  public void getImageForm() throws Exception {
    //given
    final RecipeCommand command = new RecipeCommand();
    command.setId("1");

    when(recipeService.findCommandById(anyString())).thenReturn(Mono.just(command));

    //when
    mockMvc.perform(get("/recipe/1/image"))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("recipe"));

    verify(recipeService, times(1)).findCommandById(anyString());
  }

  @Test
  public void handleImagePost() throws Exception {
    final MockMultipartFile multipartFile = new MockMultipartFile("imagefile", "testing.txt",
        "text/plain", "Spring Framework Guru".getBytes());

    when(imageService.saveImageFile(anyString(), any())).thenReturn(Mono.empty());

    mockMvc.perform(multipart("/recipe/1/image").file(multipartFile))
        .andExpect(status().is3xxRedirection())
        .andExpect(header().string("Location", "/recipe/1/show"));

    verify(imageService, times(1)).saveImageFile(anyString(), any());
  }

  @Ignore
  @Test
  public void renderImageFromDB() throws Exception {

    ////given
    //final RecipeCommand command = new RecipeCommand();
    //command.setId("1");
    //
    //final String s = "fake image text";
    //final Byte[] bytesBoxed = new Byte[s.getBytes().length];
    //
    //int i = 0;
    //
    //for (final byte primByte : s.getBytes()) {
    //  bytesBoxed[i++] = primByte;
    //}
    //
    //command.setImage(bytesBoxed);
    //
    //when(recipeService.findCommandById(anyString())).thenReturn(Mono.just(command));
    //
    ////when
    //final MockHttpServletResponse response = mockMvc.perform(get("/recipe/1/recipeimage"))
    //    .andExpect(status().isOk())
    //    .andReturn()
    //    .getResponse();
    //
    //final byte[] reponseBytes = response.getContentAsByteArray();
    //
    //assertEquals(s.getBytes().length, reponseBytes.length);
  }
}
