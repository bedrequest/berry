# BedRequest 숙박 프로젝트  

<img src="https://capsule-render.vercel.app/api?type=waving&color=gradient&height=180&section=header&text=BedRequest&fontSize=50&fontAlignY=35" />

---

## 📘 프로젝트 소개
> 숙박 예약 플랫폼을 벤치마킹하여 사용자 친화적인 UI/UX와 안정적인 백엔드 로직을 구현한 프로젝트입니다.
> 수업 과정에서 배운 다양한 기능들을 사용하기에 가장 적합하다고 생각하여 숙박이란 주제를 선정하였습니다.

## 주요 기능
> * 회원가입 및 로그인
>   - 웹 로그인과 소셜(네이버, 카카오, 구글) 로그인
>   - 이메일 및 휴대폰 본인인증
>   - 마이페이지 : 회원 정보 수정, 탈퇴, 예약 내역 관리
> * 숙소 검색 및 조회
>   - 숙소 검색 시 다양한 필터 옵션 제공, 정렬 가능
>   - 회원일 경우 숙소를 북마크하여 마이페이지에서 열람 가능
> * 결제 및 환불, 쿠폰을 통한 할인 기능
> * 리뷰
>   - 기본적으로 열람할 수 있으며 회원일 경우 CRUD 가능
>   - 리뷰 태그 통계 및 차트 제공 : 리뷰 작성 시 작성자가 사이트에서 제공하는 태그 안에서 선택함
>   - 리뷰 AI 요약
> * Q&A

## 기술 스택
> * 백엔드 : Spring Boot 3.5.13, Java 17, JPA, Spring Security
> * 프론트엔드 : HTML, Thymeleaf, CSS, Javascript
> * 데이터베이스 : MySQL
> * 빌드 도구 : Gradle
> * 기타 : Git, GitHub

## 실행 방법
> 1. Git 클론
> 2. MySQL DB 연결 : \src\main\application.properties 참조
> 3. external_api.properties를 작성하여 클론받은 폴더의 최상단에 배치 : 세부 사항은 프로젝트 폴더 최상단에 있는 api_keys.txt 파일 참조
> 4. Gradle 빌드
> 5. 서버 실행

## 팀원
> 1. 이유현 : 숙소 전반, 메인 페이지, Git 총괄
> 2. 김민수 : 리뷰 전반, 메인 페이지, 헤더, 푸터, 디자인 총괄
> 3. 조해찬 : 유저 전반, 메인 페이지
> 4. 이영석 : 결제, 관리자 페이지
> 5. 박주선 : Q&A 프론트엔드
> 6. 정진환 : Q&A 백엔드
