// Simple WebSocket OrderBook Monitor
// Connects to ws://localhost:8080/ws/orderbook and logs updates to console

const OrderBookMonitor = {
	ws: null,
	isConnected: false,

	init() {
		this.connect();
	},

	connect() {
		const wsUrl = `ws://localhost:8080/ws/orderbook`;
		console.log('Connecting to WebSocket:', wsUrl);

		try {
			this.ws = new WebSocket(wsUrl);

			this.ws.onopen = () => {
				this.isConnected = true;
				console.log('✓ WebSocket connected!');
				console.log('Listening for OrderBook updates...');
			};

			this.ws.onmessage = (event) => {
				try {
					const orderBook = JSON.parse(event.data);
					console.log('📊 OrderBook Update:', orderBook);
				} catch (e) {
					console.error('Error parsing message:', e);
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
};

// Auto-initialize when DOM is ready
if (document.readyState === 'loading') {
	document.addEventListener('DOMContentLoaded', () => {
		OrderBookMonitor.init();
	});
} else {
	OrderBookMonitor.init();
}
