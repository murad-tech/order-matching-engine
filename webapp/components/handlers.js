function handleOrderSubmit(event) {
	event.preventDefault();

	const btnBuy = document.getElementById('btnBuy');
	const btnSell = document.getElementById('btnSell');
	btnBuy.disabled = true;
	btnSell.disabled = true;
	btnBuy.innerHTML = '<span>...</span>';
	btnSell.innerHTML = '<span>...</span>';

	const form = event.target;
	const formData = new FormData(form);
	const formMessage = document.getElementById('formMessage');
	const order = {
		symbol: formData.get('symbol'),
		quantity: parseInt(formData.get('quantity'), 10),
		price: parseFloat(formData.get('price')),
		type: formData.get('orderType'),
	};

	formMessage.className = 'form-message loading';
	formMessage.textContent = 'Submitting order...';

	fetch(`${CONFIG.API_BASE_URL}/orders`, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify(order),
	})
		.then((response) => response.json())
		.then((data) => {
			formMessage.className = 'form-message success';
			formMessage.textContent = `Order submitted successfully! ${data.symbol}[${data.type}] @${data.price}: ${data.quantity}`;
		})
		.catch((error) => {
			console.error('Error submitting order:', error);
			formMessage.className = 'form-message error';
			formMessage.textContent = `Error: ${error.message}`;
		})
		.finally(() => {
			btnBuy.disabled = false;
			btnSell.disabled = false;
			btnBuy.innerHTML = '<span>BUY</span>';
			btnSell.innerHTML = '<span>SELL</span>';

			setTimeout(() => {
				formMessage.className = '';
				formMessage.textContent = '';
			}, 2000);
		});
}

function handleCancelOrder(orderId) {
	const formMessage = document.getElementById('formMessage');

	formMessage.className = 'form-message loading';
	formMessage.textContent = 'Cancelling order...';

	API.cancelOrder(orderId)
		.then((response) => {
			if (response.ok) {
				formMessage.className = 'form-message success';
				formMessage.textContent = `Order ${orderId} cancelled successfully!`;

				const orderElement = document.querySelector(`[data-order-id="${orderId}"]`);
				if (orderElement) {
					orderElement.remove();
				}

				UI.renderOrders();
			} else if (response.status === 404) {
				formMessage.className = 'form-message error';
				formMessage.textContent = 'Order not found or already cancelled';
			} else {
				formMessage.className = 'form-message error';
				formMessage.textContent = 'Failed to cancel order';
			}
		})
		.catch((error) => {
			console.error('Error cancelling order:', error);
			formMessage.className = 'form-message error';
			formMessage.textContent = `Error: ${error.message}`;
		})
		.finally(() => {
			setTimeout(() => {
				formMessage.className = '';
				formMessage.textContent = '';
			}, 2000);
		});
}

function handleSymbolChange(event) {
	const symbol = event.target.value.trim();
	STATE.orderBook = { bids: [], asks: [] };
	STATE.orders.clear();
	UI.renderOrders();

	if (!symbol) return;

	const formMessage = document.getElementById('formMessage');
	formMessage.className = 'form-message loading';
	formMessage.textContent = `Fetching orders for ${symbol}...`;

	API.getOrderBook(symbol)
		.then((response) => {
			if (response.ok) {
				formMessage.className = 'form-message success';
				formMessage.textContent = `Orders for ${symbol} loaded successfully!`;
			} else {
				formMessage.className = 'form-message error';
				formMessage.textContent = `No orders found for ${symbol}`;
			}
		})
		.catch((error) => {
			console.error('Error fetching orders:', error);
			formMessage.className = 'form-message error';
			formMessage.textContent = `Error: ${error.message}`;
		})
		.finally(() => {
			setTimeout(() => {
				formMessage.className = '';
				formMessage.textContent = '';
			}, 2000);
		});
}
