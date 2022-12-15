const CONFIG = {
	API_BASE_URL: 'http://localhost:8080/api/v1', // TODO: Move to environment variable file
	TIMEOUT_MS: 5000,
};
const STATE = {
	orders: new Map(),
	trades: [],
	orderBook: {
		bids: [],
		asks: [],
	},
};

function init() {
	console.log('=== INITIALIZING ORDER MATCHING ENGINE UI ===');

	// Button click handlers
	const btnBuy = document.getElementById('btnBuy');
	const btnSell = document.getElementById('btnSell');
	const orderTypeInput = document.getElementById('orderType');

	btnBuy.addEventListener('click', (e) => {
		e.preventDefault();
		orderTypeInput.value = 'BUY';
		document.getElementById('orderForm').dispatchEvent(new Event('submit'));
	});

	btnSell.addEventListener('click', (e) => {
		e.preventDefault();
		orderTypeInput.value = 'SELL';
		document.getElementById('orderForm').dispatchEvent(new Event('submit'));
	});

	document.getElementById('orderForm').addEventListener('submit', handleOrderSubmit);

	console.log('UI initialized successfully!');

	STATE.orders.set('1', {
		orderId: '1',
		symbol: 'AAPL',
		type: 'BUY',
		price: 150.0,
		quantity: 10,
		remainingQuantity: 10,
		status: 'PENDING',
		timestamp: new Date().toISOString(),
	});
	STATE.orders.set('2', {
		orderId: '2',
		symbol: 'AAPL',
		type: 'SELL',
		price: 155.0,
		quantity: 5,
		remainingQuantity: 5,
		status: 'PARTIALLY_FILLED',
		timestamp: new Date().toISOString(),
	});

	UI.renderOrders();
}

// Initialize when DOM is ready
if (document.readyState === 'loading') {
	document.addEventListener('DOMContentLoaded', init);
} else {
	init();
}
