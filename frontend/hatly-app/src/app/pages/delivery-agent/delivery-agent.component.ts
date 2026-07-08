import { Component, OnInit } from '@angular/core';
import { AgentInfoResponse, DeliveryAgentService } from '../../services/delivery-agent.service';
import { DatePipe } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { Subscription } from 'rxjs';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-delivery-agent',
  templateUrl: './delivery-agent.component.html',
  standalone:true,
  imports:[DatePipe],
  styleUrls: ['./delivery-agent.component.scss']
})
export class DeliveryAgentComponent implements OnInit {
  agentId!: number;
  isOnline: boolean = false;
  currentStatus: string = 'OFFLINE';
  successMessage: string = '';
  agentData: AgentInfoResponse | null = null;
 locationInterval: any;
 currentOrderOffer: any = null;
acceptedOrder: any = null;
captainName: string = '';
 private orderSubscription!: Subscription;


  constructor(private deliveryService: DeliveryAgentService,public authService: AuthService,private sanitizer: DomSanitizer) {}
    get mapUrl(): SafeResourceUrl {
    if (!this.acceptedOrder?.lat || !this.acceptedOrder?.lng) {
      return '';
    }
    const url = `https://maps.google.com/maps?q=${this.acceptedOrder.lat},${this.acceptedOrder.lng}&t=&z=15&ie=UTF8&iwloc=&output=embed`;
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }
  ngOnInit(): void {
    const name = this.authService.getCurrentUsername();
  
  if (name) {
    this.captainName = name;
  
  } else {
    this.captainName = ''; 
  }
  
       const savedUserId = this.authService.getCurrentUserId();
    if (savedUserId ) {
      this.agentId = +savedUserId;
      this.loadAgentProfile();
    }
    this.orderSubscription = this.deliveryService.orderOffers$.subscribe((order) => {
      this.currentOrderOffer = order; 
    });
 
  }

  loadAgentProfile(): void {
    this.deliveryService.getAgentInfo(this.agentId).subscribe({
      next: (data) => {
        this.agentData = data;
      
        this.isOnline = data.isOnline;
        this.currentStatus = data.status;
        if (this.isOnline) {
          this.startLocationTracking();
          this.deliveryService.connectWebSocket(this.agentId);
        }
    
      },
      error: (err) => {
        console.error('Failed to load agent profile:', err);
     
      }
    });
  }

  toggleStatus(event: any): void {
    const targetStatus = event.target.checked;

    this.deliveryService.updateStatus(this.agentId, targetStatus).subscribe({
      next: (response) => {
        this.isOnline = response.isOnline;
        this.currentStatus = response.status;
        this.successMessage = `Status updated to ${this.currentStatus} successfully.`;

        if (this.agentData) {
          this.agentData.isOnline = response.isOnline;
          this.agentData.status = response.status;
        }
        if (this.isOnline) {
          this.startLocationTracking(); 
          this.deliveryService.connectWebSocket(this.agentId);
        } else {
          this.stopLocationTracking();  
          this.deliveryService.disconnectWebSocket(); 
          this.currentOrderOffer = null;
        }
        
        console.log('Backend Response:', response);
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        console.error('Failed to update status:', err);
        this.isOnline = !targetStatus; 
        alert('Could not update status. Please try again.');
      }
    });
  }

  startLocationTracking(): void {
 
    this.stopLocationTracking();

    console.log('Started Live Location Tracking...');
 
    this.sendCurrentLocation();

 
    this.locationInterval = setInterval(() => {
      this.sendCurrentLocation();
    }, 10000); 
  }

 
  stopLocationTracking(): void {
    if (this.locationInterval) {
      clearInterval(this.locationInterval);
      console.log('Stopped Live Location Tracking.');
    }
  }


  sendCurrentLocation(): void {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const latitude = position.coords.latitude;
          const longitude = position.coords.longitude;

          console.log(`Current Location: Lat: ${latitude}, Lng: ${longitude}`);
        
      
          this.deliveryService.updateAgentLocation(this.agentId, {
            lastLat: latitude,
            lastLng: longitude
          }).subscribe({
            next: (res) => console.log('Backend response:', res),
            error: (err) => console.error('Failed to update location on backend', err)
          });
        },
        (error) => {
          console.error('Error getting GPS location:', error);
        },
        { enableHighAccuracy: true } 
      );
    } else {
      console.error('Geolocation is not supported by this browser.');
    }
  }

acceptOrder(): void {
  if (!this.currentOrderOffer) return;

  const orderId = this.currentOrderOffer.orderId; 

  this.deliveryService.handleOrderAction(this.agentId, orderId, 'ACCEPT').subscribe({
    next: (response) => {
      console.log('Backend Response:', response);
      alert('Order Accepted Successfully! 🚀');
      this.acceptedOrder = this.currentOrderOffer;
      
  
  
    },
    error: (err) => {
      console.error('Error accepting order:', err);
      alert('Failed to accept order. It might have been taken by another agent.');
    }
  });
}

rejectOrder(): void {
  if (!this.currentOrderOffer) return;

  const orderId = this.currentOrderOffer.orderId;

  this.deliveryService.handleOrderAction(this.agentId, orderId, 'REJECT').subscribe({
    next: (response) => {
      console.log('Backend Response:', response);
      
      this.currentOrderOffer = null; 
    },
    error: (err) => {
      console.error('Error rejecting order:', err);
    }
  });
}


  
  ngOnDestroy(): void {
       this.deliveryService.disconnectWebSocket();
    if (this.orderSubscription) {
      this.orderSubscription.unsubscribe();
    this.stopLocationTracking();
  }
}
}