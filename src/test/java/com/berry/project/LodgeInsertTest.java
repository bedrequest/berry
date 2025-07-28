package com.berry.project;

import com.berry.project.entity.lodge.*;
import com.berry.project.util.FacilityMaskDecoder;
import com.berry.project.repository.lodge.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Rollback(false)
@SpringBootTest
public class LodgeInsertTest {

  @Autowired
  private LodgeRepository lodgeRepository;

  @Autowired
  private LodgeImgRepository lodgeImgRepository;

  @Autowired
  private RoomRepository roomRepository;

  @Autowired
  private RoomImgRepository roomImgRepository;

  @Autowired
  private WaysRepository waysRepository;

  @Autowired
  private FacilityMaskDecoder facilityMaskDecoder;

  @PersistenceContext
  private EntityManager entityManager;

  JSONParser parser = new JSONParser();

  @Test
  void lodgeInsertionTest() {
    List<String> success = new ArrayList<>(), fail = new ArrayList<>();
    List<String> lodgeNumbers = null;
    try {
      lodgeNumbers = loadList();
    } catch (IOException e) {
      System.out.println("목록 로드 실패");
      return;
    }

    Map<String, String> typeMap = new HashMap<>();
    typeMap.put("1", "모텔");
    typeMap.put("2", "호텔·리조트");
    typeMap.put("3", "펜션");
    typeMap.put("4", "홈&빌라");
    typeMap.put("5", "캠핑");
    typeMap.put("6", "게하·한옥");

    for (int i = 0; i < lodgeNumbers.size(); i++) {
      Lodge lodge = null;
      List<LodgeImg> lodgeImgs = new ArrayList<>();
      List<Room> rooms = new ArrayList<>();
      List<RoomImg> roomImgs = new ArrayList<>();
      List<Ways> ways = new ArrayList<>();

      try {
        String code = lodgeNumbers.get(i);
        System.out.println(code + " 분석");
        // 숙소 정보 받아오기
        Document body = getJsoupConnectionDoc("https://www.yeogi.com/domestic-accommodations/" + code);
        Element nextData = body.getElementById("__NEXT_DATA__");
        if (nextData == null) {
          System.out.println("__NEXT_DATA__를 찾을 수 없습니다.");
          continue;
        }
        String bodyHTML = nextData.html();

        // 전처리
        JSONObject bodyJSON = (JSONObject) (parser.parse(bodyHTML)),
            props = step(bodyJSON, "props"),
            pageProps = step(props, "pageProps"),
            accommodationInfo = step(pageProps, "accommodationInfo");
        JSONObject meta = step(accommodationInfo, "meta"),
            theme = step(accommodationInfo, "theme"),
            manager = step(accommodationInfo, "manager"),
            location = step(meta, "location"),
            traffic = step(accommodationInfo, "traffic");
        System.out.println("> 숙소 데이터 로드");

        // facilities 마스크 전처리
        List<String> facilities = new ArrayList<>();

        for (Object o : stepArr(theme, "items")) {
          JSONObject facilityJSON = (JSONObject) parser.parse(o.toString());
          facilities.add(facilityJSON.get("name").toString());
        }
        System.out.println("> facility 마스킹 완료");

        String intro = null;
        try {
          intro = manager.get("comment").toString();
        } catch (Exception ignore) {}

        // Lodge 등록 후 lodgeId 받아오기
        Lodge lodgeToInsert = Lodge.builder()
            .lodgeName(meta.get("name").toString())
            .lodgeType(typeMap.get(meta.get("category").toString()))
            .lodgeAddr(meta.get("address").toString())
            .facility(facilityMaskDecoder.encode(facilities))
            .intro(intro)
            .description(accommodationInfo.get("details").toString())
            .businessCall(meta.get("tel").toString())
            .latitude(Double.parseDouble(location.get("latitude").toString()))
            .longitude(Double.parseDouble(location.get("longitude").toString()))
            .build();
        lodge = lodgeRepository.save(lodgeToInsert);
        System.out.println("> 숙소 등록");

        // Ways 등록
        try {
          for (Object o : stepArr(traffic, "contents"))
            ways.add(waysRepository.save(
                Ways.builder().lodgeId(lodge.getLodgeId())
                    .content(o.toString())
                    .build()));
          System.out.println("> 오시는 길 등록");
        } catch (NullPointerException ignore) {
          System.out.println("> 오시는 길 없음");
        }

        // LodgeImage 등록
        for (Object o : stepArr(meta, "images")) {
          JSONObject json = (JSONObject) o;
          lodgeImgs.add(lodgeImgRepository.save(
              LodgeImg.builder()
                  .lodgeId(lodge.getLodgeId())
                  .lodgeImgUrl(json.get("image").toString())
                  .build())
          );
        }
        System.out.println("> 숙소 이미지 등록");

        // Room 등록
        for (Object o : stepArr(accommodationInfo, "rooms")) {
          // room 전처리
          JSONObject roomJSON = (JSONObject) o,
              rent = step(roomJSON, "rent"),
              stay = step(roomJSON, "stay");
          Integer rentPrice = null, rentStock = null;
          String rentTime = null;

          // 대실 데이터 처리
          if (rent != null) {
            JSONObject rentLabel = step(rent, "label");
            JSONObject rentPriceTable = step(rent, "price");
            if (rentPriceTable != null) {
              try {
                rentPrice = Integer.parseInt(rentPriceTable.get("strikePrice").toString());
              } catch (NullPointerException e) {
                try {
                  rentPrice = Integer.parseInt(rentPriceTable.get("salePrice").toString());
                } catch (NullPointerException ignore) {}
              }
              try {
                if (rentPrice != null) rentTime = rentLabel.get("checkInOut").toString();
              } catch (Exception ignore) {}
              try {
                rentStock = Integer.parseInt(rent.get("stockCount").toString());
              } catch (Exception ignore) {
                rentStock = 0;
              }
            } else rentStock = 0;
          }

          // 숙박 데이터 처리
          JSONObject stayLabel = step(stay, "label"),
              stayPriceTable = step(stay, "price");
          int stayPrice = 0;
          String stayOption = null, stayTime = null;
          try {
            stayTime = stayLabel.get("checkInOut").toString();
            stayPrice = Integer.parseInt(stayPriceTable.get("strikePrice").toString());
            stayOption = stayLabel.get("option").toString();
          } catch (Exception ignore) {}

          int stock = Integer.parseInt(stay.get("stockCount").toString());
          if (rentStock != null && rentStock > stock) stock = rentStock;

          // 인원수
          Integer standardCount = null, maxCount = null;
          for (Object promotionObject : stepArr(roomJSON, "promotions")) {
            JSONObject promotion = (JSONObject) promotionObject;
            if (!promotion.get("title").toString().equals("객실정보")) continue;
            JSONObject content = step(promotion, "content");
            String text = content.get("text").toString();
            for (char c : text.toCharArray()) {
              if (c >= '0' && c <= '9') {
                if (standardCount == null) standardCount = c - '0';
                else {
                  maxCount = c - '0';
                  break;
                }
              }
            }
          }
          if (standardCount == null) standardCount = 0;
          if (maxCount == null) maxCount = 0;

          // Room 저장
          Room room = roomRepository.save(Room.builder()
                  .lodgeId(lodge.getLodgeId())
                  .roomName(roomJSON.get("name").toString())
                  .rentPrice(rentPrice)
                  .rentTime(rentTime)
                  .stayPrice(stayPrice)
                  .stayOption(stayOption)
                  .stayTime(stayTime)
                  .stockCount(stock)
                  .standardCount(standardCount)
                  .maxCount(maxCount)
                  .build());
          rooms.add(room);
          // RoomImage 저장
          for (Object roomImageObject : stepArr(roomJSON, "images")) {
            JSONObject roomImage = (JSONObject) roomImageObject;
            roomImgs.add(roomImgRepository.save(RoomImg.builder()
                .roomId(room.getRoomId())
                .roomImgUrl(roomImage.get("image").toString())
                .build()));
          }
        }
        System.out.println(code + " 분석 완료");
      } catch (Exception ignore) {
        fail.add(lodgeNumbers.get(i));
      }

      try {
        entityManager.persist(lodge);
        for (LodgeImg lodgeImg : lodgeImgs) entityManager.persist(lodgeImg);
        for (Room room : rooms) entityManager.persist(room);
        for (RoomImg roomImg : roomImgs) entityManager.persist(roomImg);
        for (Ways way : ways) entityManager.persist(way);

        entityManager.flush();
        success.add(lodgeNumbers.get(i));
      } catch (Exception e) {

        entityManager.clear();
        fail.add(lodgeNumbers.get(i));
      }
    }

    System.out.print("성공 : ");
    for (String code : success) System.out.println(code + ", ");
    System.out.println();

    System.out.println("실패 : ");
    for (String code : fail) System.out.println(code + ", ");
    System.out.println();
  }

  JSONObject step(Object base, String key) throws ParseException {
    JSONObject json = (JSONObject) base;
    Object target = json.get(key);
    if (target == null) return null;
    return (JSONObject) (parser.parse(target.toString()));
  }

  JSONArray stepArr(Object base, String key) throws ParseException {
    JSONObject json = (JSONObject) base;
    Object target = json.get(key);
    if (target == null) return null;
    return (JSONArray) (parser.parse(target.toString()));
  }

  Document getJsoupConnectionDoc(String url) throws IOException {
    return Jsoup.connect(url).get();
  }

  Elements getJsoupElements(Document doc) {
    return doc.select("a");
  }

  List<String> loadList() throws IOException {
    List<String> lodgeNumbers = new ArrayList<>();

    String[] places = { "경기", "제주도", "서울", "인천", "대구", "대전", "충남", "경남", "부산", "전북", "울산", "광주", "강원", "경북", "전남", "충북", "세종" };
    String[] places1 = {"서울", "인천", "대구"},
        places2 = {"대전", "부산", "울산", "광주"},
        places3 = {"경기", "제주도", "충남", "경남"},
        places4 = {"전북", "강원", "경북", "전남", "충북"};

    String base = "https://www.yeogi.com/domestic-accommodations?category=0&keyword=%s&page=1";

    for (String place : places2) {
      String src = String.format(base, place);
      Document document = getJsoupConnectionDoc(src);

      for (Element link : getJsoupElements(document)) {
        if (link.hasClass("gc-thumbnail-type-seller-card") && link.hasClass("css-wels0m")) {
          String code = link.attr("href").split("/")[2].split("\\?")[0];
          lodgeNumbers.add(code);
        }
      }
    }

    return lodgeNumbers;
  }
}
