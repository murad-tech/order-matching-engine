const OrderBookMonitor = {
	ws: null,
	isConnected: false,

	init() {
		this.connect();
	},

	connect() {
		const wsUrl = `ws://localhost:8080/ws/orderbook`;
		const symbol = document.getElementById('symbol').value;

		try {
			this.ws = new WebSocket(wsUrl);

			this.ws.onopen = () => {
				this.isConnected = true;
				console.log('✓ WebSocket connected!');
				API.getOrderBook(symbol);
			};

			this.ws.onmessage = (event) => {
				try {
					const orderBook = JSON.parse(event.data);
					this.processOrderBook(orderBook);
				} catch (error) {
					console.error('Failed to parse OrderBook message:', error);
				}
			};

			this.ws.onerror = (error) => {
				console.error('❌ WebSocket error:', error);
			};

			this.ws.onclose = () => {
				this.isConnected = false;
				console.log('⊘ WebSocket disconnected');
			};
		} catch (error) {
			console.error('Failed to create WebSocket:', error);
		}
	},

	disconnect() {
		if (this.ws) {
			this.ws.close();
		}
	},

	processOrderBook(orderBook) {
		// Clear existing orders and repopulate from orderBook
		STATE.orders.clear();
		STATE.orderBook = orderBook;

		// Process bids (BUY orders)
		if (orderBook.buyOrders && Array.isArray(orderBook.buyOrders)) {
			orderBook.buyOrders.forEach((bid) => {
				STATE.orders.set(bid.orderId, {
					orderId: bid.orderId,
					symbol: bid.symbol || 'UNKNOWN',
					type: 'BUY',
					price: parseFloat(bid.price),
					remainingQuantity: parseFloat(bid.remainingQuantity),
					status: bid.status || 'PENDING',
					timestamp: new Date().toISOString(),
				});
			});
		}

		// Process asks (SELL orders)
		if (orderBook.sellOrders && Array.isArray(orderBook.sellOrders)) {
			orderBook.sellOrders.forEach((ask, index) => {
				STATE.orders.set(ask.orderId, {
					orderId: ask.orderId,
					symbol: ask.symbol || 'UNKNOWN',
					type: 'SELL',
					price: parseFloat(ask.price),
					remainingQuantity: parseFloat(ask.remainingQuantity),
					status: ask.status || 'PENDING',
					timestamp: new Date().toISOString(),
				});
			});
		}

		// Render the updated orders on UI
		if (typeof UI !== 'undefined' && UI.renderOrders) {
			UI.renderOrders();
		}
	},
};

// Auto-initialize when DOM is ready
if (document.readyState === 'loading') {
	document.addEventListener('DOMContentLoaded', () => {
		OrderBookMonitor.init();
	});
} else {
	OrderBookMonitor.init();
}
