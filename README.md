# Order Matching Engine (MVP)

A real-time order matching engine with a classic/retro web interface. This MVP implementation matches buy and sell orders for stocks/currencies using price-time priority matching algorithm with WebSocket-based real-time updates.

## Overview

The Order Matching Engine is a distributed system consisting of:

- **Backend**: Spring Boot REST API with WebSocket support
- **Matching Engine**: Core matching logic (Java module in memory storage)
- **Frontend**: Interactive web UI with real-time order book display
- **Data Models**: Shared order and trade data structures

## Features

- **Order Submission**: Submit buy/sell orders via REST API
- **Real-time Matching**: Automatic order matching using price-time priority
- **Order Cancellation**: Cancel pending or partially filled orders
- **WebSocket Updates**: Real-time order book broadcasts to all connected clients
- **Trade Execution**: Automatic trade generation on order matches
- **Order Tracking**: Query individual order status
- **Interactive UI**: Live order book display with bids/asks separated

## Technology Stack

### Backend

- **Framework**: Spring Boot 3.x
- **Language**: Java 17+
- **Build Tool**: Maven
- **Communication**: REST API + WebSocket

### Frontend

- **HTML5** / **CSS3** / **Vanilla JavaScript** (ES6+)
- **WebSocket API** for real-time communication
- **Responsive Design** with CSS Grid/Flexbox

## Project Structure

```
order-matching-engine/
├── matching-engine/           # Core matching algorithm module
│   ├── src/main/java/
│   │   └── com/muradtek/matching/
│   │       ├── engine/        # MatchingEngineService, order matching logic
│   │       └── models/        # Order, Trade, OrderBook data models
│   └── pom.xml
│
├── order-service/             # Spring Boot REST API
│   ├── src/main/java/
│   │   └── com/muradtek/orderservice/
│   │       ├── controllers/   # OrderController (REST endpoints)
│   │       ├── dto/           # Request/Response DTOs
│   │       ├── mappers/       # DTO to Model mappers
│   │       ├── websocket/     # WebSocket handlers & broadcasters
│   │       └── config/        # Spring configuration
│   ├── resources/
│   │   └── application.properties
│   └── pom.xml
│
├── webapp/                    # Frontend web application
│   ├── index.html            # Main page
│   ├── app.js                # App initialization & state management
│   ├── styles.css            # Global styles
│   ├── Dockerfile            # Frontend container image
│   ├── nginx.conf            # Nginx server configuration
│   ├── components/
│   │   ├── orderbook-monitor.js  # WebSocket client
│   │   ├── order-item.html.js    # Order template component
│   │   ├── ui.js                 # UI rendering logic
│   │   ├── api.js                # API client functions
│   │   └── handlers.js           # Event handlers
│   └── styles/
│       ├── variables.css
│       ├── order-form.css
│       └── orders-section.css
│
├── Dockerfile                 # Backend container image
├── docker-compose.yaml        # Docker Compose orchestration
├── pom.xml                    # Root project pom.xml
```

## API Endpoints

### Orders

| Method | Endpoint                   | Description             |
| ------ | -------------------------- | ----------------------- |
| POST   | `/api/v1/orders`           | Submit a new order      |
| GET    | `/api/v1/orders/{orderId}` | Get order details by ID |
| DELETE | `/api/v1/orders/{orderId}` | Cancel an order         |

### Order Book

| Method | Endpoint                     | Description                                                 |
| ------ | ---------------------------- | ----------------------------------------------------------- |
| GET    | `/api/v1/orderbook/{symbol}` | Request order book snapshot and trigger WebSocket broadcast |

## WebSocket Endpoints

| Path                               | Purpose                                      |
| ---------------------------------- | -------------------------------------------- |
| `ws://localhost:8080/ws/orderbook` | Real-time order book updates (bids and asks) |

## Order Models

### Order

```json
{
  "orderId": "string",
  "symbol": "AAPL",
  "type": "BUY" | "SELL",
  "price": 150.50,
  "quantity": 100,
  "remainingQuantity": 75,
  "status": "PENDING" | "PARTIALLY_FILLED" | "FILLED" | "CANCELLED",
  "timestamp": "1234567890"
}
```

### Trade

```json
{
	"tradeId": "string",
	"buyOrderId": "string",
	"sellOrderId": "string",
	"symbol": "AAPL",
	"price": 150.5,
	"quantity": 50,
	"timestamp": "1234567890"
}
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Building the Project

```bash
# From the root directory
mvn clean package
```

### Running with Docker Compose (Recommended)

The project includes Docker configurations for both backend and frontend services.

```bash
# From the root directory, build and start all services
docker-compose up --build

# Services will be available at:
# - Backend API: http://localhost:8080
# - Frontend: http://localhost:80
# - WebSocket: ws://localhost:8080/ws/orderbook
```

### Running the Application Locally

```bash
# Start the Spring Boot server
cd order-service
mvn spring-boot:run

# The API will be available at http://localhost:8080
# The frontend is served at http://localhost:8080/webapp (if configured)
```

### Accessing the Web UI

**Using Docker Compose:**

1. Open `http://localhost:5500` in your browser (frontend running on Nginx)
2. WebSocket will auto-connect to `ws://localhost:8080/ws/orderbook` (via backend)
3. Submit orders using the form at the bottom
4. View real-time bid/ask updates

**Running Locally:**

1. Open `webapp/index.html` in your browser or serve via HTTP
2. WebSocket will auto-connect to `ws://localhost:8080/ws/orderbook`
3. Submit orders using the form at the bottom
4. View real-time bid/ask updates

## Matching Algorithm

The engine uses a **Price-Time Priority** matching algorithm:

1. **Orders are matched by price first**
   - Buy orders matched at highest bids
   - Sell orders matched at lowest asks
2. **Within the same price level, time priority applies**
   - Earlier orders matched before later orders (FIFO)
3. **Partial fills are supported**
   - Orders can be partially matched and remain pending
4. **Immediate broadcast**
   - Order book is broadcast to all WebSocket clients after each transaction

## Docker Configuration

### Backend (Dockerfile)

- Builds a Spring Boot JAR container
- Exposes port `8080` for REST API and WebSocket
- Includes health checks for automatic restart
- Environment: Production profile enabled

### Frontend (webapp/Dockerfile)

- Uses lightweight Nginx Alpine image
- Serves static files (HTML, CSS, JavaScript) efficiently
- Exposes port `80` for HTTP traffic
- Configured with Nginx (`nginx.conf`) for SPA routing

### Docker Compose (docker-compose.yaml)

- Orchestrates backend and frontend services
- Establishes internal bridge network for service communication
- Frontend depends on backend being healthy before starting
- Service names available for internal DNS:
  - `backend`: http://localhost:8080
  - `frontend`: http://localhost:5500

## State Management

The frontend maintains a `STATE` object that contains:

- `orders`: Map of all active orders (keyed by orderId)
- `orderBook`: Current bid/ask levels from WebSocket
- `trades`: Historical trade executions (for future enhancements)

## Future Enhancements

- [ ] Persistent storage (Database integration)
- [ ] Authentication & authorization
- [ ] Multiple symbols support
- [ ] Order types (Market, Stop, etc.)
- [ ] Advanced charting and analytics
- [ ] Trade history and reporting
- [ ] Order validation rules
- [ ] Rate limiting and throttling
