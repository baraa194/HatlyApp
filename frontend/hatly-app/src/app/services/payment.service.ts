import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {

  private baseUrl = 'http://localhost:8080/payments'; 

  constructor(private http: HttpClient) {}

  createCheckoutSession(orderId: number, providerName: string) {
    return this.http.post<{ resultUrl: string }>(`${this.baseUrl}/checkout`, {
      orderId: orderId,
      providerName: providerName
    });
  }
  confirmPaymentInDb(orderId: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/success`, {
      params: { id: orderId.toString() },
      responseType: 'text' as 'json' 
    });
  }
}
