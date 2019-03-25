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
```
./gradlew bootRun
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
- (year, month, institute)에 Unique index를 설정하여, 같은 연월에 한 금융기관의 지원액은 하나만 존재하도록 했습니다.
- MonthlyMortgage와 Institute는 N:1 관계로 구성되어 있고, foreign key인 INSTITUTE를 이용해서 Join 합니다.   
#### 2. Institute
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
- 효율적으로 검색하기 위해 name과 code에 모두 index를 설정했습니다. 또한, unique로 설정하여 같은 code 또는 같은 name이 입력되지 않도록 했습니다.
- MonthlyMortage는 foreign key로 Institute의 id를 가지고 있고, 이를 이용하여 해당 Institute와 연관관계를 가진 모든 MonthlyMortgage를 역참조합니다. 
Table 관점에서 봤을 때, Institute는 MonthlyMortgage에 대한 어떠한 foreign key도 가지지 않습니다.
- Institute에 대한 정보는 `application.yml`에 정의되어 있고, 이 정보는 서버 어플리케이션이 구동되는 시점에 DB에 저장됩니다. (`InstituteConfig` class를 참고)
#### 3. User
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
- MonthlyMortgage, Institute와는 다르게 Id를 자동으로 생성하지 않고 유저의 ID를 Primary key로 사용합니다.
### 주요 로직
#### 연도별 각 금융기관의 지원금액 합계
1. 모든 MonthlyMortgage를 year를 기준으로 grouping 합니다. 결과는 다음과 같은 형태일 것입니다. (Map<Integer, List\<MonthlyMortgage>>)
2. 1의 결과인 Map의 value(List\<MonthlyMortgage>)에 대하여 amount의 합계를 구합니다. 이것이 연도별 모든 금융기관의 지원금액 합계입니다.
3. 1의 결과인 Map의 value(List\<MonthlyMortgage>)를 institute의 name을 이용하여 한 번 더 grouping 합니다. 
List\<MonthlyMortgage>가 Map<String, List\<MonthlyMortgage>>가 될 것입니다.
4. 3의 결과인 Map<String, List\<MonthlyMortgage>>의 value(List\<MonthlyMortgage>)에 대해 amount의 합계를 구합니다. 이것이 (연도별, 금융기관별) 지원금액 합계입니다.
#### 연도별 각 기관의 전체 지원금액 중 가장 큰 금액과 기관명
1. 모든 MonthlyMortgage를 year와 institute의 name으로 grouping 합니다. 결과는 Map<Integer, Map<String, List\<MonthlyMortgage>>>가 될 것입니다.
2. 1의 결과를 이용해서 (연도, 금융기관)별 지원금액 합계를 계산합니다. 결과는 Map<Integer, Map<String, Integer>>가 될 것입니다
3. 모든 (연도, 금융기관)의 지원금액 합계 중 최대를 찾기 위해, 2의 결과를 Flatten하여 Linear한 형태로 만들어줍니다. 결과는 List<year, name, sumOfAmount> 형태가 될 것입니다.
4. 3의 결과에서 sumOfAmount가 최대가 되는 (year, name, sumOfAmount) Triplet을 찾습니다.
#### 연도별 특정 기관의 지원금액 평균 중 가장 큰 금액과 가장 작은 금액
1. 입력으로 받은 금융기관의 Code를 이용하여 Institute Entity를 찾습니다.
2. 1에서 찾은 Institute는 자신의 모든 월별 지원금액의 리스트를 역참조하고 있습니다. 이는 List\<MonthlyMortgage> 형태로 정의 되어 있습니다.
3. 2에서 찾은 List\<MonthlyMortgage>를 year로 grouping 합니다. 결과는 Map<Integer, List\<MonthlyMortgage>> 형태가 될 것입니다.
4. 3의 결과에서 각 value(List\<MonthlyMortgage>)에 대한 평균값을 계산합니다. 결과는 Map<Integer, Double>이 될 것입니다.
5. 4의 결과를 Flatten하여 Linear한 Collection으로 변환한 이후, 최대와 최소를 각각 계산합니다.
#### JWT 인증
* 유저의 Password는 Base64로 encoding하여 DB에 저장합니다.
* JWT Token은 HS256 Algorithm을 사용하여 encoding 하는데, 이 때, `application.yml`에 정의된 256bits Secret key를 사용합니다.
* JWT Token의 만료기한은 발급된 시간으로부터 1시간 입니다. `application.yml`에서 만료기한을 설정할 수 있습니다.
* JWT Token은 payload에 UserId를 포함하고 있습니다. 이 UserId를 이용하여 유저 인증을 수행합니다.  