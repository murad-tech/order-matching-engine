function handleOrderSubmit(event) {
	event.preventDefault();

	// Disable buttons and show loading state
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

	// Show loading message
	formMessage.className = 'form-message loading';
	formMessage.textContent = 'Submitting order...';

	// Make POST request to backend API
	fetch(`${CONFIG.API_BASE_URL}/orders`, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify(order),
	})
		.then((response) => response.json())
		.then((data) => {
			console.log('Order response:', data);
			formMessage.className = 'form-message success';
			formMessage.textContent = `Order submitted successfully! ${data.symbol}[${data.type}] @${data.price}: ${data.quantity}`;
		})
		.catch((error) => {
			console.error('Error submitting order:', error);
			formMessage.className = 'form-message error';
			formMessage.textContent = `Error: ${error.message}`;
		})
		.finally(() => {
			// Re-enable both buttons
			btnBuy.disabled = false;
			btnSell.disabled = false;
			btnBuy.innerHTML = '<span>BUY</span>';
			btnSell.innerHTML = '<span>SELL</span>';

			// Clear message after 1 second
			setTimeout(() => {
				formMessage.className = '';
				formMessage.textContent = '';
			}, 2000);
		});
}
