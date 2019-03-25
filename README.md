# 카카오페이 2019 상반기 경력공채 사전과제 - 주택금융 API 개발
## 개발환경
* 언어: Java 8
* 프레임워크: Spring Boot 1.5 (web, data-jpa)
* 데이터베이스: H2
## 빌드 및 실행
### jar 추출 후 실행
Gradle Wrapper를 이용해서 빌드 및 실행할 수 있습니다.
```
./gradlew bootRepackage
```
빌드된 jar 파일은 다음 경로에서 확인하세요.
```
cd builds/libs
```
jar 파일을 이용해서 서버 어플리케이션을 실행하는 방법은 다음과 같습니다.
```
java -server -jar homework-0.0.1.jar 
```
### Gradle Wrapper로 실행
Gradle Wrapper를 사용해서 jar 파일을 생성하지 않고 실행할 수 있습니다.
```
./gradlew bootRun
```
### Test 실행
Gradle Wrapper를 사용해서 Test Code를 실행할 수 있습니다.
```
./gradlew test
```
## 문제 해결 전략
### Entity 정의
#### 1. MonthlyMortgage
한 금융기관이 특정 월에 제공한 신용보증 금액을 나타냅니다. 이에 대한 Entity를 다음과 같이 정의하였습니다. (불필요한 annotation 생략)
```
@Entity
@Table(name = "MONTHLY_MORTGAGE", indexes = {
        @Index(name = "year_month_institute", columnList = "year,month,institute", unique = true),
        @Index(name = "institute", columnList = "institute")
})
public class MonthlyMortgage {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "YEAR", nullable = false)
    private Integer year;

    @Column(name = "MONTH", nullable = false)
    private Integer month;

    @Column(name = "AMOUNT_100M", nullable = false)
    private Integer amount100M;

    @ManyToOne
    @JoinColumn(name = "INSTITUTE", nullable = false)
    private Institute institute;
}

}
```
- `(year, month, institute)`에 Unique index를 설정하여, 같은 연월에 한 금융기관의 지원액은 하나만 존재할 수 있도록 하였습니다.
- `MonthlyMortgage`와 `Institute`는 N:1 관계로 구성되어 있습니다. foreign key인 `INSTITUTE`는 `Institute`의 `id`값을 가집니다.    
#### 2. `Institute`
금융기관에 대한 Entity는 다음과 같이 정의하였습니다. (불필요한 annotation은 생략)
```
@Entity
@Table(name = "INSTITUTE", indexes = {
        @Index(name = "institute_name", columnList = "name", unique = true),
        @Index(name = "institute_code", columnList = "code", unique = true)
})
public class Institute {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "CODE", nullable = false)
    private String code;

    @OneToMany(mappedBy = "institute")
    private List<MonthlyMortgage> monthlyMortgageList = new ArrayList<>();
}
```
- `Institute`를 효율적으로 검색하기 위해 `name`과 `code`에 모두 index를 설정했습니다. 두 index 모두 unique로 지정하여 `code`와 `name`이 중복으로 입력되지 않도록 했습니다.
- `MonthlyMortage`는 foreign key로 `Institute`의 `id`를 가지고 있고, `Institute`는 이를 이용하여 연관관계를 가진 모든 `MonthlyMortgage`를 역참조합니다. 
Table 관점에서 봤을 때, `Institute`는 `MonthlyMortgage`에 대한 어떠한 foreign key도 가지지 않습니다.
- `Institute`에 대한 정보는 `application.yml`에 정의되어 있고, 이 정보는 서버 어플리케이션이 구동되는 시점에 DB에 저장됩니다. (`class InstituteConfig`를 참고)
#### 3. `User`
인증에 사용되는 유저 정보에 대한 Entity는 다음과 같이 정의하였습니다.
```
@Entity
@Table(name = "USER")
public class User {

    @Id
    @Column(name = "ID", nullable = false)
    private String id;

    @Column(name = "PASSWORD", nullable = false)
    private String encodedPassword;
}
```
- `MonthlyMortgage`, `Institute`와는 다르게 `id`를 자동으로 생성하지 않고, 입력 받은 유저의 ID를 Primary key로 사용합니다.
### 주요 로직
#### 연도별 각 금융기관의 지원금액 합계
1. 모든 `MonthlyMortgage`를 `year`를 기준으로 grouping 합니다. 결과는 다음과 같은 형태일 것입니다. (`Map<Integer, List<MonthlyMortgage>>`)
2. 1의 결과인 `Map`의 value(`List<MonthlyMortgage>`)에 대하여 `amount`의 합계를 구합니다. 이것이 연도별 모든 금융기관의 지원금액 합계입니다.
3. 1의 결과인 `Map`의 value(`List<MonthlyMortgage>`)를 `Institute`의 `name`을 이용하여 한 번 더 grouping 합니다. 
`List<MonthlyMortgage>`가 `Map<String, List<MonthlyMortgage>>`으로 변환될 것입니다.
4. 3의 결과인 `Map<String, List<MonthlyMortgage>>`의 value(`List<MonthlyMortgage>`)에 대해 `amount`의 합계를 구합니다. 이것이 (연도별, 금융기관별) 지원금액 합계입니다.
#### 연도별 각 기관의 전체 지원금액 중 가장 큰 금액과 기관명
1. 모든 `MonthlyMortgage`를 `year`와 `Institute`의 `name`으로 grouping 합니다. 결과는 `Map<Integer, Map<String, List<MonthlyMortgage>>>`가 될 것입니다.
2. 1의 결과를 이용해서 (연도, 금융기관)별 지원금액 합계를 계산합니다. 결과는 `Map<Integer, Map<String, Integer>>`가 될 것입니다
3. 모든 (연도, 금융기관)의 지원금액 합계 중 최대를 찾기 위해, 2의 결과를 Flatten하여 Linear한 형태로 만들어줍니다. 결과는 `Collection<year, name, sumOfAmount>` 형태가 될 것입니다.
4. 3의 결과에서 `amount`의 합이 최대가 되는 `(year, name, sumOfAmount)`을 찾습니다.
#### 특정 기관의 연도별 지원금액 평균 중 가장 큰 금액과 가장 작은 금액
1. 입력으로 받은 금융기관의 `code`를 이용하여 `Institute` Entity를 찾습니다.
2. 1에서 찾은 `Institute`는 자신의 모든 월별 지원금액의 리스트를 역참조하고 있습니다. 이는 `List<MonthlyMortgage>` 형태로 정의되어 있습니다.
3. 2에서 찾은 `List<MonthlyMortgage>`를 `year`로 grouping 합니다. 결과는 `Map<Integer, List<MonthlyMortgage>>` 형태가 될 것입니다.
4. 3의 결과에서 각 value(`List<MonthlyMortgage>`)에 대한 평균값을 계산합니다. 결과는 `Map<Integer, Double>`이 될 것입니다.
5. 4의 결과를 Flatten하여 Linear한 `Collection`으로 변환한 이후, 최대와 최소를 각각 찾습니다.
#### JWT 인증
* 유저의 비밀번호는 Base64로 encoding하여 DB에 저장합니다.
* JWT Token은 HS256 Algorithm을 사용하여 encoding 하는데, 이 때 `application.yml`에 정의된 256bits Secret key를 사용합니다.
* JWT Token의 만료기한은 발급된 시간으로부터 1시간 입니다. `application.yml`에서 만료기한을 설정할 수 있습니다.
* JWT Token은 payload에 유저의 ID를 포함하고 있습니다. 이를 이용하여 유저 인증을 수행합니다.
## API 목록
### Sign Up
유저의 ID와 Password를 입력받아 DB에 저장합니다.
#### Request
```
curl -X POST http://localhost:8080/auth/signup \
  -H 'Content-Type: application/json' \
  -d '{
    "id": "busungkim",
    "password": "1234"
}'
```
#### Response
```
200 OK
```
### Sign in
유저의 ID와 Password를 입력받아 인증처리 후 Access Token을 발급합니다.
#### Request
```
curl -X POST \
  http://localhost:8080/auth/signin \
  -H 'Content-Type: application/json' \
  -d '{
	"id": "busungkim",
	"password": "1234"
}'
```
#### Response
```
200 OK
{
    "userId": "busungkim",
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJidXN1bmdraW0iLCJpYXQiOjE1NTM1MDQ3NTEsImV4cCI6MTU1MzUwODM1MX0.DQZSc_SWF5Kg3YNlRWPWqxsII_oWem4gbx91o82PREs"
}
```
### Refresh Access Token
만료된 Access Token을 입력받아 새로운 Access Token을 발급합니다.
#### Request
```
curl -X POST \
  http://localhost:8080/auth/refresh \
  -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJidXN1bmdraW0iLCJpYXQiOjE1NTM1MDc0OTcsImV4cCI6MTU1MzUxMTA5N30.IgjqNkF0X_GTiWOkPQFAQT0OHUmyY_zSnr7uuMZ4oVc' \
  -H 'Content-Type: application/json'
```
#### Response
```
200 OK
{
    "userId": "busungkim",
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJidXN1bmdraW0iLCJpYXQiOjE1NTM1MDc1MDUsImV4cCI6MTU1MzUxMTEwNX0.L_iJ7uuPBYSptczlrtdc-7NFhe2CO47DGPJJL8kh--g"
}
```
### Persist Local CSV
서버에 존재하는 csv 파일(`resources/{filename}`)내 데이터를 DB에 저장합니다.
#### Request
```
curl -X POST \
  http://localhost:8080/v1/local-csv \
  -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJidXN1bmdraW0iLCJpYXQiOjE1NTM1MTA2MjAsImV4cCI6MTU1MzUxNDIyMH0.5F_eo6M9mWY6YrXTKWKvmJSUriMGknWEkfpHMNaaRZg' \
  -H 'Content-Type: application/json' \
  -d '{
	"fileName": "data.csv",
	"charset": "UTF-8"
}'
```
#### Response
```
200 OK
```
### Get All Institutes
DB에 저장되어 있는 모든 금융기관의 이름과 코드 목록을 반환합니다.
#### Request
```
curl -X GET \
  http://localhost:8080/v1/institutes \
  -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJidXN1bmdraW0iLCJpYXQiOjE1NTM1MTA2MjAsImV4cCI6MTU1MzUxNDIyMH0.5F_eo6M9mWY6YrXTKWKvmJSUriMGknWEkfpHMNaaRZg'
```
#### Response
```
200 OK
[
    {
        "name": "주택도시기금",
        "code": "jutek"
    },
    {
        "name": "국민은행",
        "code": "kookmin"
    },
    ...
]
```
### Get Yearly Sum
연도별 각 기관의 지원금액 합계를 반환합니다.
#### Request
```
curl -X GET \
  http://localhost:8080/v1/mortgages/year/sum \
  -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJidXN1bmdraW0iLCJpYXQiOjE1NTM1MDg1NjQsImV4cCI6MTU1MzUxMjE2NH0.vQi6JWzQPQF9XPhIrfsw37L9kDanEmadzGBekQ6M6-Y' \
  -H 'Content-Type: application/json'
```
#### Response
```
200 OK
[
    {
        "year": 2016,
        "totalAmount": 400971,
        "detailedByInstitute": {
            "하나은행": 45485,
            "농협은행/수협은행": 23913,
            "우리은행": 45461,
            "국민은행": 61380,
            "신한은행": 36767,
            "주택도시기금": 91017,
            "외환은행": 5977,
            "한국시티은행": 46,
            "기타은행": 90925
        }
    },
    {
        "year": 2017,
        "totalAmount": 295126,
        "detailedByInstitute": {
            "하나은행": 35629,
            "농협은행/수협은행": 26969,
            "우리은행": 38846,
            "국민은행": 31480,
            "신한은행": 40729,
            "주택도시기금": 85409,
            "외환은행": 0,
            "한국시티은행": 7,
            "기타은행": 36057
        }
    },
    ...
]
```
### Get Max of Yearly Sum
연도별 각 기관의 전체 지원금액 중 가장 큰 금액과 기관명을 반환합니다.
#### Request
```
curl -X GET \
  http://localhost:8080/v1/mortgages/year/max \
  -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJidXN1bmciLCJpYXQiOjE1NTM0MTYzMTMsImV4cCI6MTU1MzQxOTkxM30.aYRQxSQ3J1RIcV5CVtKzdROMzHC_edfQNqKXQsiehOM' \
  -H 'Content-Type: application/json'
```
#### Response
```
200 OK
{
    "year": 2014,
    "instituteName": "주택도시기금",
    "amount": 96184
}
```
### Get Max and Min of Yearly Sum of Given Institute
특정 기관의 연도별 지원금액 평균 중 가장 큰 값과 가장 작은 값을 출력합니다.
#### Request
```
curl -X GET \
  http://localhost:8080/v1/mortgages/year/average/kookmin \
  -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJidXN1bmdraW0iLCJpYXQiOjE1NTM1MTA2MjAsImV4cCI6MTU1MzUxNDIyMH0.5F_eo6M9mWY6YrXTKWKvmJSUriMGknWEkfpHMNaaRZg' \
  -H 'Content-Type: application/json'
```
#### Response
```
200 OK
{
    "name": "국민은행",
    "minOfAvg": {
        "year": 2006,
        "average": 484.25
    },
    "maxOfAvg": {
        "year": 2016,
        "average": 5115
    }
}
```
## 사용 순서
1. Sign Up API를 호출하여 회원가입을 진행합니다.
2. Sign In API를 호출하여 Access Token을 받습니다.
3. 2에서 받은 Access Token을 Authorization Header에 넣고 POST /v1/local-csv API를 호출하여 DB에 데이터를 저장합니다.
4. 3과 마찬가지로 Access Token을 사용하여 각 Aggregation API를 호출합니다.