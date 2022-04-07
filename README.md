# Movie Battle
Neste game o usuario recebe o nome de dois filmes e ele precisa responder qual teria um
score maior (imdb rate * numero de votos).
O usuario pode errar ate 3 vezes.

### Decisoes de arquitetura
Optei por deixar a autenticacao e controle de usuario na aplicacao, senao o teste ficaria
muito extenso. E seguindo o mote (menos e mais), decidi deixar o pacote security, juntamente
com as rotas na mesma aplicacao.

Em um projeto real, com mais microservicos teriamos:
* Um **Discovery Server**, como Eureka ou Consul, para gerenciar o balanceamento de carga, 
caso o time time opte por nao usar algum loadbalancer da AWS, por exemplo.
* Um **Gateway** para controlar as rotas e permissoes. Nenhum microservico poderia
ser acessado diretamente, a nao ser pelo gateway.
* Um **Config Server**, para incluir as configuracoes por microservico e tambem as compartilhadas
* Um **Vault** integrado com o Config Server para gerenciar informacoes sensiveis, para o 
caso de o time achar que a feature the cifrar senhas do Spring Boot nao ser suficiente
* Uma ferramenta para gerenciar autenticacao, como Auth0, Okta, Azure AD etc.

Ao final deste README ha uma sessao com sugestoes de melhorias para a aplicacao.


### Requisitos
* Java 11
* Git


### Como testar
`./gradlew clean test`

### Como rodar
`./gradlew clean bootRun`

### Como usar Swagger
`http://localhost:8080/swagger-ui/`

### Como a aplicacao esta estruturada
Este app usa spring boot 2, com Java 11 e gradle.
Tambem utiliza Spotless para manter a indentacao e Jacoco para verificar a cobertura de testes.

Ele foi criado em padrao Clean Architecture, com gateways (entradas e saidas), model e use cases.

Os gateways sao os controllers, repositorios e todas as outras conexoes externas.
Os usecases sao onde as regras de negocios estao, para fugir daquele formato de UserService,
que na verdade so funciona bem para CRUDs muito simpes.

O codigo esta todo em ingles, mas deixei os comentarios em portugues como uma tentativa
de agilizar o review.

O Test Coverage esta concentrado nos usecases (com excecao do MovieLoader, 
que e opcional para a aplicacao), onde esta em 100%, de acordo com Jacoco Report,
no entanto tambem ha testes para os controllers e repositorios. No total sao 52 testes.
![Alt text](documentation/jacocoReport.png "Jacoco Report for use cases")

### Como jogar

#### Logue o usuario

```
curl --location --request POST 'http://localhost:8080/oauth/token' \
    --header 'Authorization: Basic bXluYW1lOnNlY3JldDEyMw==' \
    --header 'Content-Type: application/x-www-form-urlencoded' \
    --data-urlencode 'username=bruce.wayne@gmail.com' \
    --data-urlencode 'password=I am Batman' \
    --data-urlencode 'grant_type=password'
```

#### Inicie uma partida para pegar a sessao

```
curl --location --request POST 'http://localhost:8080/api/movie-battle/sessions' \
--header 'Authorization: Bearer TOKEN'
```

#### Pegue um par de filmes 
Apesar de ser uma especie de getMovies, optei por usar POST http method por 2 motivos:
1) Como o resultado nao e o mesmo, nao se pode chamar este endpoint the idempotente
2) Ao chamar o par de filmes o usuario esta atrelando a sessao do jogo
e o par de filmes a sessao dele. E dados sao salvos em banco neste processo.

```
curl --location --request POST 'http://localhost:8080/api/movie-battle/movies' \
    --header 'Authorization: Bearer TOKEN' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "sessionId" : "5fb2f25f-05aa-42c3-b4bc-dd56f7993528"
    }'
```

#### Responda qual filme tem um score maior
```
curl --location --request POST 'http://localhost:8080/api/movie-battle/movies/reply' \
    --header 'Authorization: Bearer TOKEN' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "sessionId":"5fb2f25f-05aa-42c3-b4bc-dd56f7993528",
        "answer":"tt0469611"
    }'
```

#### Chame outras vezes um par de filmes
Chame outras vezes getMovies e responda para acumular pontos.
Se cometer 3 erros a sessao morre.


#### Encerre a sessao
Se o jogador quiser encerrar a sessao antes de ser eliminado pelos 3 errors:

```
curl --location --request DELETE 'http://localhost:8080/api/movie-battle/sessions/1f9ffc73-cc3e-4708-9196-f28943c10af9' \
--header 'Authorization: Bearer TOKEN'

```

#### Veja seu nome no Hall da Fama

Este endpoint mostra os Top 10 players e sua pontuacao absoluta (pontos - numero de erros)

```
curl --location --request GET 'http://localhost:8080/api/movie-battle/players' \
--header 'Authorization: Bearer TOKEN'

```

### Carregar filmes (Opcional)
Endpoint criado para carregar filmes da OMDB Api e salvar no banco de dados.
(198 foram incluidos em data.sql, portanto nao e necessario carregar os filmes para comecar)
Para executar este endpoint o usuario precisa ser ADMIN

```
curl --location --request POST 'http://localhost:8080/api/load-movies?keywords=Super' \
--header 'Authorization: Bearer TOKEN'
```

### Futuras mudancas / Sugestoes de melhorias
* Criar Spring Cloud, com gateway, Discovery Server e Config Server

* Separar pacote security para um microservice de login, que esta praticamente plugNplay.
So copiar o pacote inteiro + application.yml para outro projeto que ja funciona.

* Mover as permissoes de rotas e para o Gateway

* Cobrir MovieLoader com testes ou remover da aplicacao