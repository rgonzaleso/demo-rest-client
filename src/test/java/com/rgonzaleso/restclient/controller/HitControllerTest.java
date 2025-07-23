package com.rgonzaleso.restclient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rgonzaleso.restclient.config.SecurityConfig;
import com.rgonzaleso.restclient.dto.HideHitRequestDTO;
import com.rgonzaleso.restclient.dto.HitUserRequestDTO;
import com.rgonzaleso.restclient.security.JwtFilter;
import com.rgonzaleso.restclient.security.JwtService;
import com.rgonzaleso.restclient.service.HitsService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HitController.class)
@Import({SecurityConfig.class, JwtFilter.class})
class HitControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private Long jwtExpiration;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    HitsService hitsService;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Captor
    ArgumentCaptor<HitUserRequestDTO> userRequestDTOCaptor;

    @Captor
    ArgumentCaptor<Pageable> pageableCaptor;

    @Captor
    ArgumentCaptor<Boolean> booleanCaptor;

    @Captor
    ArgumentCaptor<List<Long>> listLongCaptor;

    @Test
    void findWithoutAuthorization() throws Exception {
        mockMvc.perform(get(HitController.HIT_PATH_FIND)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void findByDefault() throws Exception {
        String token = securityImplementation();
        mockMvc.perform(get(HitController.HIT_PATH_FIND)
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(hitsService, times(1))
                .findByCriteria(userRequestDTOCaptor.capture(), pageableCaptor.capture());

        assertThat(pageableCaptor.getValue().getPageNumber()).isZero();
    }

    @Test
    void seedWithoutAuthorization() throws Exception {
        mockMvc.perform(get(HitController.HIT_PATH_SEED)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void seedWithAuthorization() throws Exception {
        String token = securityImplementation();
        mockMvc.perform(get(HitController.HIT_PATH_SEED)
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(hitsService, times(1)).retrieveData(booleanCaptor.capture());

        assertThat(booleanCaptor.getValue()).isTrue();
    }

    @Test
    void hideWithoutAuthorization() throws Exception {
        mockMvc.perform(post(HitController.HIT_PATH_HIDE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void hideWithAuthorization() throws Exception {
        String token = securityImplementation();

        HideHitRequestDTO dto = HideHitRequestDTO.builder().ids(List.of(1L,2L)).build();

        mockMvc.perform(post(HitController.HIT_PATH_HIDE)
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(hitsService, times(1)).hideHits(listLongCaptor.capture());

        assertThat(listLongCaptor.getValue()).hasSize(2);
        assertThat(listLongCaptor.getValue().get(0)).isEqualTo(1L);
        assertThat(listLongCaptor.getValue().get(1)).isEqualTo(2L);
    }

    private String securityImplementation(){
        String username = "mockUsername";
        UserDetails userDetails = User.withUsername(username)
                .password("")
                .roles("").build();

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpiration)))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        when(jwtService.extractUsername(token)).thenReturn(username);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        return token;
    }
}