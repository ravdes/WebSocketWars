# WebSocketWars 

## Application Flow and Utilities

WebSocketWars is an online platform allowing users to create their own accounts on the website. Here's how it works:

### Account Creation and Verification

- **User Registration**: User creates an account and need to confirm it through a verification link sent to their stated email.
- **Email Verification**: Once the verification link is clicked, the user is enabled and can sign in.
- **Bearer Token**: After successful login, a bearer token (valid for 24 hours) is assigned. This token is mandatory for performing further requests. Without it, users won't be authenticated, and all requests will be rejected.

### Post-Login

After logging in, users are redirected to the `/account` page where the following options are available:

- **Enable Two-Factor Auth**: Once configured, user will need to insert a code from Google Authenticator with every login attempt.
- **Create TicTacToe Game**
- **Join Existing TicTacToe Game**
- **Logout**: While logging out, the user's bearer token is invalidated by calling `/blacklistToken`.

### Game Creation and Joining

- **Creating a Game**: Users can create a game and wait for another player.
- **Joining a Game**: The second player can be another registered user or a guest.
    - **Guest Players**: Guests can join without creating an account. Once the game is finished, the guest's account is deleted to avoid blocking the nickname for potential users willing to sign up.
- **Game starts once second user joins**
### Real-Time Gameplay

The TicTacToe gameplay implements a websocket connection to ensure there are no delays in data inflow between users. Everything is updated on the page dynamically and in real-time. Additionally, players can chat with each other which is also achieved through websocket messaging.

## Demo

### Account creation

https://github.com/ravdes/TicTacToe/assets/107648518/ed190e1c-b0d7-4759-88df-2e33d73a9888

### User vs User Gameplay

https://github.com/ravdes/TicTacToe/assets/107648518/b36b7ec0-2d57-486a-a6c3-a17613db9649

### User vs Guest Gameplay

https://github.com/ravdes/TicTacToe/assets/107648518/3c7ddcab-79bd-40b2-95be-9c06f2ae6f99

# Technologies used
- **Java 17**
- **Spring Boot**
- **Junit5**
- **Mockito**
- **Postgresql**
- **Docker**
- **Git**
- **Maven**
- **React**
- **Next.js**

# How to run?

All layers of application (database, backend and frontend) are packaged into Docker containers so application can be just run directly with one command which is docker-compose up

1. Install Docker Desktop and open it  - [download](https://www.docker.com/products/docker-desktop/)


2. Clone this repository

```bash
git clone https://github.com/ravdes/TicTacToe.git
```

3. Run in your terminal

````yaml
docker-compose up
````
4. Application is set up go to localhost:3000

