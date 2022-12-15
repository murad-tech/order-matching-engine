function OrderItemTemplate(order) {
	const statusClass = order.status.toLowerCase().replace('_', '-');
	const statusText = order.status.replace('_', ' ');
	return `
			<div class="order-item" data-order-id="${order.orderId}">
					<div class="order-row">
							<span class="order-symbol">${order.symbol}</span>
							<span class="order-price">$${parseFloat(order.price).toFixed(2)}</span>
							<span class="order-qty">${parseFloat(order.remainingQuantity).toFixed(0)}</span>
							<span class="order-status ${statusClass}">${statusText}</span>
							<button class="btn-cancel-compact" onclick="handleCancelOrder('${order.orderId}')">×</button>
					</div>
			</div>
	`;
}
