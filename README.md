JPA - pring mysql://sample/demo

http://localhost:8080/sample/demo/

Query-DSL - pring mysql://sample/demo

http://localhost:8080/sample/demo/querydsl

# 테스트코드 작성시
Docker를 실행해주세여...TestContainer 올릴 시 필요합니당

# 상미기한별 재고 배치 작업(유통기한별 재고)
* 스케쥴러(매일 00시 10분 stock_by_best_before 테이블 저장)
1. nosnos api 호출
   1. |Request Param|   |   |   |
         |---|---|---|---|
      |데이터필드|자료형|설명|비고|
      |member_id|integer|고객사 ID|필수|
      |shipping_product_ids|integer|출고상품ID|필수 (최대 50개)|
      |page|integer|페이지 번호|   |
   2. response
      2. ```json
         {
         "code": "9999",
         "message": "ok",
         "response": {
         "total_count": 117,
         "total_page": 12,
         "current_page": 1,
         "data_list": [
                        { 
                          "shipping_product_id": "41667",
                          "expire_date(유통기한)": "19700102",
                          "total_stock(총재고)": "11376",
                          "normal_stock(가용재고)": "10460"
                        },
                        {
                          "shipping_product_id": "41667",
                          "expire_date": "19700101",
                          "total_stock": "2",
                          "normal_stock": "2"
                        }
                      ]
                    }
         }
         ```
2. 상미기한 계산
   1. 상미기한 (%) =  (유통기한-오늘) / (유통기한-제조일자) x 100
   2. |  | 상황1 | 상황2 |
      | ------- | --- | --- |
      |유통기한| O | O |
      |제조일자| O | X |
      |연산방식| NOSNOS 값 그대로 넣기 | 기본상품DB의 "제조일자로부터 X일" 활용 |
      |  |  | 제조일자 = 유통기한 - X |
# 재고 변동 시점(노스노스 기준)
1. normal_stock(가용재고)
   1. 발주처리->출고요청->출고지시 일 경우 차감
2. total_stock(총재고)
   1. 발주처리->출고요청->출고지시->출고완료 일 경우 차감
   
# 상미기한별 재고 -> 유통기한별 재고 변경
1. 유통기간 날짜별 상태(2022-05-24기준 유통기간이 10일 부터 임박인 경우)
   - expirationDateFrom <= expiration_date < expirationDateTo   
   1. 유통기간 초과
      1. expirationDateFrom - null          
      2. expirationDateTo - 2022-05-23(23일부터 이전 유효기간 조회)
         1. sql : expiration_date < 2022-05-24 00:00:00(day+1)
   2. 유통기간 임박
      1. expirationDateFrom - 2022-05-24(오늘날짜)
         1. sql : 2022-05-24 00:00:00 <= expiration_date
      2. expirationDateTo - 2022-06-02(기준일 10일)
         1. sql : expiration_date < 2022-06-03 00:00:00(day+1)
   3. 유통기간 안정
      1. expirationDateFrom - 2022-06-03(03일 부터 이후 유효기간 조회)
         1. sql : 2022-06-23 00:00:00 <= expiration_date
      2. expirationDateTo - null