package com.javarush.jira.profile.internal.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.jira.profile.ProfileTo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import com.javarush.jira.AbstractControllerTest;
import com.javarush.jira.login.AuthUser;
import com.javarush.jira.login.User;
import com.javarush.jira.login.Role;
import com.javarush.jira.profile.internal.ProfileMapper;
import com.javarush.jira.profile.internal.ProfileRepository;
import com.javarush.jira.profile.internal.model.Profile;
import static com.javarush.jira.profile.internal.web.ProfileTestData.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
class ProfileRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = ProfileRestController.REST_URL;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileRepository profileRepository;

    @MockBean
    private ProfileMapper profileMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private static final long USER_ID = 1L;
    private final AuthUser authUser = new AuthUser(new User(USER_ID, "new@gmail.com", "newPassword", "newFirstName", "newLastName", "newDisplayName", Role.DEV));

    @BeforeEach
    void setupAuth() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(authUser, null)
        );
    }

    @Test
    void getProfile() throws Exception {
        Profile profile = getNew(USER_ID);
        when(profileRepository.getOrCreate(USER_ID)).thenReturn(profile);
        when(profileMapper.toTo(profile)).thenReturn(USER_PROFILE_TO);

        mockMvc.perform(get(REST_URL)
                        .with(user(authUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(PROFIlE_TO_MATCHER.contentJson(USER_PROFILE_TO));
    }

    @Test
    void getUnauthorized() throws Exception {
        mockMvc.perform(get(REST_URL))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateProfile() throws Exception {
        ProfileTo to = getUpdatedTo();
        Profile existing = getNew(USER_ID);
        Profile updated = getUpdated(USER_ID);

        when(profileRepository.getOrCreate(USER_ID)).thenReturn(existing);
        when(profileMapper.updateFromTo(existing, to)).thenReturn(updated);

        mockMvc.perform(put(REST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(authUser))
                        .content(objectMapper.writeValueAsString(to)))
                .andExpect(status().isNoContent());

        verify(profileRepository).save(updated);
    }

    @Test
    void updateProfileInvalidInput() throws Exception {
        ProfileTo invalid = getInvalidTo();

        mockMvc.perform(put(REST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(authUser))
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateProfileWithUnknownNotification() throws Exception {
        ProfileTo to = getWithUnknownNotificationTo();

        mockMvc.perform(put(REST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(authUser))
                        .content(objectMapper.writeValueAsString(to)))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateProfileWithHtmlUnsafeContact() throws Exception {
        ProfileTo to = getWithContactHtmlUnsafeTo();

        mockMvc.perform(put(REST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(authUser))
                        .content(objectMapper.writeValueAsString(to)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateProfileUnauthorized() throws Exception {
        ProfileTo to = getUpdatedTo();

        mockMvc.perform(put(REST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(to)))
                .andExpect(status().isForbidden());
    }
}