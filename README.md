
## [MUSINSA] Java(Kotlin) Backend Engineer - 과제

## 구현 범위에 대한 설명
    1. 개발 환경
        - kotlin
        - springboot
        - spring-integration
        - jpa
    2. 구현 설명
        - admin 에서 상품 등록/수정/삭제를 할 시에 랭킹 정보를 업데이트 한다.
        - message queue 기반 사용이 어려움으로 spring-integration PublishSubscribeChannel 을 이용하여 pub/sub 구조로 구현.
            - 브랜드 랭킹 업데이트
            - 카테고리 랭킹 업데이트
        - boot 시 초기 정보를 처리한다. (InitialService)

## 코드 빌드, 테스트 실행 방법
    1. 빌드
        - gradle build
    2. 테스트 실행
        - gradle test
    3. 실행
        - gradle bootRun

## API 명세
    1. 카테고리 별 최저가격 브랜드와 상품 가격, 총액을 조회하는 API
        - api:  GET http://localhost:8080/category/lowest
        - test case
            - 카테고리 별 최저가 조회시 성공 한다
            - 전체 카테고리 최저가 변경 시 변경된 값으로 갱신된다
            - 전체 카테고리 최저가 삭제 시 변경된 값으로 갱신된다
            - 전체 카테고리 최저가 생성 시 변경된 값으로 갱신된다
    2. 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회하는 API
        - api:  GET http://localhost:8080/brand/lowest
        - test case
            - 브랜드 최저가 조회가 잘 된다
            - 브랜드 최저가 조회시 카테고리가 하나라도 지워지면 다른 브랜드로 변경된다
            - 브랜드 최저가 조회시 다른 브랜드 상품을 가격을 최저가로 변경하면 브랜드가 변경된다
            - 브랜드 최저가 조회시 신규 브랜드 상품을 추가하면 브랜드가 추가된다
    3. 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API
        - api:  GET http://localhost:8080/category/rank?name={categoryName}
                GET http://localhost:8080/category/rank?name=바지
        - test case
            - 카테고리 이름별 조회시 조회가 잘 이루어 진다
            - 카테고리 이름별 조회시 최저가 상품 생성 시 변경된 값으로 갱신된다
            - 카테고리 이름별 조회시 최고가 상품 생성 시 변경된 값으로 갱신된다
            - 카테고리 이름별 조회시 최저가 상품 변경 시 변경된 값으로 갱신된다
            - 카테고리 이름별 조회시 최고가 상품 변경 시 변경된 값으로 갱신된다
            - 카테고리 이름별 조회시 최저가 상품 삭제 시 변경된 값으로 갱신된다
            - 카테고리 이름별 조회시 최고가 상품 삭제 시 변경된 값으로 갱신된다
    4. 브랜드 및 상품을 추가 / 업데이트 / 삭제하는 API
        - 조회 api:  GET http://localhost:8080/admin/product/{id} (id 별)
                    GET http://localhost:8080/admin/product/brand/{brand} (브랜드 별)
                        http://localhost:8080/admin/product/brand/A
                    GET http://localhost:8080/admin/product/category/{category} (카테고리 별)
                        http://localhost:8080/admin/product/category/바지
        - 추가 api:  POST http://localhost:8080/admin/product
        - 업데이트 api: PUT http://localhost:8080/admin/product
        - 삭제 api: DELETE http://localhost:8080/admin/product/{id}
        - test case
            - 상품 조회시 id 로 조회 가능
            - 상품 조회시 id 로 조회 실패시 404 반환
            - 상품 생성 시 해당 브랜드와 카테고리가 없으면 생성 성공
            - 상품 생성 시 해당 브랜드와 카테고리가 중복이 있으면 실패한다
            - 상품 변경 시 저장 되어 있는 상품이면 성공한다
            - 상품 변경시 저장 되어 있지 않은 상품이면 실패한다
            - 상품 변경시 중복 브랜드 카테고리 상품이면 실패한다
            - 상품 삭제시 존재 하는 id 호출시 성공 한다
            - 상품 삭제시 존재 하지 않는 id 호출시 실패 한다