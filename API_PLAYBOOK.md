# NetMasters — Playbook de pruebas (Postman & Vue snippets)

Este documento contiene colecciones de petición, ejemplos curl y snippets mínimos para un cliente en Vue para probar los endpoints principales: autenticar, crear matches, jugar (Triqui / Connect4), y consultar rankings. Asume la API corriendo en http://localhost:8080.

----

## Autenticación

- Registro (POST /auth/register)
  - Body JSON:
    {
      "username": "alice",
      "email": "alice@example.com",
      "password": "secret"
    }

- Login (POST /auth/login)
  - Body JSON:
    {
      "email": "alice@example.com",
      "password": "secret"
    }
  - Respuesta esperada: `{ "token": "eyJ...", "username": "alice", "email": "alice@example.com" }`

Guarda el token y envíalo en las siguientes peticiones con el header:
Authorization: Bearer <token>

Ejemplo curl (login):

```powershell
curl -X POST "http://localhost:8080/auth/login" -H "Content-Type: application/json" -d '{"email":"alice@example.com","password":"secret"}'
```

----

## Matches

- Crear match (POST /api/matches)
  - Body JSON (`CreateMatchDTO`):
    {
      "gameId": 1,    // 1 = Triqui (o según tu catálogo)
      "player1Id": 1,
      "player2Id": 2
    }
  - Retorna: `MatchDTO` con id, players y estado (201)

- Obtener match (GET /api/matches/{id})

- Cambiar estado (PUT /api/matches/{id}/status?status=FINISHED)

- Finalizar match (POST /api/matches/{id}/finish?winnerId=1)

Ejemplo curl (crear match):

```powershell
curl -X POST "http://localhost:8080/api/matches" -H "Content-Type: application/json" -H "Authorization: Bearer <token>" -d '{"gameId":1,"player1Id":1,"player2Id":2}'
```

----

## Triqui (tres en raya)

- Obtener tablero: GET /api/triqui/{matchId}/board
  - Respuesta: `BoardDTO` con `board` (array int[9]) y `currentTurn` (playerId) y `winner` si existe.

- Enviar movimiento: POST /api/triqui/{matchId}/move
  - Body `MoveDTO`:
    {
      "playerId": 1, // (temporal) se espera extraer del token en futuro
      "position": 4   // posición 0..8
    }

Ejemplo curl:

```powershell
curl -X POST "http://localhost:8080/api/triqui/10/move" -H "Content-Type: application/json" -H "Authorization: Bearer <token>" -d '{"playerId":1,"position":4}'
```

----

## Connect Four

Endpoints expuestos en `ConnectFourController`:
- GET /api/connect4/{matchId}/board  (todavía por adaptar en la implementación)
- POST /api/connect4/{matchId}/move  (todavía por adaptar)

Nota: si los endpoints devuelven 404 es porque en el código aparecen TODOs; para pruebas E2E puedes invocar directamente el servicio que usa los repositorios o usar los tests de ejemplo ya incluidos.

----

## Rankings

- Obtener ranking global: GET /api/rankings
- Obtener ranking de un jugador: GET /api/rankings/player/{playerId}
- Añadir puntos a jugador: POST /api/rankings/player/{playerId}/add?points=5 (requiere autorización si tu configuración lo define)
- Top N: GET /api/rankings/top?limit=10
- Ranking por tipo de juego: GET /api/rankings/game/{gameType}
- Reset rankings (POST /api/rankings/reset)
- Calcular ranking desde match (POST /api/rankings/calculate/{matchId})

Ejemplo curl (obtener ranking global):

```powershell
curl -X GET "http://localhost:8080/api/rankings"
```

----

## WebSocket / STOMP (Tiempo real)

- Endpoint STOMP: `ws://localhost:8080/ws` (SockJS habilitado)
- Prefijo de aplicación: `/app`
- Broker simple: envía a topics bajo `/topic`
- Seguridad handshake: debes incluir header `Authorization: Bearer <token>` cuando abras la conexión (JwtHandshakeInterceptor valida el token)

Topics útiles (ejemplos que usamos desde el server):
- `/topic/match/{matchId}/started` — enviado cuando se inicia el juego (contiene estado inicial del tablero y currentTurn)
- `/topic/match/{matchId}/move` — (si está implementado) notifica movimientos

Ejemplo mínimo de cliente JS (SockJS + Stomp) — uso en Vue:

```js
import SockJS from 'sockjs-client';
import { Client as StompClient } from '@stomp/stompjs';

function connectToMatch(matchId, token, onStarted, onMove) {
  const socket = new SockJS('http://localhost:8080/ws');
  const client = new StompClient({
    webSocketFactory: () => socket,
    connectHeaders: { Authorization: 'Bearer ' + token },
    onConnect: () => {
      client.subscribe(`/topic/match/${matchId}/started`, (msg) => {
        const payload = JSON.parse(msg.body);
        onStarted(payload);
      });
      client.subscribe(`/topic/match/${matchId}/move`, (msg) => {
        const payload = JSON.parse(msg.body);
        onMove(payload);
      });
    },
  });
  client.activate();
  return client;
}
```

En Vue podrías usarlo en un composable o directamente en un componente `mounted()`.

----

## Snippets Vue (componente simple)

Ejemplo de componente SFC (mínimo) que hace login, crea un match, conecta STOMP y muestra tablero Triqui:

```html
<template>
  <div>
    <button @click="login">Login</button>
    <button @click="createMatch">Crear Match</button>
    <div v-if="board">
      <div v-for="(cell, idx) in board" :key="idx">{{ cell }}</div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { connectToMatch } from './stompClient'; // usar la función del ejemplo anterior

export default {
  data() {
    return { token: null, matchId: null, board: null };
  },
  methods: {
    async login() {
      const res = await axios.post('/auth/login', { email: 'alice@example.com', password: 'secret' });
      this.token = res.data.token;
    },
    async createMatch() {
      const res = await axios.post('/api/matches', { gameId: 1, player1Id: 1, player2Id: 2 }, { headers: { Authorization: 'Bearer ' + this.token } });
      this.matchId = res.data.id;
      // conectar STOMP
      this.client = connectToMatch(this.matchId, this.token, (payload) => {
        console.log('Game started', payload);
        this.board = payload.board;
      }, (move) => {
        console.log('Move received', move);
      });
    }
  }
}
</script>
```

----

## Notas de seguridad y pruebas

- Los endpoints REST usan JWT. Pasa siempre el header `Authorization: Bearer <token>` en peticiones que lo requieran.
- El handshake STOMP requiere el mismo header; el cliente JS lo envía en `connectHeaders`.
- Las cargas de movimiento se guardan cifradas en la base de datos; para inspeccionarlas usa la CLI `MoveDecryptRunner` o pide un endpoint admin seguro.

----

Si quieres, genero automáticamente una colección Postman exportable con variables de entorno (`baseUrl`, `token`, `matchId`) y ejemplos más completos (incluyendo body schema). ¿Lo genero ahora y lo añado al repo? 
