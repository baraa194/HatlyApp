import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {Router , RouterLink, RouterModule } from '@angular/router'; 
import { CartService, CartItem } from '../../services/cart.service';
import { OrderService, OrderRequest } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';
import { AddressService } from '../../services/address.service'; 
import { PaymentService } from '../../services/payment.service'; 

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule,RouterLink],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss']
})
export class CartComponent implements OnInit {
  cartItems: CartItem[] = [];
  subtotal: number = 0;
  deliveryFee: number = 0;
  serviceFee: number = 0;
  totalPrice: number = 0;
  orderId!: number;
  
  private _selectedPaymentMethod: 'CASH' | 'STRIPE' = 'CASH';

  get selectedPaymentMethod(): 'CASH' | 'STRIPE' {
    return this._selectedPaymentMethod;
  }
  set selectedPaymentMethod(value: 'CASH' | 'STRIPE') {
    this._selectedPaymentMethod = value;
    if (this.cartItems.length > 0) {
      this.loadOrderFees();
    }
  }

  constructor(
    private cartService: CartService,
    private orderService: OrderService,
    private authService: AuthService,
    private addressService: AddressService,
    private paymentService: PaymentService ,
    private router:Router
  ) {}

  ngOnInit(): void {
    this.cartService.cart$.subscribe(items => {
      this.cartItems = items;
      if (this.cartItems.length > 0) {
        this.loadOrderFees(); 
      } else {
        this.subtotal = 0;
        this.deliveryFee = 0;
        this.serviceFee = 0;
        this.totalPrice = 0;
      }
    });
  }

  loadOrderFees(): void {
    const currentUserId = this.authService.getCurrentUserId(); 
    if (!currentUserId) return;

    const savedBranchJson = localStorage.getItem('nearest_branch');
    if (!savedBranchJson) return;
    const branchData = JSON.parse(savedBranchJson);

    this.addressService.getDefaultAddress(currentUserId).subscribe({
      next: (currentAddress) => {
        if (!currentAddress || !currentAddress.id) return;

        const formattedItems = this.cartService.getFormattedOrderItems();

        const orderPayload: OrderRequest = {
          restaurant_id: branchData.restaurantId, 
          restaurnat_branch_id: branchData.id,    
          customerAddress_id: currentAddress.id,
          deliveryLat: currentAddress.lat,   
          deliveryLng: currentAddress.lng,  
          paymentProviderName: this.selectedPaymentMethod,
          items: formattedItems
        };

        this.orderService.calculateFees(orderPayload, currentUserId).subscribe({
          next: (fees) => {
            this.subtotal = fees.subtotal;
            this.deliveryFee = fees.deliveryFee;
            this.serviceFee = fees.serviceFee;
            this.totalPrice = fees.totalPrice; 
          },
          error: (err) => console.error('Error calculating fees:', err)
        });
      },
      error: (err) => console.error('Failed to get address for fees calculation:', err)
    });
  }

  checkoutOrder(): void {
    if (this.cartItems.length === 0) {
      alert('Your cart is empty!');
      return;
    }
 
    const currentUserId = this.authService.getCurrentUserId(); 
    if (!currentUserId) {
      alert('Please log in to place an order! 🔐');
      return;
    }

    const savedBranchJson = localStorage.getItem('nearest_branch');
    if (!savedBranchJson) {
      alert('Branch data is missing! Please re-select the restaurant.');
      return;
    }
    const branchData = JSON.parse(savedBranchJson);

    this.addressService.getDefaultAddress(currentUserId).subscribe({
      next: (currentAddress) => {
        if (!currentAddress || !currentAddress.id) {
          alert('Please select a valid delivery address first! 📍');
          return;
        }

        const formattedItems = this.cartService.getFormattedOrderItems();

        const orderPayload: OrderRequest = {
          restaurant_id: branchData.restaurantId, 
          restaurnat_branch_id: branchData.id,    
          customerAddress_id: currentAddress.id,
          deliveryLat: currentAddress.lat,   
          deliveryLng: currentAddress.lng,  
          paymentProviderName: this.selectedPaymentMethod,        
          items: formattedItems
        };

        this.orderService.createOrder(orderPayload, currentUserId).subscribe({
          next: (response: any) => { 
            this.orderId = response.id; 

            if (this.selectedPaymentMethod === 'STRIPE') {
          
              this.redirectToStripe(this.orderId);
            } else {
              alert(`Order Placed Successfully! Order ID: # ${this.orderId} 🎉`);
           this.router.navigate(['/orders', this.orderId, 'tracking']);
              this.cartService.clearCart(); 
            }
          },
          error: (err) => {
            console.error('Failed to create order:', err);
            alert('Error placing order.');
          }
        });
      },
      error: (err) => console.error('Failed to get address:', err)
    });
  }

  redirectToStripe(orderId: number): void {
 
    this.paymentService.createCheckoutSession(orderId, 'STRIPE').subscribe({
      next: (res: any) => {
        if (res.resultUrl) {
       
          window.location.href = res.resultUrl;
        } else {
          alert('Could not generate Stripe payment link.');
        }
      },
      error: (err) => {
        console.error('Stripe session error:', err);
        alert('Payment initiation failed.');
      }
    });
  }
}