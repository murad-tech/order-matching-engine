const API = {
	async submitOrder(orderData) {
		const response = await fetch(`${CONFIG.API_BASE_URL}/orders`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(orderData),
		});

		if (!response.ok) {
			const error = await response.json();
			throw new Error(error.message || 'Failed to submit order');
		}

		return await response.json();
	},

	async cancelOrder(orderId) {
		const response = await fetch(`${CONFIG.API_BASE_URL}/orders/${orderId}`, {
			method: 'DELETE',
		});

		return response.ok;
	},

	async getOrder(orderId) {
		const response = await fetch(`${CONFIG.API_BASE_URL}/orders/${orderId}`);

		if (!response.ok) {
			return null;
		}

		return await response.json();
	},
};
