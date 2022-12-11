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
	const order = {
		symbol: formData.get('symbol'),
		quantity: parseInt(formData.get('quantity'), 10),
		price: parseFloat(formData.get('price')),
		type: formData.get('orderType'),
	};

	setTimeout(() => {
		// Simulate API call delay
		console.log('Order submitted:', order);
		// CHANGED: Re-enable both buttons
		btnBuy.disabled = false;
		btnSell.disabled = false;
		btnBuy.innerHTML = '<span>BUY</span>';
		btnSell.innerHTML = '<span>SELL</span>';
	}, 1000);
}
