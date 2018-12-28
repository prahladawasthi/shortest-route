package com.shortestroute.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.InternalResourceViewResolver;


public class ValidationControllerTest {
    @Mock
    View mockView;
    @Mock
    private ErrorAttributes errorAttributes;
    @InjectMocks
    private ValidationController controller;
    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = standaloneSetup(
                new ValidationController(errorAttributes)
        ).setViewResolvers(getInternalResourceViewResolver())
                .build();

    }

    private InternalResourceViewResolver getInternalResourceViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

    @Test
    public void verifyThatValidationPageViewIsCorrect() throws Exception {
        String message = "The application has encountered an error. Please restart again.";
        mockMvc.perform(get(controller.getErrorPath()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validationMessage", message))
                .andExpect(view().name("validation"));
    }
}