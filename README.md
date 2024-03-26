# be-was-2024
코드스쿼드 백엔드 교육용 WAS 2024 개정판

# GitHub Wiki
- 학습 내용 정리: [WAS1 - Wiki](https://github.com/Yeriimii/be-was-neon/wiki/Java-Concurrent-%E2%80%90-CompletableFuture)

# 전체 요청 흐름
```mermaid
---
title: 요청 흐름도 
---
flowchart LR
    subgraph RequestHandler
        direction LR
        subgraph convert
            direction TB
            HttpHeaderParser --Header 정보--> HttpConverter
            HttpConverter --파싱 요청--> HttpHeaderParser
        end
        subgraph mapping
            UriMapper
        end
        subgraph process
            direction TB
            ResourceHandler --byte array--> HttpProcessor
            HttpProcessor --static file path--> ResourceHandler
        end
        HttpRequest
        HttpResponse
    end
WebServer --Socket--> RequestHandler
HttpConverter --> HttpRequest
HttpConverter --> HttpResponse
HttpRequest --URI--> UriMapper --> HttpProcessor --write--> HttpResponse --> Client
```

## HTTP GET 요청 흐름 (`/index.html`, `/registration`)
<details>
<summary>접기/펼치기</summary>

```mermaid
sequenceDiagram
    actor client
    client->>WebServer: 1. GET 요청: '/index.html'
    activate WebServer
    WebServer->>WebServer: 2. accept --> Socket(connection) 생성 
    deactivate WebServer
    WebServer->>RequestHandler: 3. Socket(connection) 전달
    activate RequestHandler
    RequestHandler->>RequestHandler: 4. HttpConverter --> `HttpRequest`, `HttpResponse` 생성
    RequestHandler->>UriMapper: 5. `HttpRequest`를 처리할 수 있는 `Processor` 전달 요청
    UriMapper-->>RequestHandler: 6. `Processor` 반환 (HttpRequest, HttpResponse 처리)
    RequestHandler->>RequestHandler: 7. `Processor` 로직 실행 (없으면 404 Not Found)
    RequestHandler-->>client: 8. `HttpResponse` 응답
    deactivate RequestHandler
    activate client
    client-->>client: 9. 화면 구성
    deactivate client
```
</details>

## 회원가입 흐름 (POST 요청)
<details>
<summary>접기/펼치기</summary>

```mermaid
sequenceDiagram
    actor client
    client->>WebServer: 1. POST /registration HTTP/1.1: http body -> "id=yelly&password=qwerty"
    WebServer->>RequestHandler: 2. Socket(connection) 전달
    activate RequestHandler
    RequestHandler->>RequestHandler: 3. UriMapper 통해 회원가입 처리할 Processor 찾음 (MemberSave)
    RequestHandler->>Processor: 4. HttpRequest 처리 요청
    activate Processor
    Processor->>Processor: 5. User 생성
    Processor->>Database: 6. User 등록 요청
    Processor->>RequestHandler: 7. 응답 헤더에 302 FOUND /index.html 입력
    deactivate Processor
    RequestHandler-->>client: 7. HttpResponse 반환 (302 FOUND 리다이렉션)
    deactivate RequestHandler
    client->>client: 8. `/index.html` 리다이렉션 (WebServer에 다시 GET 요청)
```
</details>

## 로그인 요청 흐름 (POST 요청)
<details>
<summary>접기/펼치기</summary>

```mermaid
sequenceDiagram
    actor client
    client->>WebServer: 1. POST /login HTTP/1.1: http body -> "id=yelly&password=qwerty"
    WebServer->>RequestHandler: 2. Socket(connection) 전달
    activate RequestHandler
    RequestHandler->>RequestHandler: 3. UriMapper 통해 로그인 처리할 Processor 찾음 (MemberLogin)
    RequestHandler->>Processor: 4. HttpRequest 처리 요청
    activate Processor
    Processor->>LoginManager: 5. LoginManager 검증 요청
    LoginManager-->>Processor: 6. Optional<User> 반환
    Processor->>SessionManager: 7. (User가 있으면) session 생성 및 등록 요청
    Processor-->>RequestHandler: 8-1. (User가 있으면) 응답 헤더에 302 FOUND /index.html 입력
    Processor-->>RequestHandler: 8-2. (User가 없으면) 응답 헤더에 302 FOUND /login-failed.html 입력
    deactivate Processor
    RequestHandler-->>client: 9. HttpResponse 반환 (302 FOUND 리다이렉션)
    deactivate RequestHandler
    client->>client: 10. `/` 리다이렉션 (WebServer에 다시 GET 요청)
```
</details>

## 유저 리스트 요청 흐름 (GET 요청)
<details>
<summary>접기/펼치기</summary>

```mermaid
sequenceDiagram
    actor client
    client->>WebServer: 1. GET /user/list HTTP/1.1
    WebServer->>RequestHandler: 2. Socket(connection) 전달
    activate RequestHandler
    RequestHandler->>RequestHandler: 3. UriMapper 통해 유저 리스트 페이지를 처리할 Processor 찾음 (MemberList)
    RequestHandler->>Processor: 4. HttpRequest 처리 요청
    activate Processor
    Processor->>SessionManager: 7. 세션 유저 요청
    SessionManager-->>Processor: 8. 세션 유저(Optional) 반환
    Processor-->>RequestHandler: 9-1. (세션 유저가 있으면) 동적 페이지 생성
    Processor-->>RequestHandler: 9-2. (세션 유저가 없으면) 응답 헤더에 302 FOUND '/login' 입력 
    deactivate Processor
    RequestHandler-->>client: 10. HttpResponse 반환
    deactivate RequestHandler
    client->>client: 11. HTML 화면 표시
```
</details>

## 로그아웃 요청 흐름 (GET 요청)
<details>
<summary>접기/펼치기</summary>

```mermaid
sequenceDiagram
    actor client
    client->>WebServer: 1. GET /logout HTTP/1.1
    WebServer->>RequestHandler: 2. Socket(connection) 전달
    activate RequestHandler
    RequestHandler->>RequestHandler: 3. UriMapper 통해 로그아웃 처리할 Processor 찾음 (MemberLogout)
    RequestHandler->>Processor: 4. HttpRequest 처리 요청
    activate Processor
    Processor->>SessionManager: 7. 세션이 존재하는지 확인
    SessionManager-->>SessionManager: 8. (세션이 있으면) 해당 세션 제거
    Processor-->>RequestHandler: 9-1. (세션이 있으면) 응답 헤더에 쿠키 만료 및 302 FOUND '/' 입력
    Processor-->>RequestHandler: 9-2. (세션이 없으면) 응답 헤더에 302 FOUND '/' 입력
    deactivate Processor
    RequestHandler-->>client: 10. HttpResponse 반환 (302 FOUND 리다이렉션)
    deactivate RequestHandler
    client->>client: 11. `/` 리다이렉션 (WebServer에 다시 GET 요청)
```
</details>

# 기능 구현 리스트
## RequestHandler
- [x] 요청 헤더(GET/Host/Connection/Accept)에 대해 파싱하고 로그로 출력할 수 있다
- [x] 'localhost:8080/index.html' 요청에 대해 정적 html을 응답할 수 있다

## HttpHeaderParser
- [x] request line, host, connection 등을 파싱할 수 있다
- [x] 쿼리 파라미터를 파싱해서 Map으로 변환할 수 있다

## HttpRequestConverter
- [x] Socket의 connection으로부터 requestString을 한 줄로 만들 수 있다
- [x] 한 줄의 requestString으로부터 HttpRequest 객체로 컨버팅 할 수 있다
- [x] http 요청에 body가 있을 경우 body 내용을 쿼리 파라미터로 가져올 수 있다

## HttpResponseConverter
- [x] Socket의 connection으로부터 HttpResponse를 생성할 수 있다

## ResourceHandler
- [x] 파일을 읽어 byte로 변환할 수 있다
- [x] URI 경로에서 확장자만 추출할 수 있다
- [x] 동적 HTML을 생성하기 위한 템플릿 파일을 읽어 문자열로 반환할 수 있다

## StaticMapper
- [x] 요청 URI에 해당하는 정적 파일을 매핑할 수 있다
- [x] 존재하지 않는 URI는 빈 Optional을 반환할 수 있다

## LoginManager
- [x] 데이터베이스에 등록된 유저인지 검증할 수 있다

## SessionManager
- [x] 세션 ID를 UUID 로 생성할 수 있다
- [x] 세션 ID를 key로, User 객체를 value 로 가질 수 있다
- [x] 세션 ID에 해당하는 User 객체를 찾을 수 있다
  - [x] 세션 ID에 해당하는 User 가 없으면 빈 Optional 을 반환할 수 있다 
- [x] 특정 세션 ID를 제거할 수 있다
- [x] Cookie 리스트로부터 세션 Key에 해당하는 세션 아이디를 찾을 수 있다

## Cookie
- [x] 쿠키 값을 설정하고 Path를 설정할 수 있다

## ArticleDatabase
- [x] 추가할 때 마다 id가 1씩 증가한다
- [x] 게시물 아이디로 찾을 수 있다
- [x] 작성자 아이디로 찾을 수 있다
