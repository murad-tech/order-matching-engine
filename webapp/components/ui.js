const UI = {
	renderOrders() {
		// Filter: Only show PENDING and PARTIALLY_FILLED orders
		const allOrders = Array.from(STATE.orders.values()).filter((order) => {
			const isActive = order.status === 'PENDING' || order.status === 'PARTIALLY_FILLED';
			return isActive;
		});

		// Separate into bids (BUY) and asks (SELL)
		const bids = allOrders.filter((order) => order.type === 'BUY');
		const asks = allOrders.filter((order) => order.type === 'SELL');

		// Sort bids by price descending (highest first), then by timestamp
		bids.sort((a, b) => {
			if (b.price !== a.price) return b.price - a.price;
			return new Date(b.timestamp) - new Date(a.timestamp);
		});

		// Sort asks by price ascending (lowest first), then by timestamp
		asks.sort((a, b) => {
			if (a.price !== b.price) return a.price - b.price;
			return new Date(b.timestamp) - new Date(a.timestamp);
		});

		// Update best bid and best ask prices
		this.updateBestPrices(bids, asks);

		// Render bid orders
		const bidContainer = document.getElementById('bidOrdersList');
		if (bids.length === 0) {
			bidContainer.innerHTML = '<div class="empty-state"><p>No buy orders</p></div>';
		} else {
			bidContainer.innerHTML = bids
				.slice(0, CONFIG.MAX_ORDERS_DISPLAY)
				.map((order) => OrderItemTemplate(order))
				.join('');
		}

		// Render ask orders
		const askContainer = document.getElementById('askOrdersList');
		if (asks.length === 0) {
			askContainer.innerHTML = '<div class="empty-state"><p>No sell orders</p></div>';
		} else {
			askContainer.innerHTML = asks
				.slice(0, CONFIG.MAX_ORDERS_DISPLAY)
				.map((order) => OrderItemTemplate(order))
				.join('');
		}
	},

	updateBestPrices(bids, asks) {
		const bestBidPriceEl = document.getElementById('bestBidPrice');
		const bestAskPriceEl = document.getElementById('bestAskPrice');

		if (bids.length > 0) {
			bestBidPriceEl.textContent = `$${parseFloat(bids[0].price).toFixed(2)}`;
		} else {
			bestBidPriceEl.textContent = '-';
		}

		if (asks.length > 0) {
			bestAskPriceEl.textContent = `$${parseFloat(asks[0].price).toFixed(2)}`;
		} else {
			bestAskPriceEl.textContent = '-';
		}

		// Update badge counts
		document.getElementById('bidsCount').textContent = bids.length;
		document.getElementById('asksCount').textContent = asks.length;
	},
};
