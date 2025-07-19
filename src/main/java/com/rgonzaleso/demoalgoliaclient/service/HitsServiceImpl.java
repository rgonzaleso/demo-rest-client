package com.rgonzaleso.demoalgoliaclient.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rgonzaleso.demoalgoliaclient.dto.HitUserRequestDTO;
import com.rgonzaleso.demoalgoliaclient.entity.Hit;
import com.rgonzaleso.demoalgoliaclient.entity.Request;
import com.rgonzaleso.demoalgoliaclient.externalapi.AlgoliaApiClient;
import com.rgonzaleso.demoalgoliaclient.dto.HitDTO;
import com.rgonzaleso.demoalgoliaclient.repository.HitRepository;
import com.rgonzaleso.demoalgoliaclient.repository.RequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class HitsServiceImpl implements HitsService {

    private final AlgoliaApiClient algoliaApiClient;
    private final HitRepository hitRepository;
    private final RequestRepository requestRepository;

    // consideration: retrieve the 20 first pages
    @Override
    public void retrieveData(boolean firstLoad) {
        List<Request> requests = new ArrayList<>();
        List<Hit> hits = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (int i = 0; i < 20; i++) {
            String rawJson = algoliaApiClient.getHints("java", i, 100);
            JsonNode jsonNode = null;
            try {
                jsonNode = objectMapper.readTree(rawJson);
            } catch (IOException e) {
                throw new IllegalArgumentException("Could not deserialize JSON", e);
            }

            Request request = new Request();
            request.setData(jsonNode);
            requests.add(request);

            hits.addAll(this.extractHits(jsonNode));
        }

        // if not the first load ignore repeated hits
        if(!firstLoad) {
            Optional<Long> maxStoryId = Optional.ofNullable(hitRepository.findMaxStoryId());
            if(maxStoryId.isPresent()) {
                hits = hits.stream()
                        .filter(hit -> hit.getStoryId().compareTo(maxStoryId.get())>0)
                        .toList();
            }
        }

        requestRepository.saveAll(requests);
        hitRepository.saveAll(hits);
    }

    @Override
    public Page<HitDTO> findByCriteria(HitUserRequestDTO filters, Pageable pageable) {
        ObjectMapper mapper = new ObjectMapper();

        String tagFilterJson = null;

        if (filters.getTags() != null && !filters.getTags().isEmpty()) {
            try {
                Map<String, Object> tagFilter = Map.of("_tags", filters.getTags());
                tagFilterJson = mapper.writeValueAsString(tagFilter);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Failed to build tag filter JSON", e);
            }
        }

        return hitRepository.findByAuthorTagsTitleMonth(filters.getAuthor(), tagFilterJson,
                filters.getTitle(), filters.getMonth(), pageable)
                .map(hit -> new HitDTO(hit.getId(), hit.getData()));
    }

    @Override
    public void hideHits(List<Long> ids) {
        hitRepository.hideByIds(ids);
    }

    private List<Hit> extractHits(JsonNode hitsNode) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM");

        return hitsNode.get("hits").valueStream()
                .map(hitNode -> {
                    Hit hit = new Hit();
                    hit.setHidden(false);
                    hit.setData(hitNode);
                    hit.setStoryId(hitNode.path("story_id").asLong(0));
                    hit.setAuthor(hitNode.path("author").asText());

                    if(hitNode.has("story_title")){
                        hit.setTitle(hitNode.path("story_title").asText());
                    }else{
                        hit.setTitle(hitNode.path("title").asText());
                    }

                    if (hitNode.has("created_at")) {
                        OffsetDateTime offsetDateTime = OffsetDateTime.parse(hitNode.path("created_at").asText());
                        LocalDateTime localDateTime = offsetDateTime.toLocalDateTime();
                        hit.setMonth(localDateTime.format(formatter).toLowerCase());
                    }

                    return hit;
                })
                .toList();
    }
}
