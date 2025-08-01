package com.berry.project;

import com.berry.project.entity.lodge.LodgeDescription;
import com.berry.project.repository.lodge.LodgeDescriptionRepository;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class LodgeDescriptionTest {

  @Autowired
  private LodgeDescriptionRepository lodgeDescriptionRepository;

  private static final Logger log = LoggerFactory.getLogger(LodgeDescriptionTest.class);

  @Test
  void lodgeDescriptionTest() throws Exception {
    JSONParser parser = new JSONParser();

    List<LodgeDescription> lodgeDescriptions = lodgeDescriptionRepository.findByLodgeId(315);
    for (LodgeDescription lodgeDescription : lodgeDescriptions) {
      log.info("> 제목 : {}", lodgeDescription.getTitle());
      JSONArray parsed = (JSONArray) parser.parse(lodgeDescription.getContent());
      for (Object item : parsed) {
        log.info("> {} : {}", item.getClass() , item);
      }
    }
  }
}
