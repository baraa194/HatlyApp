import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable,Subject } from 'rxjs';
import { Client } from '@stomp/stompjs';
export interface AgentInfoResponse {
  user_id: number;
  vehicleType: string;
  vehicleNumber: string;
  isOnline: boolean;
  status: string;
  joinedAt: string; 
}

export interface UpdateAgentStatusResponse {
  agentId: number;
  isOnline: boolean;
  status:string;
  message?: string;
}
export interface UpdateLocationRequest {
  lastLat: number;
  lastLng: number;
}
export interface AgentOrderActionRequest {
  action: 'ACCEPT' | 'REJECT'; 
}
@Injectable({
  providedIn: 'root'
})

export class DeliveryAgentService {
    private baseUrl = 'http://localhost:8080/delivery-agents';
    private stompClient!: Client;
    private orderOffersSource = new Subject<any>();
  orderOffers$ = this.orderOffersSource.asObservable();

  constructor(private http: HttpClient) {}
  connectWebSocket(agentId: number): void {

    this.stompClient = new Client({
      brokerURL: 'ws://localhost:8080/ws-connect', 
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.stompClient.onConnect = (frame) => {
      console.log('Connected to WebSocket successfully!');
      
  
      this.stompClient.subscribe(`/queue/agent/${agentId}/orders`, (message) => {
        if (message.body) {
          const newOrder = JSON.parse(message.body);
          console.log('Received Real-time Order Offer:', newOrder);
          
        
          this.orderOffersSource.next(newOrder);
        }
      });
    };

    this.stompClient.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
    };

    this.stompClient.activate();
  }


  disconnectWebSocket(): void {
    if (this.stompClient) {
      this.stompClient.deactivate();
      console.log('Disconnected WebSocket.');
    }
  }
  getAgentInfo(userId: number): Observable<AgentInfoResponse> {
    return this.http.get<AgentInfoResponse>(`${this.baseUrl}/user/${userId}`);
  }


  updateStatus(agentId: number, isOnline: boolean): Observable<UpdateAgentStatusResponse> {
    const params = new HttpParams().set('isOnline', isOnline.toString());
    
    return this.http.put<UpdateAgentStatusResponse>(
      `${this.baseUrl}/${agentId}/status`, 
      {}, 
      { params }
    );
  }
  updateAgentLocation(agentId: number, request: UpdateLocationRequest): Observable<string> {
  return this.http.put(`${this.baseUrl}/${agentId}/location`, request, { responseType: 'text' });

}
handleOrderAction(agentId: number, orderId: number, action: 'ACCEPT' | 'REJECT'): Observable<string> {
  const requestBody: AgentOrderActionRequest = { action };
  

  return this.http.post(`${this.baseUrl}/${agentId}/orders/${orderId}/action`, requestBody, { responseType: 'text' });
}
}