package com.rgonzaleso.demoalgoliaclient.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rgonzaleso.demoalgoliaclient.dto.HitDTO;
import com.rgonzaleso.demoalgoliaclient.dto.HitUserRequestDTO;
import com.rgonzaleso.demoalgoliaclient.entity.Hit;
import com.rgonzaleso.demoalgoliaclient.externalapi.AlgoliaApiClient;
import com.rgonzaleso.demoalgoliaclient.repository.HitRepository;
import com.rgonzaleso.demoalgoliaclient.repository.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.rgonzaleso.demoalgoliaclient.constants.AppConstants.MAX_SIZE_PER_PAGE;
import static com.rgonzaleso.demoalgoliaclient.constants.AppConstants.SORT_BY_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class HitsServiceImplTest {

    @Mock
    AlgoliaApiClient algoliaApiClient;

    @Mock
    HitRepository hitRepository;

    @Mock
    RequestRepository requestRepository;

    @Captor
    ArgumentCaptor<List<Hit>> hitListCaptor;

    @InjectMocks
    HitsServiceImpl hitsService;

    private final String clientResponse = """
            {
               "exhaustive": {
                 "nbHits": false,
                 "typo": false
               },
               "exhaustiveNbHits": false,
               "exhaustiveTypo": false,
               "hits": [
                 {
                    "_highlightResult": {
                      "author": {
                        "matchLevel": "none",
                        "matchedWords": [],
                        "value": "graeber_28927"
                      },
                      "comment_text": {
                        "fullyHighlighted": false,
                        "matchLevel": "full",
                        "matchedWords": [
                          "java"
                        ],
                        "value": "Whoa, the &quot;\\u003Cem\\u003Ejapa\\u003C/em\\u003Enese&quot; checkbox animation is awesome, so satisfying!"
                      },
                      "story_title": {
                        "matchLevel": "none",
                        "matchedWords": [],
                        "value": "Lorem Gibson"
                      },
                      "story_url": {
                        "matchLevel": "none",
                        "matchedWords": [],
                        "value": "http://loremgibson.com/"
                      }
                    },
                    "_tags": [
                      "comment",
                      "author_graeber_28927",
                      "story_44548620"
                    ],
                    "author": "graeber_28927",
                    "comment_text": "Whoa, the &quot;japanese&quot; checkbox animation is awesome, so satisfying!",
                    "created_at": "2025-07-16T13:54:55Z",
                    "created_at_i": 1752674095,
                    "objectID": "44582430",
                    "parent_id": 44576671,
                    "story_id": 44548620,
                    "story_title": "Lorem Gibson",
                    "story_url": "http://loremgibson.com/",
                    "updated_at": "2025-07-16T13:56:46Z"
                 }
               ],
               "hitsPerPage": 20,
               "nbHits": 556447,
               "nbPages": 50,
               "page": 1,
               "params": "query=java&page=1&advancedSyntax=true&analyticsTags=backend",
               "processingTimeMS": 12,
               "processingTimingsMS": {
                 "_request": {
                   "roundTrip": 18
                 },
                 "afterFetch": {
                   "format": {
                     "total": 1
                   },
                   "merge": {
                     "entries": {
                       "decompress": 2,
                       "total": 2
                     },
                     "total": 2
                   },
                   "total": 3
                 },
                 "fetch": {
                   "query": 7,
                   "scanning": 1,
                   "total": 9
                 },
                 "total": 13
               },
               "query": "java",
               "serverTimeMS": 14
             }
            """;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void retrieveFirstLoadData()  {

        given(algoliaApiClient.getHints(any(),any(),any())).willReturn(clientResponse);

        hitsService.retrieveData(true);

        verify(hitRepository, times(0)).findMaxStoryId();
        verify(hitRepository, times(1)).saveAll(hitListCaptor.capture());
        assertThat(hitListCaptor.getValue()).hasSize(20);
    }

    @Test
    void retrieveNotFirstLoadData() {
        given(algoliaApiClient.getHints(any(),any(),any()))
                .willReturn(clientResponse);
        hitsService.retrieveData(false);

        verify(hitRepository, times(1)).findMaxStoryId();
        verify(hitRepository, times(1)).saveAll(hitListCaptor.capture());
        assertThat(hitListCaptor.getValue()).hasSize(20);
    }

    @Test
    void findHitsByAuthor() {
        Long id = 1000L;
        String author = "graeber_28927";
        Page<Hit> hitPage = new PageImpl<>(Arrays.asList(
                Hit.builder()
                        .id(id)
                        .author(author)
                        .build())) ;

        given(hitRepository.findByAuthorTagsTitleMonth(eq(author), any(), any(), any(), any())).willReturn(hitPage);

        HitUserRequestDTO filters = HitUserRequestDTO.builder().author(author).build();

        Pageable pageable = PageRequest.of(0, MAX_SIZE_PER_PAGE, Sort.by(SORT_BY_ID));
        Page<HitDTO> result = hitsService.findByCriteria(filters, pageable);
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.stream().findFirst().get().id()).isEqualTo(id);
    }

    @Test
    void findHitsByTags() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Long id = 1000L;
        List<String> tags = List.of("tag1", "tag2", "tag3");
        String tagsString = mapper.writeValueAsString(Map.of("_tags", tags));

        Page<Hit> hitPage = new PageImpl<>(Arrays.asList(
                Hit.builder()
                        .id(id)
                        .author("Rolando")
                        .createdDate(LocalDateTime.now())
                        .build())) ;

        given(hitRepository.findByAuthorTagsTitleMonth(any(), eq(tagsString), any(), any(), any())).willReturn(hitPage);

        HitUserRequestDTO filters = HitUserRequestDTO.builder().tags(tags).build();

        Pageable pageable = PageRequest.of(0, MAX_SIZE_PER_PAGE, Sort.by(SORT_BY_ID));

        Page<HitDTO> result = hitsService.findByCriteria(filters, pageable);
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }
}