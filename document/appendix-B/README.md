# 부록 B (악취 제거 기법)

***

## 악취와 탈취용 리팩토링 기법

__악취의 종류를 구별하는 것과 각각의 리팩토링 기법의 목적이 뭔지 이해하고 이를 통해 얻는 효과와 놓치는 단점이 뭔지 정리해놓는게 중요하다.__ 

### 가변 데이터 (Mutable Data)

- 변수 캡슐화 하기 (6.6 절)

- 변수 쪼개기 (9.1 절)

- 문장 슬라이스 하기 (8.6 절)

- 함수 추출하기 (6.1 절)

- 질의 함수와 변경 함수 분리하기 (11.1 절)

- 세터 제거하기 (11.7 절)

- 파생 변수를 질의 함수로 바꾸기 (9.3 절)

- 여러 함수를 클래스로 묶기 (6.9 절)

- 여러 함수를 변환 함수로 묶기 (6.10 절)

- 참조를 값으로 바꾸기 (9.4 절)