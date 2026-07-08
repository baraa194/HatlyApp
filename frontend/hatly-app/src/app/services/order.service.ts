import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';


export interface OrderItemRequest {
  productBranchDetailId: number;
  quantity: number;
}
export interface OrderDeliveryResponse {
  orderId: number;
  orderStatus: string;
  paymentStatus: string;
  providerName: string;
  agentId: number | null;
  agentName: string | null;
  agentPhone: string | null;
  vehicleType: string | null;
  vehicleNumber: string | null;
  lat: number;
  lng: number;
}
export interface OrderRequest {
  restaurant_id: number;
  restaurnat_branch_id: number;
  customerAddress_id: number;   
  deliveryLat: number;
  deliveryLng: number;
  paymentProviderName: 'CASH' | 'STRIPE'; 
  items: OrderItemRequest[];
}


export interface OrderResponse {
  id: number;
  amount: number;
  serviceFee: number;
  deliveryFee: number;
  total: number;
  status: string;
  createdAt: string;

}

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = 'http://localhost:8080/orders/create'; 
  private baseUrl='http://localhost:8080/orders'

  constructor(private http: HttpClient) {}

  createOrder(request: OrderRequest, userId: number): Observable<OrderResponse> {

    const params = new HttpParams().set('userId', userId.toString());
    
    return this.http.post<OrderResponse>(this.apiUrl, request, { params });
  }
  calculateFees(request: OrderRequest, userId: number): Observable<any> {
  const params = new HttpParams().set('userId', userId.toString());
  return this.http.post<any>('http://localhost:8080/orders/calculate-fees', request, { params });
}
getOrderTracking(orderId: number): Observable<OrderDeliveryResponse> {
    return this.http.get<OrderDeliveryResponse>(`${this.baseUrl}/${orderId}/tracking`);
  }
}