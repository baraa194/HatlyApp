import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { ProductBranchResponse } from './restaurant.service'; 


export interface CartItem {
  productBranchDetailId: number;
  name: string;
  description: string;
  price: number;
  imgUrl: string;
  quantity: number;
}

export interface OrderItemDto {
  productBranchDetailId: number;
  quantity: number;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {

  private cartItems: CartItem[] = [];
  
  private cartSubject = new BehaviorSubject<CartItem[]>([]);
  cart$ = this.cartSubject.asObservable();

  addToCart(product: ProductBranchResponse): void {

    const existingItem = this.cartItems.find(
      item => item.productBranchDetailId === product.id
    );
    
    if (existingItem) {
      existingItem.quantity += 1;
    } else {
  
      this.cartItems.push({
        productBranchDetailId: product.id,
        name: product.name,
        description: product.description,
        price: product.price,
        imgUrl: product.imgUrl,
        quantity: 1
      });
    }
    
    this.cartSubject.next([...this.cartItems]);
  }

 
  getItems(): CartItem[] {
    return this.cartItems;
  }

 
  getFormattedOrderItems(): OrderItemDto[] {
    return this.cartItems.map(item => ({
      productBranchDetailId: item.productBranchDetailId,
      quantity: item.quantity
    }));
  }

  clearCart(): void {
    this.cartItems = [];
    this.cartSubject.next([]);
  }
  
}