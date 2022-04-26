# Movie Battle
In this game the user receives two movie names, and he needs to answer which one has a higher score 
(imdb rate * number of votes).
The game ends in the 3rd wrong answer.

### Requisitos
* Java 11
* Git

### How to test
`./gradlew clean test`

### How to start up
`./gradlew clean bootRun`

### How execute
`http://localhost:8080/swagger-ui/`

### How to play

#### Login

```
curl --location --request POST 'http://localhost:8080/oauth/token' \
    --header 'Authorization: Basic bXluYW1lOnNlY3JldDEyMw==' \
    --header 'Content-Type: application/x-www-form-urlencoded' \
    --data-urlencode 'username=bruce.wayne@gmail.com' \
    --data-urlencode 'password=I am Batman' \
    --data-urlencode 'grant_type=password'
```

#### Initiate session

```
curl --location --request POST 'http://localhost:8080/api/movie-battle/sessions' \
--header 'Authorization: Bearer TOKEN'
```

#### Fetch a movie pair
```
curl --location --request POST 'http://localhost:8080/api/movie-battle/movies' \
    --header 'Authorization: Bearer TOKEN' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "sessionId" : "5fb2f25f-05aa-42c3-b4bc-dd56f7993528"
    }'
```

#### Chose which movie you think it has a higher score
```
curl --location --request POST 'http://localhost:8080/api/movie-battle/movies/reply' \
    --header 'Authorization: Bearer TOKEN' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "sessionId":"5fb2f25f-05aa-42c3-b4bc-dd56f7993528",
        "answer":"tt0469611"
    }'
```

#### Fetch more movies
Call getMovies and answer to get more points.
The when ends when user makes 3 mistakes and the session dies.


#### Finish the session
It's also possible to end the session before the 3 errors.

```
curl --location --request DELETE 'http://localhost:8080/api/movie-battle/sessions/1f9ffc73-cc3e-4708-9196-f28943c10af9' \
--header 'Authorization: Bearer TOKEN'

```

#### See your name in the Hall of Fame

Displays Top 10 players and their scores

```
curl --location --request GET 'http://localhost:8080/api/movie-battle/players' \
--header 'Authorization: Bearer TOKEN'

```

### Load Movies (Opcional)
Endpoint that loads movies from OMDB API. (key required)
(198 movies included in data.sql, so you are good to go)
You need to be an ADMIN user to be able to access that endpoint.

```
curl --location --request POST 'http://localhost:8080/api/load-movies?keywords=Super' \
--header 'Authorization: Bearer TOKEN'
```