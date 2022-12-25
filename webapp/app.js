const CONFIG = {
	API_BASE_URL: 'http://localhost:8080/api/v1', // TODO: Move to environment variable file
	TIMEOUT_MS: 5000,
	MAX_ORDERS_DISPLAY: 10,
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

	UI.renderOrders();
}

// Initialize when DOM is ready
if (document.readyState === 'loading') {
	document.addEventListener('DOMContentLoaded', init);
} else {
	init();
}
