package com.berry.project;

import com.berry.project.data.LodgeData;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LodgeDataTest {

  @Autowired
  private LodgeData lodgeData;

  private final Logger log = LoggerFactory.getLogger(LodgeDataTest.class);

  @Test
  void facilityIconMapTest() {
    log.info("facilityIconMap 테스트");
    log.info("publicFacilities");
    for (String facility : lodgeData.getPublicFacilities())
      log.info("{} : {}", facility, lodgeData.getFacilityIconMap().get(facility));

    for (String facility : lodgeData.getInnerFacilities())
      log.info("{} : {}", facility, lodgeData.getFacilityIconMap().get(facility));

    for (String facility : lodgeData.getOtherFacilities())
      log.info("{} : {}", facility, lodgeData.getFacilityIconMap().get(facility));
  }

}
